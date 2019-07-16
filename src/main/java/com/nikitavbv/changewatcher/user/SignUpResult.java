package com.nikitavbv.changewatcher.user;

/**
 * Returned by sign up API in case of successful user creation.
 * Contains user id.
 *
 * @author Nikita Volobuev
 */
public class SignUpResult {

  /** ID of user, who was just registered. */
  private final long userID;

  /** Creates SignUpResult. */
  SignUpResult(long userID) {
    this.userID = userID;
  }

  /** Returns id of user who was just registered. */
  public long getUserID() {
    return userID;
  }
}
