package com.nikitavbv.changewatcher;

/**
 * API routes constants.
 *
 * @author Nikita Volobuev
 */
public class RouteConstants {

  /** Prefix for all API routes. */
  public static final String API_ROOT = "/api/v1";
  /** Pattern for API routes. */
  public static final String API_PATH_PATTERN = API_ROOT + "/**";

  /** Session init API URL. */
  public static final String INIT_API = API_ROOT + "/init";
  /** Authentication API URL. */
  public static final String LOGIN_API = API_ROOT + "/login";
  /** User data API URL. */
  public static final String USERS_API = API_ROOT + "/users";
  /** Webpage preview API URL. */
  public static final String PREVIEW_API = API_ROOT + "/preview";
  /** Watching job API URL. */
  public static final String JOBS_API = API_ROOT + "/jobs";

}
