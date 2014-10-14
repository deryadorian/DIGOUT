package com.digout.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digout.model.entity.product.FavouriteProductEntity;
import com.digout.model.entity.product.ProductEntity;

public interface FavouriteProductRepository extends JpaRepository<FavouriteProductEntity, Long> {

    @Query("select count(f) from FavouriteProductEntity f where f.product.id = :productId")
    long countShortlistedProduct(@Param("productId") Long productId);

    @Query("select count(f) from FavouriteProductEntity f where f.owner.id = :userId")
    long countShortlistedProductsByUser(@Param("userId") Long userId);

    @Query("select p from FavouriteProductEntity f join f.product p where f.owner.id = :userId")
    Page<ProductEntity> getFavoriteProducts(@Param("userId") Long userId, Pageable pageable);

    @Query("select p from FavouriteProductEntity f join f.product p where f.owner.userCredentials.username = :sellerName")
    List<ProductEntity> getSellerFavouriteProducts(@Param("sellerName") String sellerName);

    @Query("select case when count(f)>0 then true else false end from FavouriteProductEntity f "
            + "where f.product.id = :productId and f.owner.id = :userId")
    boolean isProductInShortlist(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying
    @Query("delete from FavouriteProductEntity as f where f.product.id = :productId and f.owner.id = :userId")
    void removeProductFromFavourites(@Param("productId") Long productId, @Param("userId") Long userId);

    @Modifying
    @Query("delete from FavouriteProductEntity f where f.product.id = :productId")
    int removeProductFromFavorites(@Param("productId") Long productId);

}
