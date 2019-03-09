package com.nikitavbv.changewatcher.user;

/**
 * Returned by sign up API in case of successful user creation.
 * Contains user id.
 *
 * @author Nikita Volobuev
 */
public class SignUpResult {

  private long userID;

  SignUpResult(long userID) {
    this.userID = userID;
  }

  public long getUserID() {
    return userID;
  }

}
