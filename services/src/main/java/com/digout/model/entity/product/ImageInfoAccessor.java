package com.digout.model.entity.product;

import com.digout.model.entity.common.StorageEntity;

public interface ImageInfoAccessor {

    Long getId();

    String getImagePath();

    StorageEntity getStorage();

    void setId(Long id);

    void setImagePath(String imagePath);

    void setStorage(StorageEntity storage);

}
