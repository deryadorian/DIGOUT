package com.digout.converter;

import com.digout.artifact.Image;
import com.digout.artifact.Product;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.product.*;
import com.digout.utils.StringsHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductSearchConverter extends SimpleConverterFactory<Product, ProductEntity> {
    // TODO: get rid of
    @Autowired
    private RequestSessionHolder requestSessionHolder;

    private Image createImage(final ProductImageInfoAccessor imageInfo) {
        return new Image() {
            /**
             * 
             */
            private static final long serialVersionUID = -4665386025165522331L;

            {
                setId(imageInfo.getId());
                setUrl(getProductImageUrl(imageInfo.getId()));
            }
        };
    }

    private String getProductImageUrl(final Long productImageId) {
        return StringsHelper.appendAll(this.requestSessionHolder.getServerAddress(), "/product/image/", productImageId);
    }

    private List<String> getTags(final ProductEntity entity) {
        List<String> tags = new ArrayList<String>();
        for (ProductTagEntity tag : entity.getTags()) {
            tags.add(tag.getTag());
        }
        return tags;
    }

    @Override
    protected ProductEntity initEntity(final Product product) {
        return null;
    }

    @Override
    protected Product initTO(final ProductEntity entity) {
        Product product = new Product();
        product.setProductId(entity.getId());
        product.setName(entity.getName());
        product.setPrice(entity.getPrice().toString());
        product.setCurrency(entity.getCurrency().name());
        product.setDatePublished(entity.getPublishedDate());
        product.getTags().addAll(getTags(entity));

        Set<ProductMainImageEntity> mainImageEntities = entity.getMainImages();
        if (!CollectionUtils.isEmpty(mainImageEntities)) {
            for (final ProductMainImageEntity productMainImage : mainImageEntities) {
                if (productMainImage.getFormat() == ImageFormat.ORIGINAL) {
                    product.setMainOrigImage(createImage(productMainImage));
                } else if (productMainImage.getFormat() == ImageFormat.STANDARD) {
                    product.setMainStandImage(createImage(productMainImage));
                } else if (productMainImage.getFormat() == ImageFormat.THUMB) {
                    product.setMainThumbImage(createImage(productMainImage));
                }
            }
        }
        Set<ProductSecondaryImageEntity> secondaryImageEntities = entity.getSecondaryImages();
        if (!CollectionUtils.isEmpty(secondaryImageEntities)) {
            List<Image> secondOrigImages = Lists.newLinkedList();
            List<Image> secondStandImages = Lists.newLinkedList();
            List<Image> secondThumbImages = Lists.newLinkedList();
            for (final ProductSecondaryImageEntity productSecImage : secondaryImageEntities) {
                if (productSecImage.getFormat() == ImageFormat.ORIGINAL) {
                    secondOrigImages.add(createImage(productSecImage));
                } else if (productSecImage.getFormat() == ImageFormat.STANDARD) {
                    secondStandImages.add(createImage(productSecImage));
                } else if (productSecImage.getFormat() == ImageFormat.THUMB) {
                    secondThumbImages.add(createImage(productSecImage));
                }
            }

            if (!CollectionUtils.isEmpty(secondOrigImages)) {
                product.getOrigImages().addAll(secondOrigImages);
            }

            if (!CollectionUtils.isEmpty(secondStandImages)) {
                product.getStandImages().addAll(secondStandImages);
            }

            if (!CollectionUtils.isEmpty(secondThumbImages)) {
                product.getThumbImages().addAll(secondThumbImages);
            }
        }

        return product;
    }
}
