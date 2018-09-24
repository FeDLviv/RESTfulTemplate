package net.omisoft.rest;

public interface ApplicationConstants {

    String API_V1_BASE_PATH = "/api/v1/";

    String LANGUAGE_HEADER = "Accept-Language";
    String AUTH_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";

    String PROFILE_LOCAL = "local";
    String PROFILE_TEST = "test";
    String PROFILE_DEV = "dev";
    String PROFILE_DO = "do";
    String PROFILE_PROD = "prod";

    //TODO change password (min, max)
    int PASSWORD_MIN_LENGTH = 4;
    int PASSWORD_MAX_LENGTH = 15;
    int DEFAULT_PAGE_NUMBER = 0;
    int DEFAULT_PAGE_SIZE = 20;

}
