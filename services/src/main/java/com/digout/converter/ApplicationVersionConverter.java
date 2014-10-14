package com.digout.converter;

import com.digout.artifact.Version;
import com.digout.model.entity.common.ApplicationVersionEntity;

public class ApplicationVersionConverter extends SimpleConverterFactory<Version, ApplicationVersionEntity> {

    @Override
    protected ApplicationVersionEntity initEntity(final Version to) {
        ApplicationVersionEntity versionEntity = new ApplicationVersionEntity();
        versionEntity.setClientPlatformType(to.getMobilePlatformType());
        versionEntity.setClientPlatformVersion(to.getMobilePlatformVersion());
        versionEntity.setServerPlatformVersion(to.getApplicationVersion());
        return versionEntity;
    }

    @Override
    protected Version initTO(final ApplicationVersionEntity entity) {
        Version version = new Version();
        version.setMobilePlatformType(entity.getClientPlatformType());
        version.setMobilePlatformVersion(entity.getClientPlatformVersion());
        version.setApplicationVersion(entity.getServerPlatformVersion());
        return version;
    }
}
