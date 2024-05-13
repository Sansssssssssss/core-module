package com.losaxa.core.kubernetes;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface InternalAccessService {

    boolean isInternal(HttpServletRequest request, Authentication authentication);

}
