package com.nikitavbv.changewatcher.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository storing users.
 *
 * @author Nikita Volobuev
 */
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
  ApplicationUser findByUsername(String username);
}
