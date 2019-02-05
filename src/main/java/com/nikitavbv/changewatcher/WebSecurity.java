package com.nikitavbv.changewatcher;

import com.nikitavbv.changewatcher.security.JWTAuthenticationFilter;
import com.nikitavbv.changewatcher.security.JWTAuthorizationFilter;
import com.nikitavbv.changewatcher.security.UserDetailsServiceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class WebSecurity extends WebSecurityConfigurerAdapter {

  private UserDetailsServiceImpl userDetailsService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private SecurityProperties securityProperties;

  public WebSecurity(UserDetailsServiceImpl userDetailsService,
                     BCryptPasswordEncoder bCryptPasswordEncoder,
                     SecurityProperties securityProperties) {
    this.userDetailsService = userDetailsService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.securityProperties = securityProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
          .antMatchers(HttpMethod.POST, RouteConstants.USERS_API).permitAll()
          .antMatchers(HttpMethod.POST, RouteConstants.LOGIN_API).permitAll()
          .antMatchers(RouteConstants.API_PATH_PATTERN).authenticated()
          .and()
          .addFilter(new JWTAuthenticationFilter(authenticationManager(), securityProperties))
          .addFilterBefore(new JWTAuthorizationFilter(authenticationManager(), securityProperties),
              UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

}
