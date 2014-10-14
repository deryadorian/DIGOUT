package com.digout.repository;

import com.digout.model.common.AddressAssignment;
import com.digout.model.entity.user.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {

    @Query("select count(a) from UserAddressEntity a where a.user.id = :userId and a.assignment = :addressAssignment")
    Long countUserAddresses(@Param("userId") Long userId, @Param("addressAssignment") AddressAssignment assignment);

    @Modifying
    @Query("DELETE from UserAddressEntity a where a.id = :addressId and a.user.id = :userId")
    int delete(@Param("addressId") Long addressId, @Param("userId") Long userId);

}
