package com.digout.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.digout.artifact.Image;
import com.digout.artifact.Product;
import com.digout.artifact.ProductStatus;
import com.digout.artifact.SellType;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.product.ProductImageInfoAccessor;
import com.digout.model.entity.product.ProductMainImageEntity;
import com.digout.model.entity.product.ProductSecondaryImageEntity;
import com.digout.model.entity.product.ProductTagEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.support.money.CurrencyUnit;
import com.digout.utils.Formats;
import com.digout.utils.StringsHelper;
import com.google.common.collect.Lists;

public class ProductConverter extends SimpleConverterFactory<Product, ProductEntity> {

    @Value("${product.images.url}")
    private String productImagesServerUrl;

    @Autowired
    private UserAddressConverter userAddressConverter;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    private Image createImage(final ProductImageInfoAccessor imageInfo) {
        final Image image = new Image();
        image.setId(imageInfo.getId());
        image.setUrl(getProductImageUrl(imageInfo.getId()));
        return image;
    }

    private String getProductImageUrl(final Long productImageId) {
        return StringsHelper.appendAll(this.productImagesServerUrl, "/", productImageId);
    }

    @Override
    protected ProductEntity initEntity(final Product to) {
        ProductEntity entity = new ProductEntity();
        entity.setId(to.getProductId());
        entity.setName(to.getName());
        entity.setInformation(to.getInformation());
        entity.setPrice(Double.valueOf(to.getPrice()));
        entity.setShipmentType(to.getShipmentType());
        entity.setShipmentId(to.getShipmentId());
        entity.setCurrency(CurrencyUnit.of(to.getCurrency()));
        entity.setSellType(com.digout.model.common.SellType.valueOf(to.getSellType().value()));
        return entity;
    }

    private Product initTags(final Product to, final ProductEntity e) {
        List<String> tags = new ArrayList<String>();
        for (ProductTagEntity tag : e.getTags()) {
            tags.add(tag.getTag());
        }
        to.getTags().addAll(tags);
        return to;
    }

    @Override
    protected Product initTO(final ProductEntity entity) {
        Product product = new Product();
        product.setProductId(entity.getId());
        product.setName(entity.getName());
        product.setInformation(entity.getInformation());
        product.setPrice(entity.getPrice() == null ? null : Formats.formatDouble(entity.getPrice(), 2));
        product.setPurchasable(entity.isPurchasable());
        UserEntity owner = entity.getOwner();
        product.setOwner(owner.getId());
        product.setOwnerUserName(owner.getUserCredentials().getUsername());
        product.setOwnerFullName(owner.getFullname());
        product.setOwnerThumbImage(this.userPhotoConverter.convertUserImageEntity(owner.getImages(), ImageFormat.THUMB));
        product.setDatePublished(entity.getPublishedDate());
        product.setDateSold(entity.getSoldDate());
        product.setShipmentType(entity.getShipmentType());
        product.setShipmentId(entity.getShipmentId());
        product.setCurrency(entity.getCurrency() == null ? null : entity.getCurrency().name());
        if (entity.getAddress() != null) {
            product.setAddress(this.userAddressConverter.initTO(entity.getAddress()));
        }
        product.setProductStatus(ProductStatus.valueOf(entity.getStatus().name()));
        if (entity.getSellType() != null) {
            product.setSellType(SellType.fromValue(entity.getSellType().name()));
        }
        product = initTags(product, entity);

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
            final List<ProductSecondaryImageEntity> orderedSecondaryImageEntities = Lists
                    .newArrayList(secondaryImageEntities);
            Collections.sort(orderedSecondaryImageEntities, new Comparator<ProductSecondaryImageEntity>() {
                @Override
                public int compare(final ProductSecondaryImageEntity first, final ProductSecondaryImageEntity second) {
                    Integer firstSeq = first.getSequence();
                    Integer secondSeq = second.getSequence();
                    if (firstSeq != null && secondSeq != null) {
                        return firstSeq.compareTo(secondSeq);
                    } else if (firstSeq != null) {
                        return firstSeq.compareTo(0);
                    } else if (secondSeq != null) {
                        return secondSeq.compareTo(0);
                    }
                    return Integer.MAX_VALUE;
                }
            });
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
