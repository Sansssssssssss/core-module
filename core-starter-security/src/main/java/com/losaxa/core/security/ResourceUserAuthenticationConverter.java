package com.losaxa.core.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 资源服务授权转器
 */
public class ResourceUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    /**
     * 保存 LoginUser 信息,方便从上下文获取
     * @param map
     * @return
     */
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            Object                                 principal;
            //如果包含 loginUser key 则保存 LoginUser 对象,配合 com.losaxa.core.security.UserContextHolder 获取用户信息
            if (map.containsKey(LoginUser.LOGIN_USER)) {
                Map<String, Object> userInfo = (Map<String, Object>) map.get(LoginUser.LOGIN_USER);
                principal = new LoginUser(map.get(USERNAME).toString(), "N/A",
                        authorities, (long) userInfo.get(LoginUser.ID), (int) userInfo.get(LoginUser.USER_TYPE));
            } else {
                principal = map.get(USERNAME);
            }
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return Collections.emptyList();
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }

}
