package com.digout.repository;

import com.digout.model.entity.user.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserImageRepository extends JpaRepository<UserImageEntity, Long> {

    @Query("select i from UserImageEntity i where i.group in(select g.group from UserImageEntity g where g.id = :imageId) and "
            + "i.user.id = :userId")
    List<UserImageEntity> findUserImage(@Param("imageId") Long imageId, @Param("userId") Long userId);

    @Query("select i from UserImageEntity i where i.group = :groupId and i.user.id = :userId")
    List<UserImageEntity> findUserImage(@Param("groupId") String groupId, @Param("userId") Long userId);

}
