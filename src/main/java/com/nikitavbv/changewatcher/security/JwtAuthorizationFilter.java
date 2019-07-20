package com.nikitavbv.changewatcher.security;

import static com.nikitavbv.changewatcher.security.SecurityConstants.HEADER_STRING;
import static com.nikitavbv.changewatcher.security.SecurityConstants.TOKEN_PREFIX;

import com.nikitavbv.changewatcher.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Checks jwt tokens to be valid.
 *
 * @author Nikita Volobuev
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  /** Security properties to validate tokens with application secret. */
  private final SecurityProperties securityProperties;

  /** Creates JwtAuthorizationFilter. */
  public JwtAuthorizationFilter(AuthenticationManager authManager,
                                SecurityProperties securityProperties) {
    super(authManager);
    this.securityProperties = securityProperties;
  }

  /** Update authentication with token provided by user. */
  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(req, res);
  }

  /** Performs authentication with token provided in request header.ii */
  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null) {
      String secret = securityProperties.getSecret();
      if (secret == null) {
        securityProperties.setSecret(securityProperties.generateSecret());
        secret = securityProperties.getSecret();
      }

      try {
        String user = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
            .getBody()
            .getSubject();

        if (user != null) {
          return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
      } catch (SignatureException e) {
        // ignore (token is invalid)
        return null;
      }
      return null;
    }
    return null;
  }
}
