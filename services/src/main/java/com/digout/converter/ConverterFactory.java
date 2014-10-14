package com.digout.converter;

import java.io.Serializable;
import java.util.List;

public interface ConverterFactory<TO extends Serializable, E extends Serializable> {
    /**
     * This method can create new Entity object and fill his simple fileds on base Transfer Object. This method must not
     * set references
     * 
     * @param to
     * @return new corresponding entity object
     */
    E createEntity(TO to);

    /**
     * This method can convert list Transfer objects to new created list of Entity objects
     * 
     * @param TOList
     * @return list entity objects
     */
    List<E> createEntityList(List<TO> toList);

    /**
     * This method can convert Transfer Object to Entity object(in some implementations may be based on database value)
     * 
     * @param to
     * @return corresponding entity object
     */
    // E getEntity(TO to);

    // List<E> getEntityList(List<TO> toList);

    /**
     * This method converts Entity object to new created Transfer Object
     * 
     * @param object
     * @return corresponding TO object
     */
    TO createTO(E entity);

    /**
     * This method can convert list Entity objects to new created list of TO objects
     * 
     * @param entityList
     * @return list TO objects
     */
    List<TO> createTOList(List<E> entityList);

    /**
     * This method can convert Entity object to list of one TO object
     * 
     * @param entity
     * @return list TO objects
     */
    List<TO> createTOSingleList(E entity);
}
