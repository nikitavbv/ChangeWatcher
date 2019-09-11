package com.nikitavbv.changewatcher;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * API requests security configuration.
 *
 * @author Nikita Volobuev
 */
@Configuration
@EnableOAuth2Sso
public class WebSecurity extends WebSecurityConfigurerAdapter {

  /** Configure security for api routes. */
  @Override
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  protected void configure(final HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest().authenticated();
  }
}
