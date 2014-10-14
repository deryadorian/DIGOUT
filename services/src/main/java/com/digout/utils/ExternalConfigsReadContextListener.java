package com.digout.utils;

import com.google.common.base.Strings;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ExternalConfigsReadContextListener implements ServletContextListener {

    public static final String DIGOUT_CONFIG = "com.digout.config";
    public static final String DIGOUT_LOGS_DIR = "com.digout.logs.dir";

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final String configFileName = event.getServletContext().getInitParameter(DIGOUT_CONFIG);
        if (!Strings.isNullOrEmpty(configFileName)) {
            System.setProperty(DIGOUT_CONFIG, configFileName);
        }

        final String logsDir = event.getServletContext().getInitParameter(DIGOUT_LOGS_DIR);
        if (!Strings.isNullOrEmpty(logsDir)) {
            System.setProperty(DIGOUT_LOGS_DIR, logsDir);
        }

    }
}
