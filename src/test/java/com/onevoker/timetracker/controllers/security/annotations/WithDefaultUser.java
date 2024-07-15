package com.onevoker.timetracker.controllers.security.annotations;

import com.onevoker.timetracker.controllers.security.WithMockUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithDefaultUser {
    String username() default "simpleMan";
    int userId() default 1;
    String password() default "1111";
    String[] authorities() default "ROLE_User";
}
