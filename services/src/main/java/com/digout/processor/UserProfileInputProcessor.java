package com.digout.processor;

import com.digout.artifact.UserProfile;

public final class UserProfileInputProcessor implements DataInputPreProcessor<UserProfile> {

    @Override
    public UserProfile preProcess(final UserProfile userProfile) {
        if (userProfile.isSetIban()) {
            userProfile.setIban(userProfile.getIban().replaceAll("\\s", ""));
        }
        return userProfile;
    }

}
