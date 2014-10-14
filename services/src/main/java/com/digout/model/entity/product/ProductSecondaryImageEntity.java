package com.digout.model.entity.product;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageEntity;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_SECONDARY_IMAGES")
public class ProductSecondaryImageEntity extends ImageEntity implements ProductImageInfoAccessor {
    private static final long serialVersionUID = 5254998127827534436L;

    @ManyToOne(targetEntity = ProductEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImageFormat format;
    
    @Column(name = "sequence", nullable = true)
    private Integer sequence;

    @Override
    public ImageFormat getFormat() {
        return this.format;
    }

    @Override
    public ProductEntity getProduct() {
        return this.product;
    }

    @Override
    public void setFormat(final ImageFormat format) {
        this.format = format;
    }

    @Override
    public void setProduct(final ProductEntity product) {
        this.product = product;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(final Integer sequence) {
        this.sequence = sequence;
    }
}
