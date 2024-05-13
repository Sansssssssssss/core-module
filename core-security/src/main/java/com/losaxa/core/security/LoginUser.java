package com.losaxa.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 登录用户信息
 */
public class LoginUser extends User {

    private static final long serialVersionUID = 1L;

    public final static String ID = "id";
    public final static String USER_TYPE = "userType";
    public final static String LOGIN_USER = "loginUser";
    public final static String PERMISSION = "permission";

    public Long id;

    public int userType;

    public LoginUser(String username,
                     String password,
                     Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

    }

    public LoginUser(String username,
                     String password,
                     Collection<? extends GrantedAuthority> authorities,
                     Long id,
                     int userType) {
        super(username, password, authorities);
        this.id = id;
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
