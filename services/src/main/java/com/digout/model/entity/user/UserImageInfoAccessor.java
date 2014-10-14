package com.digout.model.entity.user;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageInfoAccessor;

/**
 * Created with IntelliJ IDEA. User: Emo Date: 14.06.13 Time: 15:25 To change this template use File | Settings | File
 * Templates.
 */
public interface UserImageInfoAccessor extends ImageInfoAccessor {

    ImageFormat getFormat();

    UserEntity getUser();

    void setFormat(ImageFormat format);

    void setUser(UserEntity user);
}
