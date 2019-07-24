package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.security.PermissionDeniedException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to create users and get information about them.
 *
 * @author Nikita Volobuev
 */
@RestController
@RequestMapping(RouteConstants.USERS_API)
public class UserController {

  /** ApplicationUserRepository to get user details. */
  private final ApplicationUserRepository userRepository;
  /** BCryptPasswordEncoder to hash user passwords. */
  private final BCryptPasswordEncoder passwordEncoder;

  /** Creates UserController. */
  public UserController(ApplicationUserRepository userRepository,
                        BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /** Get currently authenticated user information. */
  @GetMapping
  public ApplicationUser getUserInfo(HttpServletRequest httpRequest) {
    return userRepository.findByUsername(httpRequest.getRemoteUser());
  }

  /**
   * Signs up user.
   *
   * @param user user to create
   * @throws PermissionDeniedException if sign up by this user is not allowed
   * @return user id
   */
  @PostMapping
  public SignUpResult signUp(HttpServletRequest request,
                     @RequestBody ApplicationUser user) {
    // User can be an admin if one of the following is true:
    // - There are no users registered yet and new one is the first.
    // - This request is performed by admin user.
    if (request.getRemoteUser() == null && userRepository.count() != 0L) {
      throw new PermissionDeniedException("Auth required for creating users");
    }
    ApplicationUser requestUser = userRepository.findByUsername(request.getRemoteUser());
    if (requestUser != null && !requestUser.isAdmin() && requestUser.isAdmin()) {
      throw new PermissionDeniedException("Non-admin users are not allowed to create admin users");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return new SignUpResult(user.getUserID());
  }
}
