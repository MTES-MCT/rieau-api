package com.github.mtesmct.rieau.api.depositaire.infra.http;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithUserDetails;

@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(value = "jacques.dupont", userDetailsServiceBeanName="userDetailsService")
public @interface WithInstructeurNonBetaDetails {
}