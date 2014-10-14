package com.digout.manager;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.digout.artifact.OrderDetail;
import com.digout.artifact.OrderShipmentInfo;
import com.digout.converter.OrderConverter;
import com.digout.event.OrderShippedEvent;
import com.digout.event.source.OrderShippingInfoEmailSource;
import com.digout.exception.ApplicationException;
import com.digout.model.common.ProductStatus;
import com.digout.model.entity.product.ProductEntity;
import com.digout.model.entity.user.UserOrderEntity;
import com.digout.repository.OrderRepository;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.support.shipment.ShippingCarrierService;

public class OrderManager {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private I18nMessageSource i18n;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ShippingCarrierService shippingCarrierService;

    @Transactional
    public OrderDetail shipOrder(final OrderShipmentInfo shipmentInfo) throws ApplicationException {
        UserOrderEntity order = orderRepository.findOrderById(shipmentInfo.getOrderId(), userSessionHolder.getUserId());
        if (order == null) {
            throw new ApplicationException(i18n.getMessage("order.not.found"));
        }
        if (!userSessionHolder.getUserId().equals(order.getSeller().getId())) {
            throw new ApplicationException(i18n.getMessage("user.has.no.permission.to.ship.order"));
        }

        ProductEntity productEntity = order.getProduct();
        productEntity.setStatus(ProductStatus.SHIPPING);

        final DateTime shippedAt = DateTime.now();
        order.setStartShippingDate(shippedAt);
        order.setCarrierCode(shipmentInfo.getCarrierCode());
        order.setTrackingCode(shipmentInfo.getTrackingCode());

        orderRepository.save(order);
        
        final OrderDetail orderDetail = orderConverter.createTO(order);
        
        final OrderShippingInfoEmailSource source = OrderShippingInfoEmailSource.builder()
                .productId(productEntity.getId().toString())
                .productName(productEntity.getName())
                .price(productEntity.getPrice())
                .shippedUserName(order.getSeller().getFullname())
                .shipped(shippedAt)
                .carrierName(shippingCarrierService.getCarrirerNameByCode(shipmentInfo.getCarrierCode()))
                .carrierWebsite(shippingCarrierService.getCarrirerWebsiteByCode(shipmentInfo.getCarrierCode()))
                .trackingCode(shipmentInfo.getTrackingCode()) 
                .buyerEmail(order.getBuyer().getUserCredentials().getEmail())
                .build();
        applicationContext.publishEvent(new OrderShippedEvent(source));

        return orderDetail;
    }

    @Transactional
    public OrderShipmentInfo getOrderShipmentInfo(final Long orderId) throws ApplicationException {
        final Long userId = userSessionHolder.getUserId();
        final UserOrderEntity order = orderRepository.findOrderById(orderId, userSessionHolder.getUserId());
        if (order == null) {
            throw new ApplicationException(i18n.getMessage("order.not.found"));
        }

        if (!(userId.equals(order.getSeller().getId()) || userId.equals(order.getBuyer().getId()))) {
            throw new ApplicationException(i18n.getMessage("user.has.no.permission.to.watch.order"));
        }

        final OrderShipmentInfo shipInfo = new OrderShipmentInfo();
        shipInfo.setOrderId(order.getId());
        shipInfo.setCarrierCode(order.getCarrierCode());
        shipInfo.setTrackingCode(order.getTrackingCode());
        shipInfo.setSent(order.getStartShippingDate());

        return shipInfo;
    }
}
