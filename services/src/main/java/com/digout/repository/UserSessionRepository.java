package com.digout.repository;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digout.model.entity.user.UserTokenEntity;

public interface UserSessionRepository extends JpaRepository<UserTokenEntity, String> {

    @Query("select u from UserTokenEntity u where u.tokenId = :tokenId and u.expireTime >= :date")
    UserTokenEntity findLiveTokenById(@Param("tokenId") String tokenId, @Param("date") DateTime dateTime);

}
