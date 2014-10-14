package com.digout.rest.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.digout.artifact.Registration;
import com.digout.artifact.User;
import com.digout.artifact.UserCredentials;

@SuppressWarnings("rawtypes")
@Provider
@Produces("application/json")
@Consumes("application/json")
public class JsonBodyReaderWriter implements MessageBodyWriter, MessageBodyReader {

    private final ObjectMapper objectMapper;

    public JsonBodyReaderWriter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(Feature.WRAP_ROOT_VALUE, true);
        this.objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        this.objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
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
        return this.objectMapper.readValue(entityStream, type);
    }

    @Override
    public void writeTo(final Object t, final Class type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        this.objectMapper.writeValue(entityStream, t);
    }

    
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.WRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
        
        final Registration registration = new Registration();
        registration.setEmail("fevzi.anifyeyev@gmail.com");
        registration.setFullname("Fevzi Anifyeyev");
        registration.setMobileNumber("0509115150");
        registration.setPassword("password");
        registration.setUsername("fevzi");
        
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue(stringWriter, registration);
        
        System.out.println(stringWriter.toString());
        
        stringWriter = new StringWriter();
        final UserCredentials userCred = new UserCredentials();
        userCred.setUsername("fevzi");
        userCred.setPassword("password");
        mapper.writeValue(stringWriter, userCred);
        
        System.out.println(stringWriter.toString());
    }
}
