package com.digout.converter;

import com.digout.artifact.Address;
import com.digout.model.entity.user.UserAddressEntity;

public class UserAddressConverter extends SimpleConverterFactory<Address, UserAddressEntity> {
    @Override
    protected UserAddressEntity initEntity(final Address address) {
        UserAddressEntity userAddressEntity = new UserAddressEntity();
        userAddressEntity.setAddressLine(address.getLine());
        userAddressEntity.setDistrict(address.getDistrict());
        userAddressEntity.setCity(address.getCity());
        userAddressEntity.setRegion(address.getRegion());
        userAddressEntity.setAddressDefinition(address.getDescription());
        userAddressEntity.setPostalCode(address.getPostCode());
        userAddressEntity.setLatitude(address.getLatitude());
        userAddressEntity.setLongitude(address.getLongitude());
        Long id = address.getId();
        if (id != null) {
            userAddressEntity.setId(id);
        }
        return userAddressEntity;
    }

    @Override
    protected Address initTO(final UserAddressEntity addressEntity) {
        Address address = new Address();
        address.setId(addressEntity.getId());
        address.setCity(addressEntity.getCity());
        address.setRegion(addressEntity.getRegion());
        address.setPostCode(addressEntity.getPostalCode());
        address.setLine(addressEntity.getAddressLine());
        address.setDistrict(addressEntity.getDistrict());
        address.setDescription(addressEntity.getAddressDefinition());
        address.setLatitude(addressEntity.getLatitude());
        address.setLongitude(addressEntity.getLongitude());
        return address;
    }
}
