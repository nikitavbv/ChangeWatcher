package com.nikitavbv.changewatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

  @Autowired
  private ApplicationUserDetailsService applicationUserDetailsService;

  @Autowired
  private ApplicationUserOAuth2Service oAuth2Service;

  @Autowired
  private OAuth2AuthenticationSuccessHandler successHandler;

  @Autowired
  private OAuth2AuthenticationFailureHandler failureHandler;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
    authenticationManagerBuilder.userDetailsService(applicationUserDetailsService);
  }

  /** Configure security for api routes. */
  @Override
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  protected void configure(final HttpSecurity http) throws Exception {
    http.cors()
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().formLogin().disable()
        .httpBasic().disable()
        .authorizeRequests()
          .antMatchers(RouteConstants.OAUTH_PATTERN).permitAll()
          .anyRequest().authenticated()
          .and()
        .oauth2Login()
          .authorizationEndpoint()
            .baseUri(RouteConstants.OAUTH_PREFIX + "authorize")
            .authorizationRequestRepository(cookieAuthorizationRequestRepository())
            .and()
          .redirectionEndpoint()
            .baseUri(RouteConstants.OAUTH_PREFIX + "callback")
            .and()
          .userInfoEndpoint()
            .userService(applicationUserDetailsService)
            .and()
          .successHandler(successHandler)
          .failureHandler(failureHandler);

    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
