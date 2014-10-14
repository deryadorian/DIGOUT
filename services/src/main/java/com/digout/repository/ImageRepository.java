package com.digout.repository;

import com.digout.model.entity.common.ImageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends CrudRepository<ImageEntity, Long> {

    @Modifying
    @Query("delete from ImageEntity i where i.id = :imageId")
    int deleteImage(@Param("imageId") Long imageId);

    @Query("select i from ImageEntity i where i.group in (select g.group from ImageEntity g where g.id = :imageId)")
    List<ImageEntity> getImagesByImageId(@Param("imageId") Long imageId);

    @Query("select i from ImageEntity i where i.id in (:list)")
    List<ImageEntity> getImagesByUserId(@Param("list") List<Long> imageIds);
}