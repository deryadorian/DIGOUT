package com.digout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digout.model.entity.common.ApplicationVersionEntity;

public interface ApplicationVersionRepository extends JpaRepository<ApplicationVersionEntity, Long> {

    @Query("select av from ApplicationVersionEntity av where av.clientPlatformType = :clientPlatformType "
            + "and av.clientPlatformVersion = :clientPlatformVersion "
            + "and av.serverPlatformVersion = :serverPlatformVersion")
    ApplicationVersionEntity getByClientPlatformTypeAndVersionAndServerVersion(
            @Param("clientPlatformType") String clientPlatformType,
            @Param("clientPlatformVersion") String clientPlatformVersion,
            @Param("serverPlatformVersion") String serverPlatformVersion);
}
