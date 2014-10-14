package com.digout.converter;

import com.digout.artifact.Image;
import com.digout.artifact.ImagesGroup;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.user.UserImageEntity;
import com.digout.model.entity.user.UserImageInfoAccessor;
import com.digout.utils.StringsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class UserPhotoConverter {

    @Autowired
    private RequestSessionHolder requestSessionHolder;

    public ImagesGroup convertUserImageEntities(final Set<UserImageEntity> imageEntities) {
        ImagesGroup imagesGroup = null;
        if (!CollectionUtils.isEmpty(imageEntities)) {
            imagesGroup = new ImagesGroup();
            for (UserImageEntity imageEntity : imageEntities) {
                if (imageEntity.getFormat() == ImageFormat.THUMB) {
                    imagesGroup.setThumbImage(createImage(imageEntity));
                } else if (imageEntity.getFormat() == ImageFormat.STANDARD) {
                    imagesGroup.setStandardImage(createImage(imageEntity));
                } else if (imageEntity.getFormat() == ImageFormat.ORIGINAL) {
                    imagesGroup.setOriginalImage(createImage(imageEntity));
                }
            }
        }
        return imagesGroup;
    }

    public Image convertUserImageEntity(final Set<UserImageEntity> imageEntities, final ImageFormat imageFormat) {
        Image image = null;
        if (!CollectionUtils.isEmpty(imageEntities)) {
            for (UserImageEntity imageEntity : imageEntities) {
                if (imageEntity.getFormat() == imageFormat) {
                    image = createImage(imageEntity);
                }
            }
        }
        return image;
    }

    private Image createImage(final UserImageInfoAccessor imageInfo) {
        return new Image() {
            /**
             * 
             */
            private static final long serialVersionUID = -5369530135002692826L;

            {
                setId(imageInfo.getId());
                setUrl(getUserImageUrl(imageInfo.getId()));
            }
        };
    }

    private String getUserImageUrl(final Long userImageId) {
        return StringsHelper.appendAll(this.requestSessionHolder.getServerAddress(), "/user/image/", userImageId);
    }
}
