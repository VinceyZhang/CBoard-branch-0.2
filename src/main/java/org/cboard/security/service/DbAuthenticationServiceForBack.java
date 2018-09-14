package org.cboard.security.service;

import org.cboard.dao.UserDao;
import org.cboard.dto.User;
import org.cboard.services.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by yfyuan on 2016/12/14.  设置免登录
 */
public class DbAuthenticationServiceForBack implements AuthenticationService {

    @Override
    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object us = authentication.getPrincipal();
        User user = null;
        if (us instanceof java.lang.String) {
           Collection<GrantedAuthority> authoritySet= (Collection<GrantedAuthority>) authentication.getAuthorities();
            user = new User(us.toString(), "root123", authoritySet);
            user.setUserId("1");
        } else {
            user = (User) authentication.getPrincipal();
        }

        if (user == null) {
            return null;
        }
        return user;
    }

}
