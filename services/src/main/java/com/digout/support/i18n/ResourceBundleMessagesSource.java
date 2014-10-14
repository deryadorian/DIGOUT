package com.digout.support.i18n;

import com.digout.manager.RequestSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class ResourceBundleMessagesSource extends ReloadableResourceBundleMessageSource implements I18nMessageSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBundleMessagesSource.class);

    @Autowired
    private RequestSessionHolder requestSessionHolder;

    private String getInternalMessage(final String key, final Object[] args, final Locale locale) {
        try {
            return getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            String msg = "Resource Bundle has no '" + key + "' message key";
            LOGGER.debug(msg, e);
            return msg;
        }
    }

    @Override
    public Locale getLocale() {
        return this.requestSessionHolder.getLocale();
    }

    @Override
    public String getMessage(final String key) {
        return getInternalMessage(key, null, getLocale());
    }

    @Override
    public String getMessage(final String key, final Object... args) {
        return getInternalMessage(key, args, getLocale());
    }
}
