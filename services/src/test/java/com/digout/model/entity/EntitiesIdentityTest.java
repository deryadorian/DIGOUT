package com.digout.model.entity;

import com.digout.model.entity.user.UserAddressEntity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;


@Ignore
public class EntitiesIdentityTest {
    @Test
    public void testSizeOfSet(){

        UserAddressEntity address1 = new UserAddressEntity();
        address1.setId(1L);
        UserAddressEntity address2 = new UserAddressEntity();
        address2.setId(2L);
        UserAddressEntity address3 = new UserAddressEntity();
        address3.setId(3L);

        Set<UserAddressEntity> set = new HashSet<UserAddressEntity>();
        set.add(address1);
        set.add(address2);
        set.add(address3);
        assertEquals(3, set.size());
    }

    @Test
    public void testAddSameObjToSet(){
        UserAddressEntity address1 = new UserAddressEntity();
        address1.setId(1L);
        Set<UserAddressEntity> set = new HashSet<UserAddressEntity>();
        set.add(address1);
        set.add(address1);
        set.add(address1);
        assertEquals(1, set.size());

    }



    @Test
    public void testAddToSetWithoutId(){
        UserAddressEntity address1 = new UserAddressEntity();
        address1.setAddressDefinition("addr1");
        address1.setAddressLine("aqweqwe");
        address1.setCity("Kieb");
        address1.setPostalCode("asdasd");
        address1.setRegion("UA");


        UserAddressEntity address2 = new UserAddressEntity();

        address2.setAddressDefinition("addr1");
        address2.setAddressLine("aqweqwe");
        address2.setCity("Kieb");
        address2.setPostalCode("asdasd");
        address2.setRegion("UA");
        Set<UserAddressEntity> set = new HashSet<UserAddressEntity>();
        set.add(address1);
        //set.add(address1);
        set.add(address2);

        assertEquals(1, set.size());
    }
}
