package com.digout.rest.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.digout.artifact.ObjectFactory;

// @Provider
@Produces("application/xml")
@Consumes("application/xml")
public class XmlBodyReadWriteProvider implements MessageBodyWriter, MessageBodyReader {

    private final static String ENTITY_PACKAGE = ObjectFactory.class.getPackage().getName();

    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    public XmlBodyReadWriteProvider() {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(ENTITY_PACKAGE, Thread.currentThread()
                    .getContextClassLoader());
            this.marshaller = jaxbContext.createMarshaller();
            this.unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize(final Object t, final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isReadable(final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return true;
    }

    @Override
    public boolean isWriteable(final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap httpHeaders, final InputStream entityStream)
            throws IOException, WebApplicationException {
        try {
            return this.unmarshaller.unmarshal(entityStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTo(final Object t, final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        try {
            this.marshaller.marshal(t, entityStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
