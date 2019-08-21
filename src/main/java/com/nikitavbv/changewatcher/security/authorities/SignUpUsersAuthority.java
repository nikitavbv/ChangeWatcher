package com.nikitavbv.changewatcher.security.authorities;

import org.springframework.security.core.GrantedAuthority;

public class SignUpUsersAuthority implements GrantedAuthority {
  @Override
  public String getAuthority() {
    return "signup_users";
  }
}
