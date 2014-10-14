package com.digout.service;

import java.io.InputStream;

public interface FacebookService {

    com.restfb.types.User getUser(String accessToken);

    InputStream getUserPicture(String userId);
}
