package com.losaxa.core.autoservice;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Service组件提供者
 */
public class ServiceComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public ServiceComponentProvider() {
        super(false);
        super.addIncludeFilter(new InterfaceTypeFilter(BaseService.class));
        //addExcludeFilter(new AnnotationTypeFilter(NoRepositoryBean.class));
    }

    /**
     * 查找Service组件
     * @param basePackage
     * @return
     */
    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
        }
        return candidates;
    }

    /**
     * 是否为Service组件的基类
     * @param beanDefinition
     * @return
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isNonServiceInterface    = !BaseService.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isTopLevelType           = !beanDefinition.getMetadata().hasEnclosingClass();
        return isNonServiceInterface && isTopLevelType;
    }

    /**
     * 类型过滤
     */
    private static class InterfaceTypeFilter extends AssignableTypeFilter {
        public InterfaceTypeFilter(Class<?> targetType) {
            super(targetType);
        }

        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory)
                throws IOException {
            return metadataReader.getClassMetadata().isInterface() && super.match(metadataReader, metadataReaderFactory);
        }
    }
}
