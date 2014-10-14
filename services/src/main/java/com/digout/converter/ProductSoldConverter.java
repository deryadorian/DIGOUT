package com.digout.converter;

import com.digout.artifact.Image;
import com.digout.artifact.Product;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.product.ProductImageInfoAccessor;
import com.digout.model.entity.product.ProductMainImageEntity;
import com.digout.model.entity.product.ProductSecondaryImageEntity;
import com.digout.utils.StringsHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class ProductSoldConverter extends SimpleConverterFactory<Product, ProductEntity> {

    @Autowired
    private RequestSessionHolder requestSessionHolder;

    private Image createImage(final ProductImageInfoAccessor imageInfo) {
        final Image image = new Image();
        image.setId(imageInfo.getId());
        image.setUrl(getProductImageUrl(imageInfo.getId()));
        return image;
    }

    private String getProductImageUrl(final Long productImageId) {
        return StringsHelper.appendAll(this.requestSessionHolder.getServerAddress(), "/product/image/", productImageId);
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
        product.setOwner(entity.getOwner().getId());
        product.setDatePublished(entity.getPublishedDate());
        product.setDateSold(entity.getSoldDate());
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
