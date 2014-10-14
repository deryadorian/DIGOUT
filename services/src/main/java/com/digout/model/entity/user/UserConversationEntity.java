package com.digout.model.entity.user;

import com.digout.model.entity.common.AutoGeneratedIdentifier;

import javax.persistence.*;

@Entity
@Table(name = "USER_CONVERSATION")
public class UserConversationEntity extends AutoGeneratedIdentifier {
    private static final long serialVersionUID = -4714772142100452453L;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "interlocutor_id", nullable = false, referencedColumnName = "id")
    private UserEntity interlocutor;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private UserMessageEntity lastMessage;

    public UserConversationEntity() {
    }

    public UserEntity getInterlocutor() {
        return this.interlocutor;
    }

    public UserMessageEntity getLastMessage() {
        return this.lastMessage;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setInterlocutor(final UserEntity interlocutor) {
        this.interlocutor = interlocutor;
    }

    public void setLastMessage(final UserMessageEntity lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
    }
}
