package com.digout.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.digout.exception.InvalidVersionException;
import com.digout.model.entity.common.ApplicationVersionEntity;
import com.digout.repository.ApplicationVersionRepository;

public class ApplicationManager {

    @Autowired
    private ApplicationVersionRepository applicationVersionRepository;

    public ApplicationVersionEntity getByClientPlatformTypeAndVersionAndServerVersion(
            final ApplicationVersionEntity versionEntity) throws InvalidVersionException {
        ApplicationVersionEntity version = this.applicationVersionRepository
                .getByClientPlatformTypeAndVersionAndServerVersion(versionEntity.getClientPlatformType(),
                        versionEntity.getClientPlatformVersion(), versionEntity.getServerPlatformVersion());
        if (version == null) {
            List<ApplicationVersionEntity> entities = this.applicationVersionRepository.findAll();
            if (entities != null && !entities.isEmpty()) {
                ApplicationVersionEntity entity = entities.get(0);
                throw new InvalidVersionException("Digout currently does not support your platform. "
                        + "Use link to download new version: " + entity.getDownloadUrl());
            }

        }
        return version;
    }
}
