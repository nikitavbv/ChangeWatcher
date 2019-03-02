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

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private UserDetailsServiceImpl userDetailsService;
  private BCryptPasswordEncoder bcryptPasswordEncoder;
  private SecurityProperties securityProperties;

  /**
   * Creates WebSecurity.
   *
   * @param userDetailsService required to set password encoder
   * @param bcryptPasswordEncoder password encoder to use for user passwords
   * @param securityProperties security configuration loaded from application.properties.
   */
  public WebSecurity(UserDetailsServiceImpl userDetailsService,
                     BCryptPasswordEncoder bcryptPasswordEncoder,
                     SecurityProperties securityProperties) {
    this.userDetailsService = userDetailsService;
    this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    this.securityProperties = securityProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
          .antMatchers(HttpMethod.GET, RouteConstants.INIT_API).permitAll()
          .antMatchers(RouteConstants.USERS_API).permitAll()
          .antMatchers(RouteConstants.LOGIN_API).permitAll()
          .antMatchers(RouteConstants.PREVIEW_API).permitAll()
          .antMatchers(RouteConstants.JOBS_API).permitAll()
          .antMatchers(RouteConstants.API_PATH_PATTERN).authenticated()
          .and()
          .addFilter(new JwtAuthorizationFilter(authenticationManager(), securityProperties))
          .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), securityProperties),
              UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bcryptPasswordEncoder);
  }

}
