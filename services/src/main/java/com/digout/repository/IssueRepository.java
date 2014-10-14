package com.digout.repository;

import java.util.List;

import com.digout.model.entity.user.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssueRepository extends JpaRepository<IssueEntity, Long> {
    
    @Query("select i from IssueEntity i where i.order.id = :orderId)")
    List<IssueEntity> findIssuesByOrderId(@Param("orderId") final Long orderId);
}
