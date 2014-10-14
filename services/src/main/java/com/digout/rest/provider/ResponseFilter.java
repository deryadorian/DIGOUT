package com.digout.rest.provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response) {

        System.out.println("Request: " + request.getPath());

        System.out.println("Response: " + response.getEntity());

        return response;
    }

}
