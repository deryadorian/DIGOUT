package com.digout.rest.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.container.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseLogging implements ContainerRequestFilter, ContainerResponseFilter {
    private final class Adapter implements ContainerResponseWriter {
        private final ContainerResponseWriter crw;

        private final boolean disableEntity;

        private long contentLength;

        private ContainerResponse response;

        private ByteArrayOutputStream baos;

        private final StringBuilder b = new StringBuilder();

        Adapter(final ContainerResponseWriter crw) {
            this.crw = crw;
            this.disableEntity = RequestResponseLogging.this.rc.getFeature(FEATURE_LOGGING_DISABLE_ENTITY);
        }

        @Override
        public void finish() throws IOException {
            if (!this.disableEntity) {
                byte[] entity = this.baos.toByteArray();
                printEntity(this.b, entity);

                // Output to log
                RequestResponseLogging.this.logger.info(this.b.toString());

                // Write out the headers and buffered entity
                OutputStream out = this.crw.writeStatusAndHeaders(this.contentLength, this.response);
                out.write(entity);
            }
            this.crw.finish();
        }

        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse response)
                throws IOException {
            printResponseLine(this.b, response);
            printResponseHeaders(this.b, response.getHttpHeaders());

            if (this.disableEntity) {
                RequestResponseLogging.this.logger.info(this.b.toString());
                return this.crw.writeStatusAndHeaders(contentLength, response);
            } else {
                this.contentLength = contentLength;
                this.response = response;
                return this.baos = new ByteArrayOutputStream();
            }
        }
    }

    /**
     * If true the request and response entities (if present) will not be logged. If false the request and response
     * entities will be logged.
     * <p/>
     * The default value is false.
     */
    public static final String FEATURE_LOGGING_DISABLE_ENTITY = "com.sun.jersey.config.feature.logging.DisableEntitylogging";

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLogging.class.getName());

    private static final String NOTIFICATION_PREFIX = "* ";

    private static final String REQUEST_PREFIX = "> ";

    private static final String RESPONSE_PREFIX = "< ";

    private static final int ENTITY_MAX_ALLOWED_SIZE_TO_LOG = 5 * 1024;// one
                                                                   // kilobyte

    private final Logger logger;

    private @Context
    HttpContext hc;

    private @Context
    ResourceConfig rc;

    private @Context
    HttpServletRequest httpServletRequest;

    private long id = 0;

    public RequestResponseLogging() {
        this(LOGGER);
    }

    /**
     * Create a logging filter logging the request and response to a JDK logger.
     * 
     * @param logger
     *            the logger to log requests and responses.
     */
    public RequestResponseLogging(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        setId();

        final StringBuilder b = new StringBuilder();
        printRequestLine(b, request);
        printRequestHeaders(b, request.getRequestHeaders());
        printRequestIpAddr(b, request);

        if (this.rc.getFeature(FEATURE_LOGGING_DISABLE_ENTITY)) {
            this.logger.info(b.toString());
            return request;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = request.getEntityInputStream();
            try {
                if (in.available() > 0) {
                    ReaderWriter.writeTo(in, out);

                    byte[] requestEntity = out.toByteArray();
                    printEntity(b, requestEntity);

                    request.setEntityInputStream(new ByteArrayInputStream(requestEntity));
                }
                return request;
            } catch (IOException ex) {
                throw new ContainerException(ex);
            } finally {
                this.logger.info(b.toString());
            }
        }
    }

    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response) {
        setId();
        response.setContainerResponseWriter(new Adapter(response.getContainerResponseWriter()));
        return response;
    }

    private StringBuilder prefixId(final StringBuilder b) {
        b.append(this.hc.getProperties().get("request-id").toString()).append(" ");
        return b;
    }

    private void printEntity(final StringBuilder b, final byte[] entity) throws IOException {
        final int length = entity.length;
        if (length == 0) {
            return;
        }
        b.append(length > ENTITY_MAX_ALLOWED_SIZE_TO_LOG ? "******Too large content*****" : new String(entity)).append(
                "\n");
    }

    private void printRequestHeaders(final StringBuilder b, final MultivaluedMap<String, String> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String header = e.getKey();
            for (String value : e.getValue()) {
                prefixId(b).append(REQUEST_PREFIX).append(header).append(": ").append(value).append('\n');
            }
        }
        prefixId(b).append(REQUEST_PREFIX).append('\n');
    }

    private void printRequestIpAddr(final StringBuilder b, final ContainerRequest request) {
        String ipAddress = request.getHeaderValue("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = this.httpServletRequest.getRemoteAddr();
        }
        prefixId(b).append(REQUEST_PREFIX).append("User-IP:").append(" ").append(ipAddress);
    }

    private void printRequestLine(final StringBuilder b, final ContainerRequest request) {
        prefixId(b).append(NOTIFICATION_PREFIX).append("Server in-bound request").append('\n');
        prefixId(b).append(REQUEST_PREFIX).append(request.getMethod()).append(" ")
                .append(request.getRequestUri().toASCIIString()).append('\n');
    }

    private void printResponseHeaders(final StringBuilder b, final MultivaluedMap<String, Object> headers) {
        for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
            String header = e.getKey();
            for (Object value : e.getValue()) {
                prefixId(b).append(RESPONSE_PREFIX).append(header).append(": ")
                        .append(ContainerResponse.getHeaderValue(value)).append('\n');
            }
        }
        prefixId(b).append(RESPONSE_PREFIX).append('\n');
    }

    private void printResponseLine(final StringBuilder b, final ContainerResponse response) {
        prefixId(b).append(NOTIFICATION_PREFIX).append("Server out-bound response").append('\n');
        prefixId(b).append(RESPONSE_PREFIX).append(Integer.toString(response.getStatus())).append('\n');
    }

    private synchronized void setId() {
        if (this.hc.getProperties().get("request-id") == null) {
            this.hc.getProperties().put("request-id", Long.toString(++this.id));
        }
    }
}
