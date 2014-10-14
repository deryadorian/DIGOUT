package com.digout.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleConverterFactory<TO extends Serializable, E extends Serializable> implements
        ConverterFactory<TO, E> {

    @Override
    public E createEntity(final TO to) {
        return to == null ? null : initEntity(to);
    }

    @Override
    public List<E> createEntityList(final List<TO> toList) {
        final List<E> result = new ArrayList<E>();
        if (toList != null && !toList.isEmpty()) {
            for (TO to : toList) {
                E e = createEntity(to);
                if (e != null) {
                    result.add(e);
                }
            }
        }
        return result;
    }

    @Override
    public TO createTO(final E entity) {
        return entity == null ? null : initTO(entity);
    }

    @Override
    public List<TO> createTOList(final List<E> entityList) {
        List<TO> result = new ArrayList<TO>();
        if (entityList != null && !entityList.isEmpty()) {
            for (E entity : entityList) {
                TO to = createTO(entity);
                if (to != null) {
                    result.add(to);
                }
            }
        }
        return result;
    }

    @Override
    public List<TO> createTOSingleList(final E entity) {
        List<TO> result = new ArrayList<TO>();
        ;
        TO to = createTO(entity);
        if (to != null) {
            result.add(to);
        }
        return result;
    }

    /**
     * This method can initialize Entity object on base TO object
     * 
     * @param entity
     * @param to
     * @return Entity object
     */
    protected abstract E initEntity(TO to);

    /**
     * This method can initialize TO object on base Entity object
     * 
     * @param to
     * @param entity
     * @return TO object
     */
    protected abstract TO initTO(E entity);

}
