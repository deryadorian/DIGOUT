package com.digout.model.entity.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "USER_SESSION_TOKENS")
public class UserTokenEntity implements Serializable {
    private static final long serialVersionUID = 4200278698585251285L;

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @ManyToOne(targetEntity = UserEntity.class, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "last_action_time", nullable = false)
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime lastActionTime = DateTime.now();

    @Column(name = "expire_time", nullable = false)
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime expireTime;

    public DateTime getExpireTime() {
        return this.expireTime;
    }

    public DateTime getLastActionTime() {
        return this.lastActionTime;
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setExpireTime(final DateTime expireTime) {
        this.expireTime = expireTime;
    }

    public void setLastActionTime(final DateTime lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public void setTokenId(final String tokenId) {
        this.tokenId = tokenId;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
    }
}
