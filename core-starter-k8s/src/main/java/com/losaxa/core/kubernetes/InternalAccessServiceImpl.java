package com.losaxa.core.kubernetes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.kubernetes.fabric8.discovery.KubernetesDiscoveryClient;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 内部服务访问认证
 */
public class InternalAccessServiceImpl implements InternalAccessService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KubernetesDiscoveryClient discoveryClient;

    public InternalAccessServiceImpl(KubernetesDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 如果是内部服务访问接口可以直接通过,无需认证鉴权
     *
     * @param request
     * @param authentication
     * @return
     */
    @Override
    public boolean isInternal(HttpServletRequest request,
                              Authentication authentication) {
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return false;
        }
        AnonymousAuthenticationToken anonymous     = (AnonymousAuthenticationToken) authentication;
        WebAuthenticationDetails     details       = (WebAuthenticationDetails) anonymous.getDetails();
        String                       remoteAddress = details.getRemoteAddress();
        log.info("request ip {}", remoteAddress);
        if ("127.0.0.1".equals(remoteAddress)) {
            log.info("internal server {}", "127.0.0.1");
            return true;
        }
        return discoveryClient.getServices().stream().map(discoveryClient::getInstances).flatMap(Collection::stream).anyMatch(e -> {
            String host = e.getHost();
            log.info("internal server {} {}", e.getServiceId(), host);
            return host.equals(remoteAddress);
        });
    }
}
