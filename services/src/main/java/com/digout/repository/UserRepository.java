package com.digout.repository;

import com.digout.model.UserOrigin;
import com.digout.model.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @Query("select u from UserEntity u where " + "u.userCredentials.email = :email and "
	    + "u.isSystemUser = :isSystemUser and " + "u.origin = :origin")
    UserEntity findByEmail(@Param("email") String email, @Param("isSystemUser") boolean isSystemUser,
	    @Param("origin") UserOrigin userOrigin);

    @Query("select u from UserEntity u where u.fullname = :fullname")
    public UserEntity findByFullname(@Param("fullname") String fullname);

    @Query("select u from UserEntity u where " + "u.userCredentials.username = :username and "
	    + "u.isSystemUser = :isSystemUser and " + "u.origin = :origin")
    UserEntity findByUsername(@Param("username") String username, @Param("isSystemUser") boolean isSystemUser,
	    @Param("origin") UserOrigin userOrigin);

    /*
     * @Query(
     * "select u from UserEntity u join fetch u.followers f where u.userCredentials.username = :sellerName"
     * ) Set<UserEntity> getSellerFollowersNames(@Param("sellerName") String
     * sellerName);
     */

    @Query(value = "select u from UserEntity u where u.userCredentials.username like :userNamePrepared", countQuery = "select count(u) from UserEntity u where u.userCredentials.username like :userNamePrepared")
    Page<UserEntity> findByUserName(@Param("userNamePrepared") String userNamePrepared, Pageable pageable);

    @Query("select u from UserEntity u where u.userCredentials.username = :username and "
	    + "u.userCredentials.password = :password and " + "u.isSystemUser = :isSystemUser and "
	    + "u.origin = :origin")
    UserEntity findByUsernameAndPassword(@Param("username") String username, @Param("password") String password,
	    @Param("isSystemUser") boolean isSystemUser, @Param("origin") UserOrigin userOrigin);

    @Query("select u from UserEntity u where "
	    + "(u.userCredentials.username = :usernameOrEmail or u.userCredentials.email = :usernameOrEmail) and "
	    + "u.isSystemUser = :isSystemUser and " + "u.origin = :origin")
    UserEntity findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail,
	    @Param("isSystemUser") boolean isSystemUser, @Param("origin") UserOrigin userOrigin);

    @Query("select u from UserEntity u where "
	    + "(u.userCredentials.username = :username or u.userCredentials.email = :username) and "
	    + "u.userCredentials.password = :password and " + "u.isSystemUser = :isSystemUser and "
	    + "u.origin = :origin")
    UserEntity findByUsernameOrEmailAndPassword(@Param("username") String username, @Param("password") String password,
	    @Param("isSystemUser") boolean isSystemUser, @Param("origin") UserOrigin userOrigin);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where "
	    + "(u.userCredentials.username = :username or u.userCredentials.email = :username or "
	    + "u.userCredentials.email = :email) and u.isSystemUser = :isSystemUser and u.origin = :origin")
    boolean isUserExistsByUsernameOrEmail(@Param("username") String usernameOrEmail, @Param("email") String email,
	    @Param("isSystemUser") boolean isSystemUser, @Param("origin") UserOrigin userOrigin);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where "
	    + "u.externalId = :externalId and u.origin = :origin")
    boolean isUserExistsByExternalId(@Param("externalId") String externalId, @Param("origin") UserOrigin userOrigin);

    @Query("select u from UserEntity u where " + "u.externalId = :externalId and u.origin = :origin")
    UserEntity getUserByExternalId(@Param("externalId") String externalId, @Param("origin") UserOrigin userOrigin);

}
