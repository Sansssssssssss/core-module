package com.losaxa.core.kubernetes.config;

import com.losaxa.core.kubernetes.InternalAccessService;
import com.losaxa.core.kubernetes.InternalAccessServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.kubernetes.fabric8.discovery.KubernetesDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 有关 K8s 的基础配置
 */
@Configuration
public class K8sConfig {

    /**
     * 实现 k8s 内部的服务相互访问无需认证授权
     * @param discoveryClient
     * @return
     */
    @ConditionalOnMissingBean(InternalAccessService.class)
    @Bean
    public InternalAccessService internalAccessService(KubernetesDiscoveryClient discoveryClient){
        return new InternalAccessServiceImpl(discoveryClient);
    }

}
