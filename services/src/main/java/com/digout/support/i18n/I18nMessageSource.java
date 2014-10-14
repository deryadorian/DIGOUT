package com.digout.support.i18n;

import java.util.Locale;

public interface I18nMessageSource {

    Locale getLocale();

    String getMessage(String code);

    String getMessage(String code, Object... args);
}
