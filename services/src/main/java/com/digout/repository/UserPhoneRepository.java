package com.digout.repository;

import com.digout.model.entity.user.UserPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPhoneRepository extends JpaRepository<UserPhoneEntity, Long> {
}
