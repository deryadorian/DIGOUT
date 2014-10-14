package com.digout.repository;

import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserFollowerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFollowerRepository extends JpaRepository<UserFollowerEntity, Long>,
        CrudRepository<UserFollowerEntity, Long> {

    @Query("select count(f) from UserFollowerEntity f where f.follower.id = :userId")
    long countFollowed(@Param("userId") Long userId);

    @Query("select count(f) from UserFollowerEntity f where f.follower.id = :followerId and f.followed.id <> :userId")
    long countFollowedConsiderCurrentUser(@Param("followerId") Long followerId, @Param("userId") Long userId);

    @Query("select count(f) from UserFollowerEntity f where f.followed.id = :userId")
    long countFollowers(@Param("userId") Long userId);

    @Query("select count(f) from UserFollowerEntity f where f.followed.id = :followedId and f.follower.id <> :userId")
    long countFollowersConsiderCurrentUser(@Param("followedId") Long followedId, @Param("userId") Long userId);

    /*
     * @Query(value = "select e from UserFollowerEntity e where e.follower.id = :followerId", countQuery =
     * "select count(e) from UserFollowerEntity e where e.followed.id = :followerId") public Page<UserFollowerEntity>
     * getFollowed(@Param("followerId") Long followerId, @Param("userId") Long userId, Pageable pageable);
     * 
     * @Query(value = "select e from UserFollowerEntity e where e.followed.id = :userId or e.followed.id = :followedId",
     * countQuery =
     * "select count(e) from UserFollowerEntity e where e.followed.id = :userId or e.followed.id = :followedId") public
     * Page<UserFollowerEntity> getFollowers(@Param("followedId") Long followedId, @Param("userId") Long userId,
     * Pageable pageable);
     */

    @Query(value = "select e from UserFollowerEntity e where e.follower.id = :userId", countQuery = "select count(e) from UserFollowerEntity e where e.follower.id = :userId")
    public Page<UserFollowerEntity> getFollowed(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select e from UserFollowerEntity e where e.follower.id = :followerId and e.followed.id <> :userId", countQuery = "select count(e) from UserFollowerEntity e where e.follower.id = :followerId and e.followed.id <> :userId")
    public Page<UserFollowerEntity> getFollowedConsiderCurrentUser(@Param("followerId") Long followerId,
            @Param("userId") Long userId, Pageable pageable);

    @Query(value = "select e from UserFollowerEntity e where e.followed.id = :userId", countQuery = "select count(e) from UserFollowerEntity e where e.followed.id = :userId")
    public Page<UserFollowerEntity> getFollowers(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select e from UserFollowerEntity e where e.followed.id = :followedId and e.follower.id <> :userId ", countQuery = "select count(e) from UserFollowerEntity e where e.followed.id = :followedId and e.follower.id <> :userId")
    public Page<UserFollowerEntity> getFollowersConsiderCurrentUser(@Param("followedId") Long followedId,
            @Param("userId") Long userId, Pageable pageable);

    @Query("select f.followed from UserFollowerEntity f where f.follower.id = :userId")
    List<UserEntity> getFriends(@Param("userId") Long userId);

    @Query("select f.followed from UserFollowerEntity f where f.follower in "
            + "(select f.followed from f where f.follower.id = :userId) and (f.followed not in (:list) and f.followed.id<> :userId)")
    List<UserEntity> getFriendsWithParams(@Param("userId") Long userId, @Param("list") List list);

    @Query("select case when count(f)>0 then true else false end from UserFollowerEntity f where f.follower.id = :userId and f.followed.id = :followedId")
    boolean isFollowed(@Param("userId") Long userId, @Param("followedId") Long followedId);

    @Modifying
    @Query("update UserFollowerEntity u set u.following = :following where u.follower.id = :followedId and u.followed.id = :followerId")
    int setFollowing(@Param("followerId") Long followerId, @Param("followedId") Long followedId,
            @Param("following") boolean following);

    @Modifying
    @Query("DELETE FROM UserFollowerEntity u where u.follower.id = :followerId and u.followed.id = :followedId")
    public void unfollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

}
