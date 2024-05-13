package com.losaxa.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 登录用户上下文
 */
public class UserContextHolder {

    public static Long getId() {
        LoginUser user = getUser();
        if (user == null) return null;
        return user.getId();
    }

    public static LoginUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        return null;
    }

    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

}
