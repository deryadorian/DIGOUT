package com.digout.repository;

import com.digout.model.common.ProductStatus;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digout.model.entity.product.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("select count(p) from ProductEntity p where p.owner.id = :userId and p.status = :status")
    long countProductsByStatus(@Param("userId") Long userId, @Param("status") ProductStatus status);

    @Query("select p from ProductEntity p where p.id = :productId and p.publishedDate is not null and p.status = 'FOR_SALE'")
    ProductEntity findInMarket(@Param("productId") Long productId);

    @Query(value = "select p from ProductEntity p left join p.owner u left join u.following f where f.id = :userId", countQuery = "select count(p) from ProductEntity p left join p.owner u left join u.following f where f.id = :userId")
    Page<ProductEntity> findMarketProducts(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select p from ProductEntity p where p.status = :status and p.publishedDate is not null order by p.publishedDate desc", countQuery = "select count(p) from ProductEntity p where p.status = :status and p.publishedDate is not null")
    Page<ProductEntity> findMarketProducts(@Param("status") ProductStatus status, Pageable pageable);

    @Query(value = "select p from ProductEntity p inner join p.tags t " + "where t.tag like :tag and p not in"
            + "(select p from ProductEntity p where p.name like :tag) and p.status = 'FOR_SALE'", countQuery = "select count(p) from ProductEntity p inner join p.tags t "
            + "where t.tag like :tag and p not in"
            + "(select p from ProductEntity p where p.name like :tag) and p.status = 'FOR_SALE'")
    Page<ProductEntity> findProductByTagAfter(@Param("tag") String tag, Pageable pageable);

    // todo: getting product status from method arguments
    @Query(value = "select p from ProductEntity p where p.name like :productNamePrepared and p.status = 'FOR_SALE'", countQuery = "select count(p) from ProductEntity p where p.name like :productNamePrepared and p.status = 'FOR_SALE'")
    Page<ProductEntity> findProductsByName(@Param("productNamePrepared") String productNamePrepared, Pageable pageable);

    @Query(value = "select p from ProductTagEntity t inner join t.products p where t.tag like :tag", countQuery = "select count(p) from ProductTagEntity t inner join t.products p where t.tag like :tag")
    Page<ProductEntity> findProductsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "select p from ProductEntity p left join p.tags t where p.name like :tag or t.tag like :tag", countQuery = "select count(p) from ProductEntity p left join p.tags t where p.name like :tag or t.tag like :tag")
    Page<ProductEntity> findProductsByTagAndProductName(@Param("tag") String tag, Pageable pageable);

    @Query(value = "select p from ProductEntity p where p.owner.id = :userId and p.publishedDate is null and p.status = :status", countQuery = "select count(p) from ProductEntity p where p.owner.id = :userId and p.publishedDate is null and p.status = :status")
    Page<ProductEntity> findUserDraftProducts(@Param("userId") Long userId, @Param("status") ProductStatus status,
            Pageable pageable);

    @Query("select p from ProductEntity p where p.owner.id = :userId and p.id = :productId")
    ProductEntity findUserProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query(value = "select p from ProductEntity p where p.owner.id = :userId and p.status = :status order by p.publishedDate desc", countQuery = "select count(p) from ProductEntity p where p.owner.id = :userId and p.status = :status")
    Page<ProductEntity> getSoldByUserId(@Param("userId") Long userId, @Param("status") ProductStatus status,
            Pageable pageable);

    @Query(value = "select p from ProductEntity p where p.owner.id = :userId and p.status = :status and p.publishedDate is not null order by p.publishedDate desc", countQuery = "select count(p) from ProductEntity p where p.owner.id = :userId and p.status = :status and p.publishedDate is not null")
    Page<ProductEntity> getUserProducts(@Param("userId") Long userId, @Param("status") ProductStatus status,
            Pageable pageable);

    /*
     * @Query(value = "select p from ProductTagEntity t inner join t.products p " + "where t.tag like :tag and p not in"
     * + "(select p from ProductEntity p where p.name like :tag) and p.status = 'FOR_SALE'", countQuery =
     * "select count(p) from ProductTagEntity t inner join t.products p " + "where t.tag like :tag and p not in" +
     * "(select p from ProductEntity p where p.name like :tag) and p.status = 'FOR_SALE'") Page<ProductEntity>
     * findProductByTagAfter(@Param("tag") String tag, Pageable pageable);
     */

    @Query("select case when count(p) > 0 then true else false end from ProductEntity p "
            + "where p.owner.id = :userId and p.id = :productId and p.publishedDate is null")
    Boolean isProductDraftAndOwnedBy(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("select case when count(p) > 0 then true else false end from ProductEntity p "
            + "where p.owner.id = :userId and p.id = :productId")
    Boolean isProductOwnedBy(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("select case when count(p) > 0 then true else false end from ProductEntity p "
            + "where p.owner.id = :userId and p.id = :productId and p.publishedDate is not null")
    Boolean isProductPublishedAndOwnedBy(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying
    @Query("update ProductEntity p set p.publishedDate = :date where p.id = :productId")
    void setProductPublishDate(@Param("productId") Long productId, @Param("date") DateTime date);

    @Modifying
    @Query("update ProductEntity p set p.status = :status where p.id = :productId")
    void setProductStatus(@Param("productId") Long productId, @Param("status") ProductStatus status);
}
