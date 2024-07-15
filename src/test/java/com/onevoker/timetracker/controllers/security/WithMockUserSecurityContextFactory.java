package com.onevoker.timetracker.controllers.security;

import com.onevoker.timetracker.controllers.security.annotations.WithDefaultUser;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipalAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithDefaultUser> {
    @Override
    public SecurityContext createSecurityContext(WithDefaultUser annotation) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.authorities())
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(annotation.userId())
                .username(annotation.username())
                .password(annotation.password())
                .authorities(authorities)
                .build();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UserPrincipalAuthenticationToken(principal));
        return context;
    }
}
