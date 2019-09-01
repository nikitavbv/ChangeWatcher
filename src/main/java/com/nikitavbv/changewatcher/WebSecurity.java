package com.nikitavbv.changewatcher;

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
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class WebSecurity extends WebSecurityConfigurerAdapter {

  /** Configure security for api routes. */
  @Override
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  protected void configure(final HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .oauth2Login();
  }
}
