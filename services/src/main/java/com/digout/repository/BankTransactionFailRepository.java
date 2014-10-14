package com.digout.repository;

import com.digout.model.entity.common.BankTransactionFailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionFailRepository extends JpaRepository<BankTransactionFailEntity, Long> {

}
