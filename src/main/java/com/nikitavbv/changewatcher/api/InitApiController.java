package com.nikitavbv.changewatcher.api;

import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.SetupRequiredException;
import com.nikitavbv.changewatcher.security.AuthRequiredException;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API to provide basic user information.
 *
 * @author Nikita Volobuev
 */
@RestController
@RequestMapping(RouteConstants.INIT_API)
public class InitApiController {

  /** Repository with user data. */
  private final ApplicationUserRepository userRepository;

  /**
   * Constructs InitApiController.
   *
   * @param userRepository repository with user data
   */
  public InitApiController(final ApplicationUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Handles API init requests.
   *
   * @throws SetupRequiredException if application setup has not been done yet
   * @throws AuthRequiredException if unauthorized user accesses this api
   * @return basic user data
   */
  @GetMapping
  public InitApiResponse doInit(final HttpServletRequest request) {
    if (!checkIfSetupIsDone()) {
      throw new SetupRequiredException();
    }

    final ApplicationUser user = userRepository.findByUsername(request.getRemoteUser());
    if (user == null) {
      throw new AuthRequiredException();
    }
    return new InitApiResponse(user.getJobs());
  }

  private boolean checkIfSetupIsDone() {
    return userRepository.count() > 0;
  }

}
