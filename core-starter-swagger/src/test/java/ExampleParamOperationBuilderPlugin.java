//package com.losaxa.framework.core.swag;
//
//import com.fasterxml.classmate.TypeResolver;
//import com.losaxa.framework.core.common.CollectionUtil;
//import com.losaxa.framework.core.common.ReflectUtil;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import javassist.CannotCompileException;
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.CtField;
//import javassist.NotFoundException;
//import javassist.bytecode.AnnotationsAttribute;
//import javassist.bytecode.ConstPool;
//import javassist.bytecode.SignatureAttribute;
//import javassist.bytecode.annotation.BooleanMemberValue;
//import javassist.bytecode.annotation.StringMemberValue;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.validation.annotation.Validated;
//import springfox.documentation.service.ParameterStyle;
//import springfox.documentation.service.ParameterType;
//import springfox.documentation.service.ResolvedMethodParameter;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.ParameterBuilderPlugin;
//import springfox.documentation.spi.service.contexts.ParameterContext;
//
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import javax.validation.groups.Default;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * 放弃了(有点麻烦)
// */
//@Slf4j
//@Component
//public class ExampleParamOperationBuilderPlugin implements ParameterBuilderPlugin {
//
//    @Autowired
//    private TypeResolver typeResolver;
//
//    //query -> ExpandedParameterNotBlankAnnotationPlugin
//    //body -> NotBlankAnnotationPlugin
//    @SneakyThrows
//    @Override
//    public void apply(ParameterContext parameterContext) {
//        ResolvedMethodParameter methodParameter = parameterContext.resolvedMethodParameter();
//        Class<?>                originClass     = parameterContext.resolvedMethodParameter().getParameterType().getErasedType();
//        Optional<Validated>     validatedOp     = methodParameter.findAnnotation(Validated.class);
//        if (!validatedOp.isPresent()) {
//            return;
//        }
//        Class<?>[]  group         = validatedOp.get().value();
//        boolean     hsaGroup      = !CollectionUtil.isEmpty(group);
//        List<Field> requiredField = new ArrayList<>();
//        List<Field> otherField    = new ArrayList<>();
//        ReflectionUtils.doWithFields(originClass, field -> {
//            List<Class<?>> fieldGroup = new LinkedList<>();
//            boolean        hasNot     = false;
//            for (Annotation e : field.getAnnotations()) {
//                if (!(e instanceof NotBlank) && !(e instanceof NotEmpty) && !(e instanceof NotNull)) {
//                    continue;
//                }
//                hasNot = true;
//                try {
//                    Class<?>[] groups = ReflectUtil.getAnnotationValue(e, "groups");
//                    fieldGroup.addAll(Arrays.asList(groups));
//                } catch (NoSuchFieldException ex) {
//                    log.warn(ex.getMessage(), ex);
//                }
//            }
//            if (!hasNot) {
//                otherField.add(field);
//                return;
//            }
//            boolean fieldHsaGroup = !CollectionUtil.isEmpty(fieldGroup);
//            boolean fieldHasDefaultGroup = fieldGroup.stream().anyMatch(
//                    e -> e.equals(Default.class));
//
//            boolean fieldIsDefaultGroup = !fieldHsaGroup || fieldHasDefaultGroup;
//            if (!hsaGroup && fieldIsDefaultGroup) {
//                requiredField.add(field);
//                return;
//            }
//            boolean groupHasDefault = Arrays.stream(group).flatMap(
//                    e -> Arrays.stream(e.getInterfaces())).anyMatch(
//                    e -> e.equals(Default.class));
//
//            boolean hasEqualGroup = Arrays.stream(group).anyMatch(fieldGroup::contains);
//
//            if (hsaGroup && !groupHasDefault && hasEqualGroup) {
//                requiredField.add(field);
//                return;
//            }
//
//            // Validated 有 继承 Default 的 group && Not~注解是 Default group || 具有相同 group
//            if (hsaGroup && groupHasDefault && (fieldIsDefaultGroup || hasEqualGroup)) {
//                requiredField.add(field);
//                return;
//            }
//
//            otherField.add(field);
//        });
//        ClassPool classPool = ClassPool.getDefault();
//
//        String suffix;
//        if (hsaGroup) {
//            suffix = Arrays.stream(group).map(Class::getSimpleName).collect(Collectors.joining());
//        } else {
//            suffix = "Default";
//        }
//
//        CtClass newClass = classPool.makeClass(originClass.getName() + "$$" + suffix);
//        for (Field field : requiredField) {
//            newField(newClass, field, true);
//        }
//        for (Field field : otherField) {
//            newField(newClass, field, false);
//        }
//
//        newClassAnntations(originClass, newClass);
//        log.info(newClass.toString());
//        parameterContext.getDocumentationContext().getAdditionalModels().add(typeResolver.resolve(newClass.toClass()));
//        parameterContext.requestParameterBuilder().in(ParameterType.BODY)
//                .name("user").query(q -> q.style(ParameterStyle.DEEPOBJECT)
//                .model(m -> m.referenceModel(ref -> ref.key(k -> k.qualifiedModelName(qualified ->
//                        qualified.namespace(newClass.getPackageName())
//                                .name(newClass.getSimpleName()))))));
//    }
//
//    private void newClassAnntations(Class<?> originClass, CtClass newClass) {
//        ApiModel                                 oldApiModel          = originClass.getAnnotation(ApiModel.class);
//        String                                   description          = Optional.ofNullable(oldApiModel).map(ApiModel::description).orElse("");
//        ConstPool                                constPool            = newClass.getClassFile().getConstPool();
//        AnnotationsAttribute                     annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
//        javassist.bytecode.annotation.Annotation newApiModel          = new javassist.bytecode.annotation.Annotation(ApiModel.class.getName(), constPool);
//        newApiModel.addMemberValue("description", new StringMemberValue(description, constPool));
//        annotationsAttribute.addAnnotation(newApiModel);
//        newClass.getClassFile().addAttribute(annotationsAttribute);
//    }
//
//    private void newField(CtClass newClass, Field field, boolean required) throws CannotCompileException, NotFoundException {
//        CtField newField = new CtField(ClassPool.getDefault().get(field.getType().getName()), field.getName(), newClass);
//        newField.setModifiers(Modifier.PUBLIC);
//        if (Arrays.stream(field.getType().getInterfaces()).anyMatch(Collection.class::isAssignableFrom)) {
//            Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
//            //todo:luosx:泛型
//            if (!CollectionUtil.isEmpty(actualTypeArguments)) {
//                SignatureAttribute.ClassSignature genericSignature = new SignatureAttribute.ClassSignature(null, null,
//                        new SignatureAttribute.ClassType[]{new SignatureAttribute.ClassType(
//                                "java.util.Collection",
//                                new SignatureAttribute.TypeArgument[]{new SignatureAttribute.TypeArgument(
//                                        new SignatureAttribute.ClassType(actualTypeArguments[0].getTypeName()))}
//                        )});
//                newField.setGenericSignature(genericSignature.encode());
//            }
//        }
//        ApiModelProperty                         oldApiModelProperty  = field.getAnnotation(ApiModelProperty.class);
//        String                                   value                = Optional.ofNullable(oldApiModelProperty).map(ApiModelProperty::value).orElse("");
//        ConstPool                                constPool            = newClass.getClassFile().getConstPool();
//        AnnotationsAttribute                     annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
//        javassist.bytecode.annotation.Annotation newApiModelProperty  = new javassist.bytecode.annotation.Annotation(ApiModelProperty.class.getName(), constPool);
//        newApiModelProperty.addMemberValue("value", new StringMemberValue(value, constPool));
//        if (required) {
//            newApiModelProperty.addMemberValue("required", new BooleanMemberValue(true, constPool));
//        }
//        annotationsAttribute.addAnnotation(newApiModelProperty);
//        newField.getFieldInfo().addAttribute(annotationsAttribute);
//        newClass.addField(newField);
//    }
//
//    @Override
//    public boolean supports(DocumentationType documentationType) {
//        return true;
//    }
//}
