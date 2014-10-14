package com.digout.rest.provider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class RestfulFilterFactory implements ResourceFilterFactory {

    @Autowired
    private LogFilter logFilter;

    @Override
    public List<ResourceFilter> create(final AbstractMethod am) {
        return null;// Lists.newAr<ResourceFilter> newArrayList(logFilter);
    }

}
