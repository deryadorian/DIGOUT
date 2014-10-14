package com.digout.model.support;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public final class RequestSession {

    public static final String SESSION_TOKEN = "sessionToken";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_LANG = "accept-language";
    private final HttpServletRequest httpServletRequest;

    private String hostName;
    private String hostPort;

    private final String sessionToken;
    private final Locale locale;

    private String ipAddress;

    public RequestSession(final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        String sessionToken = httpServletRequest.getParameter(SESSION_TOKEN);
        this.sessionToken = Strings.isNullOrEmpty(sessionToken) ? httpServletRequest.getHeader(SESSION_TOKEN)
                : sessionToken;
        this.locale = resolveLocale(this.httpServletRequest);
    }

    public String getContextRoot() {
        return this.httpServletRequest.getContextPath().replace("/", "");
    }

    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    public String getIpAddress() {
        if (this.ipAddress == null) {
            this.ipAddress = this.httpServletRequest.getRemoteAddr();
        }
        return this.ipAddress;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getRemoteAddress() {
        return this.httpServletRequest.getRemoteAddr();
    }

    public String getRemoteHost() {
        return this.httpServletRequest.getRemoteHost();
    }

    public String getServerName() {
        return this.hostName.isEmpty() ? this.httpServletRequest.getServerName() : this.hostName;
    }

    public String getServerPort() {
        return this.hostPort.isEmpty() ? "" + this.httpServletRequest.getServerPort() : this.hostPort;
    }

    public String getTokenId() {
        return this.sessionToken;
    }

    public String getUserAgent() {
        return this.httpServletRequest.getHeader(USER_AGENT);
    }

    private Locale resolveLocale(final HttpServletRequest request) {
        String localeHeader = request.getHeader(ACCEPT_LANG);
        if(!Strings.isNullOrEmpty(localeHeader)) {
            localeHeader = localeHeader.substring(0, 2);
        }
        if (Strings.isNullOrEmpty(localeHeader) || localeHeader.startsWith(Locale.ENGLISH.getLanguage())) {
            return Locale.ENGLISH;
        }
        return new Locale(localeHeader);
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public void setHostPort(final String hostPort) {
        this.hostPort = hostPort;
    }

}
