package com.losaxa.core.autoservice;

import com.losaxa.core.mongo.BaseMongoRepository;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * service 组件工厂
 *
 * @param <E>
 * @param <R>
 */
public class BaseServiceFactory<E, R extends BaseMongoRepository<E, Serializable>> {

    private BeanFactory beanFactory;

    public BaseServiceFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected Object getTargetService(R repository,
                                      Class<E> entityClass) {
        return new BaseServiceImpl<>(repository, entityClass);
    }

    public <S> S getService(Class<S> serviceInterface) {
        StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method,
                                   Class<?> targetClass) {
                return !method.isDefault();
            }

            @Override
            public ClassFilter getClassFilter() {
                return BaseService.class::isAssignableFrom;
            }
        };
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Nullable
            @Override
            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                return invocation.proceed();
            }
        };
        Advisor advisor = new DefaultPointcutAdvisor(pointcut, interceptor);
        List<TypeInformation<?>> arguments = ClassTypeInformation.from(serviceInterface)
                .getRequiredSuperTypeInformation(BaseService.class)
                .getTypeArguments();
        TypeInformation<?> entityType     = arguments.get(0);
        TypeInformation<?> repositoryType = arguments.get(2);
        R                  repository     = (R) beanFactory.getBean(repositoryType.getType());
        Object             target         = getTargetService(repository, (Class<E>) entityType.getType());
        ProxyFactory       result         = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(serviceInterface);
        if (DefaultMethodInvokingMethodInterceptor.hasDefaultMethods(serviceInterface)) {
            result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
        }
        result.addAdvisor(advisor);
        S proxy = (S) result.getProxy();
        return proxy;
    }

}
