package com.digout.repository;

import com.digout.model.entity.product.ProductTagEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ProductTagRepository extends JpaRepository<ProductTagEntity, String> {

    @Query(value = "select e from ProductTagEntity e where e.tag in (:tags)")
    public List<ProductTagEntity> findByTags(@Param("tags") Set<String> tags);
}
