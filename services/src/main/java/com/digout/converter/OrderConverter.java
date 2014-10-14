package com.digout.converter;

import com.digout.artifact.OrderDetail;
import com.digout.model.entity.user.UserOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderConverter extends SimpleConverterFactory<OrderDetail, UserOrderEntity> {
    @Autowired
    private UserAddressConverter userAddressConverter;
    @Autowired
    private ProductOrderConverter productOrderConverter;
    @Autowired
    private UserProfileOrderConverter userProfileOrderConverter;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    @Override
    protected UserOrderEntity initEntity(final OrderDetail orderDetail) {
        return null;
    }

    @Override
    protected OrderDetail initTO(final UserOrderEntity entity) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(entity.getId());
        orderDetail.setOrderDate(entity.getOrderDate());
        orderDetail.setProduct(this.productOrderConverter.createTO(entity.getProduct()));
        orderDetail.setReceiverAddress(this.userAddressConverter.createTO(entity.getAddressReceiver()));
        orderDetail.setBuyer(this.userProfileOrderConverter.createTO(entity.getBuyer()));
        orderDetail.setBuyerImagesGroup(this.userPhotoConverter.convertUserImageEntities(entity.getBuyer().getImages()));
        return orderDetail;
    }
}
