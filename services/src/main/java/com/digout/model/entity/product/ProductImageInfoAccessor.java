package com.digout.model.entity.product;

import com.digout.model.common.ImageFormat;
import com.digout.model.entity.common.ImageInfoAccessor;

public interface ProductImageInfoAccessor extends ImageInfoAccessor {

    ImageFormat getFormat();

    ProductEntity getProduct();

    void setFormat(ImageFormat format);

    void setProduct(ProductEntity product);
}
