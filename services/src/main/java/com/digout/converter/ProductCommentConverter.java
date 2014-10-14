package com.digout.converter;

import com.digout.artifact.Comment;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.product.ProductCommentEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.repository.ProductRepository;
import com.digout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductCommentConverter extends SimpleConverterFactory<Comment, ProductCommentEntity> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    @Override
    protected ProductCommentEntity initEntity(final Comment comment) {
        ProductCommentEntity productCommentEntity = new ProductCommentEntity();
        productCommentEntity.setComment(comment.getText());
        productCommentEntity.setPublishedDate(comment.getDate());
        productCommentEntity.setPostedBy(this.userRepository.findOne(comment.getOwnerId()));
        if (comment.isSetProductId()) {
            productCommentEntity.setProduct(this.productRepository.findOne(comment.getProductId()));
        }
        return productCommentEntity;
    }

    @Override
    protected Comment initTO(final ProductCommentEntity entity) {
        Comment comment = new Comment();
        comment.setCommentId(entity.getId());
        comment.setProductId(entity.getId());
        comment.setText(entity.getComment());
        comment.setDate(entity.getPublishedDate());
        UserEntity owner = entity.getPostedBy();
        comment.setOwnerId(owner.getId());
        comment.setOwnerUsername(owner.getUserCredentials().getUsername());
        comment.setOwnerFullName(owner.getFullname());
        comment.setOwnerThumbImage(this.userPhotoConverter.convertUserImageEntity(owner.getImages(), ImageFormat.THUMB));
        return comment;
    }

}
