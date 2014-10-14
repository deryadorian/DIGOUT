package com.digout.model.entity.common;

public interface ImageInfoAccessor {

    String getGroup();

    Long getId();

    // StorageEntity getStorage();
    //
    // void setStorage(StorageEntity storage);

    String getImagePath();

    void setGroup(String group);

    void setId(Long id);

    void setImagePath(String imagePath);

}
