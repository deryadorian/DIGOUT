package com.digout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digout.model.entity.common.AuditLogEntity;

public interface AuditRepository extends JpaRepository<AuditLogEntity, Long> {

}
