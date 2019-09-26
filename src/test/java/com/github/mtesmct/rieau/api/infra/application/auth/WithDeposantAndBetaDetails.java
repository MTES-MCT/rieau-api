package com.github.mtesmct.rieau.api.infra.application.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.mtesmct.rieau.api.application.auth.MockUsers;
import com.github.mtesmct.rieau.api.application.auth.Roles;

import org.springframework.security.test.context.support.WithMockUser;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = MockUsers.JEAN_MARTIN, roles={Roles.DEPOSANT,Roles.BETA})
public @interface WithDeposantAndBetaDetails {
    public final static String ID = MockUsers.JEAN_MARTIN;
}