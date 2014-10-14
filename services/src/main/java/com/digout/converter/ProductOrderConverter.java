package com.digout.converter;

import com.digout.artifact.Image;
import com.digout.artifact.Product;
import com.digout.artifact.ProductStatus;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.product.ProductImageInfoAccessor;
import com.digout.model.entity.product.ProductMainImageEntity;
import com.digout.model.entity.product.ProductSecondaryImageEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.utils.StringsHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class ProductOrderConverter extends SimpleConverterFactory<Product, ProductEntity> {
    @Autowired
    private UserAddressConverter userAddressConverter;
    // TODO: get rid of
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    private Image createImage(final ProductImageInfoAccessor imageInfo) {
        return new Image() {
            /**
             * 
             */
            private static final long serialVersionUID = 4027691306524571718L;

            {
                setId(imageInfo.getId());
                setUrl(getProductImageUrl(imageInfo.getId()));
            }
        };
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
        product.setCurrency(entity.getCurrency().toString());
        product.setPrice(entity.getPrice().toString());
        UserEntity owner = entity.getOwner();
        product.setOwner(owner.getId());
        product.setOwnerUserName(owner.getUserCredentials().getUsername());
        product.setOwnerFullName(owner.getFullname());
        product.setOwnerThumbImage(this.userPhotoConverter.convertUserImageEntity(owner.getImages(), ImageFormat.THUMB));
        if (entity.getAddress() != null) {
            product.setAddress(this.userAddressConverter.createTO(entity.getAddress()));
        }

        product.setProductStatus(ProductStatus.fromValue(entity.getStatus().toString()));

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
