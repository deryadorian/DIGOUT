package com.digout.repository;

import com.digout.model.entity.product.ProductCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductCommentEntity, Long> {

    @Query("select count(c) from ProductCommentEntity c where product.id = :productId")
    public long countCommentsByProduct(@Param("productId") Long productId);

    @Query(value = "select c from ProductCommentEntity c where c.product.id = :productId", countQuery = "select count(c) from ProductCommentEntity c where c.product.id = :productId")
    public Page<ProductCommentEntity> getCommentsByProductId(@Param("productId") Long productId, Pageable pageable);

    /*
     * @Query("select c from ProductCommentEntity c where c.product.id = :productId and c.publishedDate " +
     * "in (select max(p.publishedDate) from ProductCommentEntity p where c.product.id = :productId)") public
     * ProductCommentEntity getLastComment(@Param("productId") Long productId);
     */

    @Query("select c from ProductCommentEntity c where c.product.id = :productId order by c.publishedDate desc")
    public List<ProductCommentEntity> getLastComment(@Param("productId") Long productId, Pageable pageable);

}
