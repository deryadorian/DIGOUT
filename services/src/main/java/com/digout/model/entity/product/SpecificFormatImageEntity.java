package com.digout.model.entity.product;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageEntity;

import javax.persistence.*;

@MappedSuperclass
public abstract class SpecificFormatImageEntity extends ImageEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -4294055686661269579L;
    private static int fact(final int i) {
        return i == 0 ? 1 : fact(i - 1) * i;
    }

    public static void main(final String[] args) {
        System.out.println(fact(5));
    }

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImageFormat format;

    public ImageFormat getFormat() {
        return this.format;
    }

    public void setFormat(final ImageFormat format) {
        this.format = format;
    }
}