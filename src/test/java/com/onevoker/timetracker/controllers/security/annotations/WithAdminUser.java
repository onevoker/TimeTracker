package com.onevoker.timetracker.controllers.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithDefaultUser(authorities = {"ROLE_User", "ROLE_Admin"})
public @interface WithAdminUser {
}
