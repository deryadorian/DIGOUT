package com.digout.repository;

import com.digout.model.entity.user.UserOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<UserOrderEntity, Long> {

    @Query("select count(o) from UserOrderEntity o where o.buyer.id = :userId")
    long countOrders(@Param("userId") Long userId);

    @Query("select count(o) from UserOrderEntity o where o.seller.id = :userId")
    long countSelling(@Param("userId") Long userId);

    @Query("select o from UserOrderEntity o where o.id = :orderId and (o.seller.id = :userId or o.buyer.id = :userId)")
    UserOrderEntity findOrderById(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Query(value = "select o from UserOrderEntity o where o.buyer.id = :buyerId and (o.product.status  in (:statuses)) order by o.product.status asc, o.orderDate desc", countQuery = "select count(o) from UserOrderEntity o where o.buyer.id = :buyerId and (o.product.status  in (:statuses))")
    Page<UserOrderEntity> getOrders(@Param("buyerId") Long buyerId, @Param("statuses") List statuses, Pageable pageable);

    @Query(value = "select o from UserOrderEntity o where o.seller.id = :sellerId and (o.product.status  in (:statuses)) order by o.product.status asc, o.orderDate desc", countQuery = "select count(o) from UserOrderEntity o where o.seller.id = :sellerId and (o.product.status  in (:statuses))")
    Page<UserOrderEntity> getSellings(@Param("sellerId") Long sellerId, @Param("statuses") List statuses,
            Pageable pageable);

    /*
     * @Modifying
     * 
     * @Query("UPDATE ") public int approvePurchase(@Param("orderId")Long orderId, @Param("userId")Long userId);
     */
}
