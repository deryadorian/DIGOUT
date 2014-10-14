package com.digout.converter;

import com.digout.artifact.*;
import com.digout.manager.RequestSessionHolder;
import com.digout.model.common.AddressAssignment;
import com.digout.model.entity.user.UserAddressEntity;
import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserImageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class UserProfileConverter extends SimpleConverterFactory<UserProfile, UserEntity> {
    @Autowired
    private UserAddressConverter userAddressConverter;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    @Override
    protected UserEntity initEntity(final UserProfile userProfile) {
        UserEntity userEntity = new UserEntity();
        List<Address> addresses = null;
        userEntity.setFullname(userProfile.getFullname());
        userEntity.setMobileNumber(userProfile.getPhone());
        Addresses addressesAll = userProfile.getAddresses();
        if (userProfile.isSetAddresses()) {
            addresses = addressesAll.getAddresses();
        }
        if (!CollectionUtils.isEmpty(addresses)) {
            for (Address address : addresses) {
                UserAddressEntity userAddressEntity = this.userAddressConverter.initEntity(address);
                userEntity.getAddresses().add(userAddressEntity);
                userAddressEntity.setUser(userEntity);
            }
        }
        return userEntity;
    }

    @Override
    protected UserProfile initTO(final UserEntity userEntity) {
        UserProfile userProfile = new UserProfile();
        userProfile.setAddresses(new Addresses());
        userProfile.setId(userEntity.getId());
        userProfile.setUsername(userEntity.getUserCredentials().getUsername());
        userProfile.setFullname(userEntity.getFullname());
        userProfile.setPhone(userEntity.getMobileNumber());
        Set<UserAddressEntity> addresses = userEntity.getAddresses();
        if (!CollectionUtils.isEmpty(addresses)) {
            for (UserAddressEntity addressEntity : addresses) {
                if (addressEntity.getAssignment().equals(AddressAssignment.FOR_USER)) {
                    Address address = this.userAddressConverter.initTO(addressEntity);
                    userProfile.getAddresses().getAddresses().add(address);
                }
            }
        }

        Set<UserImageEntity> imageEntities = userEntity.getImages();
        if (!CollectionUtils.isEmpty(imageEntities)) {
            userProfile.setImagesGroup(this.userPhotoConverter.convertUserImageEntities(imageEntities));
        }
        return userProfile;
    }
}
