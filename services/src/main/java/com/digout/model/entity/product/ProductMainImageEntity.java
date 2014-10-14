package com.digout.model.entity.product;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageEntity;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_MAIN_IMAGES", uniqueConstraints = { @UniqueConstraint(columnNames = { "product_id", "format" }) })
public class ProductMainImageEntity extends ImageEntity implements ProductImageInfoAccessor {

    /**
     * 
     */
    private static final long serialVersionUID = -7811287989347717141L;

    @ManyToOne(targetEntity = ProductEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImageFormat format;

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
}
