package org.akazukin.i18n;

public interface I18nObject {
    default String build(final I18nUtils i18nUtils) {
        return this.build(i18nUtils, i18nUtils.getDefaultLocale(), false);
    }

    String build(final I18nUtils i18nUtils, final String locale, boolean defaultLocale);

    default String build(final I18nUtils i18nUtils, final String locale) {
        return this.build(i18nUtils, locale, true);
    }
}
