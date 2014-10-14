package com.digout.config;

import org.springframework.util.Assert;

public class ConfigurationProvider {

    private int userSessionTimeoutMins;
    private String guestLogin;// this is digout system user
    private String guestPassword;
    private String facebookPassword;
    private String userImagesStorePath;
    private String productsImagesStorePath;

    public final String getFacebookPassword() {
        return this.facebookPassword;
    }

    public final String getGuestLogin() {
        return this.guestLogin;
    }

    public final String getGuestPassword() {
        return this.guestPassword;
    }

    public final String getProductsImagesStorePath() {
        return this.productsImagesStorePath;
    }

    public final String getUserImagesStorePath() {
        return this.userImagesStorePath;
    }

    public final int getUserSessionTimeoutMins() {
        return this.userSessionTimeoutMins;
    }

    public final void setFacebookPassword(final String facebookPassword) {
        this.facebookPassword = facebookPassword;
    }

    public final void setGuestLogin(final String guestLogin) {
        this.guestLogin = guestLogin;
    }

    public final void setGuestPassword(final String guestPassword) {
        this.guestPassword = guestPassword;
    }

    public final void setProductsImagesStorePath(final String productsImagesStorePath) {
        Assert.notNull(productsImagesStorePath, "Path for products images is not set");
        this.productsImagesStorePath = productsImagesStorePath;
    }

    public final void setUserImagesStorePath(final String userImagesStorePath) {
        this.userImagesStorePath = userImagesStorePath;
    }

    public final void setUserSessionTimeoutMins(final int userSessionTimeoutMins) {
        this.userSessionTimeoutMins = userSessionTimeoutMins;
    }

}
