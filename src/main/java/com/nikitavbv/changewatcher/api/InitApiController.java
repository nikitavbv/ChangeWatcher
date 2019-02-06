package com.nikitavbv.changewatcher.api;

import com.nikitavbv.changewatcher.RouteConstants;
import com.nikitavbv.changewatcher.security.AuthRequiredException;
import com.nikitavbv.changewatcher.user.ApplicationUser;
import com.nikitavbv.changewatcher.user.ApplicationUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(RouteConstants.INIT_API)
public class InitApiController {

  private ApplicationUserRepository applicationUserRepository;

  public InitApiController(ApplicationUserRepository applicationUserRepository) {
    this.applicationUserRepository = applicationUserRepository;
  }

  @GetMapping
  public InitApiResponse doInit(HttpServletRequest request) {
    ApplicationUser user = applicationUserRepository.findByUsername(request.getRemoteUser());
    if (user == null) {
      throw new AuthRequiredException();
    }
    return new InitApiResponse(user.getJobs());
  }

}
