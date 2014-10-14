package com.digout.manager;

import com.digout.model.support.RequestSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public final class RequestSessionHolder {

    private static final ThreadLocal<RequestSession> threadLocalRequestSession = new InheritableThreadLocal<RequestSession>();

    private String hostName;
    private String hostPort;

    public void clean() {
        threadLocalRequestSession.set(null);
    }

    public Locale getLocale() {
        return getSession().getLocale();
    }

    public HttpServletRequest getRequest() {
        return getSession().getHttpServletRequest();
    }

    public String getServerAddress() {
        // TODO: refactor the hack
        return getRequest().getRequestURL().toString().replace(getRequest().getPathInfo(), "");
    }

    public RequestSession getSession() {
        return threadLocalRequestSession.get();
    }

    public void init(final RequestSession requestSession) {
        requestSession.setHostName(this.hostName);
        requestSession.setHostPort(this.hostPort);

        threadLocalRequestSession.set(requestSession);
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public void setHostPort(final String hostPort) {
        this.hostPort = hostPort;
    }
}
