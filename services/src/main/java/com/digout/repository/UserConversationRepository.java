package com.digout.repository;

import com.digout.model.entity.user.UserConversationEntity;
import com.digout.model.entity.user.UserMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserConversationRepository extends JpaRepository<UserConversationEntity, Long> {

    @Query("select c from UserConversationEntity c where ((c.user.id = :userId and c.interlocutor.id = :interlocutorId) or (c.user.id = :interlocutorId and c.interlocutor.id = :userId))")
    public UserConversationEntity find(@Param("userId") Long userId, @Param("interlocutorId") Long interlocutorId);

    @Query(value = "select u from UserConversationEntity c left join c.lastMessage u where ((u.sender.id = :userId and u.deletedBySender = false) or (u.receiver.id = :userId and u.deletedByReceiver = false)) order by u.sentDate desc", countQuery = "select count(u) from UserConversationEntity c left join c.lastMessage u where ((u.sender.id = :userId and u.deletedBySender = false) or (u.receiver.id = :userId and u.deletedByReceiver = false))")
    public Page<UserMessageEntity> getConversations(@Param("userId") Long userId, Pageable pageable);

}
