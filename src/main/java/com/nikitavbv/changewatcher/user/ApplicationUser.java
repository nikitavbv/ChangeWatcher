package com.nikitavbv.changewatcher.user;

import com.nikitavbv.changewatcher.jobs.WatchingJob;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.security.auth.Subject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Entity representing application user.
 *
 * @author Nikita Volobuev
 */
@Entity
public class ApplicationUser extends User {

  /** Watching jobs created by this user. */
  @OneToMany
  @JoinTable(
      name = "user_jobs",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "job_id")
  )
  private final List<WatchingJob> jobs = new ArrayList<>();

  public ApplicationUser(
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities
  ) {
    super(username, password, authorities);
  }

  /** Add a new watching job to this user. */
  public void addJob(final WatchingJob job) {
    this.jobs.add(job);
  }

  /** Remove watching job from this user. */
  public void removeJob(final WatchingJob job) {
    this.jobs.remove(job);
  }

  /** Get list of jobs belonging to this user. */
  public List<WatchingJob> getJobs() {
    return this.jobs;
  }
}
