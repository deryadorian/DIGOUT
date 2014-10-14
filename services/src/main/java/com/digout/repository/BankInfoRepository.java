package com.digout.repository;

import com.digout.model.entity.common.BankInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankInfoRepository extends JpaRepository<BankInfoEntity, Long> {
}
