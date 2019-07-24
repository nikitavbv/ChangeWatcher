package com.nikitavbv.changewatcher.security;

import static com.nikitavbv.changewatcher.security.SecurityConstants.EXPIRATION_TIME;
import static com.nikitavbv.changewatcher.security.SecurityConstants.HEADER_STRING;
import static com.nikitavbv.changewatcher.security.SecurityConstants.TOKEN_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.SecurityProperties;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Checks user credentials and generates auth tokens.
 *
 * @author Nikita Volobuev
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  /** AuthenticationManager to set user auth to username/password. */
  private final AuthenticationManager authManager;
  /** SecurityProperties to get application secret used for session tokens. */
  private final SecurityProperties properties;

  /**
   * Creates JWTAuthentication filter.
   *
   * @param authManager for user authentication
   * @param properties configuration for security tokens
   */
  public JwtAuthenticationFilter(AuthenticationManager authManager,
                                 SecurityProperties properties) {
    super(new AntPathRequestMatcher(RouteConstants.LOGIN_API, "POST"));
    this.authManager = authManager;
    this.properties = properties;
  }

  /** Authenticate user with provided username/password. */
  @Override
  public Authentication attemptAuthentication(
          HttpServletRequest req,
          HttpServletResponse res
  ) throws AuthenticationException {
    try {
      ApplicationUser creds = new ObjectMapper()
          .readValue(req.getInputStream(), ApplicationUser.class);

      return authManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              creds.getUsername(),
              creds.getPassword(),
              new ArrayList<>())
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * On successful authentication, generate and send new token.
   *
   * <p>Token is sent in Authorization header.</p>
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest req,
                                          HttpServletResponse res,
                                          FilterChain chain,
                                          Authentication auth) {
    String secret = properties.getSecret();
    if (secret == null) {
      properties.setSecret(properties.generateSecret());
      secret = properties.getSecret();
    }

    byte[] keyBytes = Decoders.BASE64.decode(secret);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    String token = Jwts.builder()
        .setSubject(((User) auth.getPrincipal()).getUsername())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key)
        .compact();

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }
}
