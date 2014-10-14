package com.digout.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;

public class FacebookIntegrationService implements FacebookService {

    private static final String FB_PICTURE_URL = "http://graph.facebook.com/%s/picture";

    @Override
    public User getUser(final String accessToken) throws FacebookException {
        final FacebookClient fbClient = new DefaultFacebookClient(accessToken);
        com.restfb.types.User user = fbClient.fetchObject("me", com.restfb.types.User.class);
        return user;
    }

    @Override
    public InputStream getUserPicture(final String userId) {
        try {
            InputStream inputStream = new URL(String.format(FB_PICTURE_URL, userId)).openStream();
            return inputStream;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
