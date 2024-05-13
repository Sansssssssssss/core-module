package com.losaxa.core.hystrix;

import com.google.auto.service.AutoService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sun.tools.javac.tree.JCTree.*;

/**
 * 根据 @HystrixFallback 自动生成 Hystrix Fallback 方法
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.losaxa.core.hystrix.HystrixFallback")
public class HystrixFallbackProcessor extends AbstractProcessor {

    private JavacTrees trees;
    private TreeMaker  treeMaker;
    private Names      names;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        //获取有 @HystrixFallback 注解的所有类
        Set<? extends Element> hasHystrixFallbackElement = roundEnv.getElementsAnnotatedWith(HystrixFallback.class);
        for (Element classElement : hasHystrixFallbackElement) {
            if (!ElementKind.CLASS.equals(classElement.getKind())) {
                //如果 @HystrixFallback 不是在类上 , 则不处理
                continue;
            }
            //获取类的所有方法Element
            Map<String, ? extends Element> allMethodElement = classElement.getEnclosedElements().stream().filter(e ->
                    ElementKind.METHOD.equals(e.getKind())).collect(Collectors.toMap(e -> e.getSimpleName() + "", e -> e));
            //过滤出所有需要生成fallback的RequestMethod
            List<? extends Element> needGenerateMethodElement = allMethodElement.values().stream().filter(e -> {
                if (e.getAnnotation(RequestMapping.class) == null &&
                        e.getAnnotation(PostMapping.class) == null &&
                        e.getAnnotation(GetMapping.class) == null &&
                        e.getAnnotation(DeleteMapping.class) == null &&
                        e.getAnnotation(PatchMapping.class) == null) {
                    return false;
                }
                HystrixCommand hystrixCommand = e.getAnnotation(HystrixCommand.class);
                if (hystrixCommand != null) {
                    return !allMethodElement.containsKey(hystrixCommand.fallbackMethod());
                }
                return true;
            }).collect(Collectors.toList());
            if (needGenerateMethodElement.isEmpty()) {
                continue;
            }
            //获取语法树的路径
            TreePath treePath = this.trees.getPath(classElement);
            //获取路径的编译单元
            JCCompilationUnit jcCompilationUnit = (JCCompilationUnit) treePath.getCompilationUnit();
            //获取类的导包
            com.sun.tools.javac.util.List<JCImport> imports = jcCompilationUnit.getImports();
            //是否已经导入HystrixCommand
            //todo:luosx: 判断日志包和return包是否导入
            boolean hystrixCommandPkgExist = imports.stream().anyMatch(e ->
                    e.getQualifiedIdentifier().toString().equals("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand"));
            if (!hystrixCommandPkgExist) {
                //导入HystrixCommand包
                jcCompilationUnit.defs = jcCompilationUnit.defs.prepend(
                        treeMaker.Import(treeMaker.Select(
                                treeMaker.Ident(names.fromString("com.netflix.hystrix.contrib.javanica.annotation")),
                                names.fromString("HystrixCommand")), false));
            }
            for (Element methodElement : needGenerateMethodElement) {
                //是否已经存在 HystrixCommand 注解
                HystrixCommand hystrixCommand = methodElement.getAnnotation(HystrixCommand.class);
                if (hystrixCommand == null) {
                    String genFallbackName = methodElement.getSimpleName() + "Fallback";
                    trees.getTree(methodElement).accept(new TreeTranslator(){
                        @Override
                        public void visitMethodDef(JCMethodDecl jcMethodDecl) {
                            //添加 HystrixCommand 注解
                            JCAnnotation jcAnnotation = treeMaker.Annotation(
                                    treeMaker.Ident(
                                            names.fromString("HystrixCommand")),
                                    com.sun.tools.javac.util.List.of(treeMaker.Assign(
                                            treeMaker.Ident(names.fromString("fallbackMethod")),
                                            treeMaker.Literal(genFallbackName))));
                            jcMethodDecl.mods.annotations = jcMethodDecl.getModifiers().getAnnotations().append(jcAnnotation);
                        }
                    });
                    genFallback(classElement, methodElement, genFallbackName);
                } else {
                    if (hystrixCommand.fallbackMethod().length() == 0) {
                        continue;
                    }
                    genFallback(classElement, methodElement, hystrixCommand.fallbackMethod());
                }
            }
        }
        return false;
    }


    private void genFallback(Element classElement, Element methodElement, String genFallbackName) {
        //访问者模式,访问方法Element
        trees.getTree(methodElement).accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCMethodDecl jcMethodDecl) {
                //方法名称
                Name name = names.fromString(genFallbackName);
                //方法参数列表
                com.sun.tools.javac.util.List<JCVariableDecl> parameters = com.sun.tools.javac.util.List.nil();
                for (JCVariableDecl parameter : jcMethodDecl.getParameters()) {
                    JCVariableDecl param = treeMaker.VarDef(parameter.getModifiers(), //访问标志
                            parameter.name, //名字
                            parameter.vartype, //类型
                            parameter.nameexpr); //初始化语句
                    parameters = parameters.append(param);
                }
                //方法体->日志输出方法的参数
                com.sun.tools.javac.util.List<JCExpression> logArgs = com.sun.tools.javac.util.List.nil();
                //方法体->日志输出内容
                JCExpression logWarnExpr = treeMaker.Binary(Tag.PLUS,
                        treeMaker.Literal(classElement.getSimpleName() + ": " + methodElement.getSimpleName() + " fallback "),
                        treeMaker.Literal(", "));
                //方法体->日志输出参数
                for (JCVariableDecl parameter : parameters) {
                    logWarnExpr = treeMaker.Binary(Tag.PLUS, logWarnExpr, treeMaker.Literal(parameter.name + " = {}, "));
                    logArgs = logArgs.append(treeMaker.Ident(parameter.name));
                }
                //异常参数
                JCVariableDecl throwParam = treeMaker.VarDef(
                        treeMaker.Modifiers(Flags.PARAMETER),
                        names.fromString("e"),
                        treeMaker.Ident(names.fromString("Throwable")),
                        null);
                //添加异常参数
                parameters = parameters.append(throwParam);
                //日志输出异常
                logWarnExpr = treeMaker.Binary(Tag.PLUS, logWarnExpr, treeMaker.Literal("err = {}"));
                //日志输出异常参数
                logArgs = logArgs.append(treeMaker.Apply(
                        com.sun.tools.javac.util.List.nil(),
                        treeMaker.Select(
                                treeMaker.Ident(names.fromString("e")),
                                names.fromString("getMessage")),
                        com.sun.tools.javac.util.List.nil()));
                logArgs = logArgs.prepend(logWarnExpr);
                ListBuffer<JCStatement> statements = new ListBuffer<>();
                //fallback 执行 log 语句
                statements.append(treeMaker.Exec(
                        treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                                treeMaker.Select(treeMaker.Ident(names.fromString("log")),
                                        names.fromString("warn")), logArgs)));
                //fallback 执行 return 语句
                statements.append(treeMaker.Return(
                        treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                                treeMaker.Select(treeMaker.Ident(names.fromString(jcMethodDecl.getReturnType().type.tsym.name.toString())),
                                        names.fromString("fallback")), com.sun.tools.javac.util.List.nil())));
                JCBlock body = treeMaker.Block(0L, statements.toList());
                JCMethodDecl genFallback = treeMaker.MethodDef( //声明一个方法
                        treeMaker.Modifiers(Flags.PUBLIC),//访问标志
                        name, //方法名
                        (JCExpression) jcMethodDecl.getReturnType(), //返回类型
                        com.sun.tools.javac.util.List.nil(), //泛型形参列表
                        parameters, //参数列表
                        com.sun.tools.javac.util.List.nil(),  //异常列表
                        body, //方法体
                        null);  //默认方法（应该是interface中的default方法）

                //两种访问tree的方式
                JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
                JCClassDecl   classDecl    = (JCClassDecl) elementUtils.getTree(classElement);
                treeMaker.at(classDecl.pos);
                classDecl.defs = classDecl.defs.append(genFallback);
                //访问者模式
                //trees.getTree(classElement).accept(new TreeTranslator() {
                //    @Override
                //    public void visitClassDef(JCClassDecl jcClassDecl) {
                //        jcClassDecl.defs = jcClassDecl.defs.append();
                //    }
                //});
            }
        });
    }
}
