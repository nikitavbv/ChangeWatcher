package com.nikitavbv.changewatcher.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikitavbv.changewatcher.SecurityProperties;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.nikitavbv.changewatcher.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private AuthenticationManager authenticationManager;
  private SecurityProperties securityProperties;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                 SecurityProperties securityProperties) {
    this.authenticationManager = authenticationManager;
    this.securityProperties = securityProperties;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req,
                                              HttpServletResponse res) throws AuthenticationException {
    try {
      ApplicationUser creds = new ObjectMapper()
          .readValue(req.getInputStream(), ApplicationUser.class);

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              creds.getUsername(),
              creds.getPassword(),
              new ArrayList<>())
      );
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
                                          HttpServletResponse res,
                                          FilterChain chain,
                                          Authentication auth) throws IOException, ServletException {
    String secret = securityProperties.getSecret();
    if (secret == null) {
      securityProperties.setSecret(securityProperties.generateSecret());
      secret = securityProperties.getSecret();
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
