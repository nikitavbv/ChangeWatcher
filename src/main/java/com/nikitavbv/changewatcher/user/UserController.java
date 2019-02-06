package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.security.PermissionDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(RouteConstants.USERS_API)
public class UserController {

  private ApplicationUserRepository applicationUserRepository;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserController(ApplicationUserRepository applicationUserRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.applicationUserRepository = applicationUserRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @GetMapping
  public ApplicationUser getUserInfo(HttpServletRequest httpRequest) {
    return applicationUserRepository.findByUsername(httpRequest.getRemoteUser());
  }

  @PostMapping
  public SignUpResult signUp(HttpServletRequest request,
                     @RequestBody ApplicationUser user) {
    if (request.getRemoteUser() == null && applicationUserRepository.count() != 0L) {
      throw new PermissionDeniedException("Auth required for creating users");
    }
    ApplicationUser requestUser = applicationUserRepository.findByUsername(request.getRemoteUser());
    if (!requestUser.getIsAdmin() && requestUser.getIsAdmin()) {
      throw new PermissionDeniedException("Non-admin users are not allowed to create admin users");
    }
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    applicationUserRepository.save(user);
    return new SignUpResult(user.getId());
  }
}
