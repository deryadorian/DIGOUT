package com.digout.rest.provider;

import java.lang.reflect.UndeclaredThrowableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.digout.artifact.Error;
import com.digout.exception.ApplicationException;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionMapper.class);

    @Override
    public Response toResponse(Throwable throwable) {

        ResponseBuilder builder = new ResponseBuilderImpl();
        if (throwable instanceof UndeclaredThrowableException) {
            throwable = ((UndeclaredThrowableException) throwable).getUndeclaredThrowable();
        }
        Error error = new Error();
        error.setDetails(throwable.toString());

        if (throwable instanceof ApplicationException) {
            LOGGER.debug("ApplicationException", throwable);
            ApplicationException applicationException = (ApplicationException) throwable;
            error.setType(applicationException.getType());
            if (applicationException.withMessageContext()) {
                error.getMessages().addAll(applicationException.getMessageContext().getMessages());
            }
            builder.entity(error).status(Status.NOT_ACCEPTABLE);
        } else {
            LOGGER.error("InternalException", throwable);
            error.setType("InternalException");
            error.getMessages().add("Internal system error occured");
            builder.entity(error).status(Status.INTERNAL_SERVER_ERROR);
        }
        return builder.type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
