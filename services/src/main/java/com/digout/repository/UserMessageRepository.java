package com.digout.repository;

import com.digout.model.entity.user.UserMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMessageRepository extends JpaRepository<UserMessageEntity, Long> {
    public static final String query = "((u.sender.id = :userId or u.receiver.id = :ussrId) and (u.sender.id = :interlocutorId or u.receiver.id = :interlocutorId))";

    @Query("select count(m) from UserMessageEntity m where m.receiver.id = :userId and m.read = false")
    public long countUnreadMessages(@Param("userId") Long userId);

    @Query(value = "select u from UserMessageEntity u where ((u.sender.id = :userId and u.receiver.id = :interlocutorId and u.deletedBySender = false) or (u.sender.id = :interlocutorId  and u.receiver.id = :userId and u.deletedByReceiver = false)) order by u.sentDate desc", countQuery = "select count(u) from UserMessageEntity u where ((u.sender.id = :userId and u.receiver.id = :interlocutorId and u.deletedBySender = false) or (u.sender.id = :interlocutorId  and u.receiver.id = :userId and u.deletedByReceiver = false))")
    public Page<UserMessageEntity> getDialogMessages(@Param("userId") Long userId,
            @Param("interlocutorId") Long interlocutorId, Pageable pageable);

    @Query(value = "select u from UserMessageEntity u where ((u.sender.id = :userId and u.deletedBySender = false) or (u.receiver.id = :userId and u.deletedByReceiver = false)) order by u.sentDate desc", countQuery = "select count(u) from UserMessageEntity u where ((u.sender.id = :userId and u.deletedBySender = false) or (u.receiver.id = :userId and u.deletedByReceiver = false))")
    public Page<UserMessageEntity> getMessages(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("update UserMessageEntity u set u.read = true where (u.receiver.id = :receiverId and u.sender.id = :interlocutorId and u.read = false)")
    public int markAllMessagesAsRead(@Param("receiverId") Long receiverId, @Param("interlocutorId") Long interlocutorId);

    @Modifying
    @Query("UPDATE UserMessageEntity u set u.deletedByReceiver = :visible where u.receiver.id = :userId and u.sender.id = :interlocutorId")
    public int setVisibleDialogByReceiver(@Param("visible") Boolean visible, @Param("userId") Long userId,
            @Param("interlocutorId") Long interlocutorId);

    @Modifying
    @Query("UPDATE UserMessageEntity u set u.deletedBySender = :invisible where u.sender.id = :userId and u.receiver.id = :interlocutorId")
    public int setVisibleDialogBySender(@Param("invisible") Boolean invisible, @Param("userId") Long userId,
            @Param("interlocutorId") Long interlocutorId);

}
