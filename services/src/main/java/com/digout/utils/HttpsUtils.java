package com.digout.utils;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

public final class HttpsUtils {
    private static final class TrustedManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(final java.security.cert.X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(final java.security.cert.X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static HostnameVerifier getHostnameVerifier() {
        final HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(final String value, final SSLSession sslSession) {
                return true;
            }
        };
        return hv;
    }

    public static final ClientConfig getSSLClientConfig() {
        final ClientConfig config = new DefaultClientConfig();
        try {
            final SSLContext sslContext = getSSLContext();
            final HostnameVerifier hostnameVerifier = getHostnameVerifier();
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                    new HTTPSProperties(hostnameVerifier, sslContext));
        } catch (final GeneralSecurityException exc) {
            throw new RuntimeException(exc);
        }
        return config;
    }

    private static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { new TrustedManager() }, new SecureRandom());

        return sslContext;
    }

    private HttpsUtils() {
    }
}
