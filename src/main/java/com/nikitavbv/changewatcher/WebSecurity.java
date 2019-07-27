package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.security.JwtAuthenticationFilter;
import com.nikitavbv.changewatcher.security.JwtAuthorizationFilter;
import com.nikitavbv.changewatcher.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * API requests security configuration.
 *
 * @author Nikita Volobuev
 */
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  /** Service which provides user data. */
  private final UserDetailsServiceImpl userDetails;
  /** Service to hash passwords. */
  private final BCryptPasswordEncoder passwordEncoder;
  /** Security configuration. */
  private final SecurityProperties security;

  /**
   * Creates WebSecurity.
   *
   * @param userDetails required to set password encoder
   * @param passwordEncoder password encoder to use for user passwords
   * @param security security configuration loaded from
   *                           application.properties.
   */
  public WebSecurity(final UserDetailsServiceImpl userDetails,
                     final BCryptPasswordEncoder passwordEncoder,
                     final SecurityProperties security) {
    this.userDetails = userDetails;
    this.passwordEncoder = passwordEncoder;
    this.security = security;
  }

  /** Configure security for api routes. */
  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  protected void configure(final HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
          .antMatchers(HttpMethod.GET, RouteConstants.INIT_API).permitAll()
          .antMatchers(RouteConstants.USERS_API).permitAll()
          .antMatchers(RouteConstants.LOGIN_API).permitAll()
          .antMatchers(RouteConstants.PREVIEW_API).permitAll()
          .antMatchers(RouteConstants.JOBS_API).permitAll()
          .antMatchers(RouteConstants.API_PATH_PATTERN).authenticated()
          .and()
          .addFilter(new JwtAuthorizationFilter(authenticationManager(), security))
          .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), security),
              UsernamePasswordAuthenticationFilter.class);
  }

  /** Sets auth configuration. */
  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder);
  }

}
