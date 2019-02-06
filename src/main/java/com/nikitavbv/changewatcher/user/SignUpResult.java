package com.nikitavbv.changewatcher.user;

public class SignUpResult {

  private long userID;

  SignUpResult(long userID) {
    this.userID = userID;
  }

  public long getUserID() {
    return userID;
  }

}
