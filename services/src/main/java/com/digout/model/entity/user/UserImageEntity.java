package com.digout.model.entity.user;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageEntity;
import javax.persistence.*;

@Entity
@Table(name = "USER_IMAGE", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "format" }) })
public class UserImageEntity extends ImageEntity implements UserImageInfoAccessor {

    /**
     * 
     */
    private static final long serialVersionUID = 3063324987137095141L;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImageFormat format;

    public UserImageEntity() {
    }

    @Override
    public ImageFormat getFormat() {
        return this.format;
    }

    @Override
    public UserEntity getUser() {
        return this.user;
    }

    @Override
    public void setFormat(final ImageFormat format) {
        this.format = format;
    }

    @Override
    public void setUser(final UserEntity user) {
        this.user = user;
    }
}
