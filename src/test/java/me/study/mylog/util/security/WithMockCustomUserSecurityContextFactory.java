package me.study.mylog.util.security;

import me.study.mylog.auth.security.UserPrincipal;
import me.study.mylog.users.domain.RoleType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUserPrincipal) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserPrincipal principal = UserPrincipal.builder()
                .id(customUserPrincipal.id())
                .email(customUserPrincipal.email())
                .build();

        var authorities = Collections.
                singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode()));

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, "password", authorities);
        context.setAuthentication(auth);
        return context;
    }
}
