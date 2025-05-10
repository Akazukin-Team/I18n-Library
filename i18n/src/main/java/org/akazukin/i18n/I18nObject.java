package org.akazukin.i18n;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an internationalization object that can support building localized string resources.
 * This interface is used as a blueprint for handling text that supports multiple languages and locales.
 */
public interface I18nObject {
    /**
     * Builds a localized string resource using the provided I18nUtils instance, the default locale,
     * and a default fallback behavior of false.
     *
     * @param i18nUtils an instance of I18nUtils providing utilities and configurations for localization
     * @return the localized string resource based on the default locale and fallback settings,
     * or {@code null} if no appropriate value could be found
     */
    @Nullable
    default String build(final I18nUtils i18nUtils) {
        return this.build(i18nUtils, i18nUtils.getDefaultLocale(), false);
    }

    /**
     * Builds a localized string resource based on the provided localization utility, locale, and whether
     * to use the default locale as a fallback.
     *
     * @param i18nUtils     an instance of I18nUtils providing utilities and configurations for localization
     * @param locale        the specific locale for which the string resource needs to be built
     * @param defaultLocale a boolean flag indicating whether to use the default locale as a fallback
     * @return the localized string resource corresponding to the specified locale and settings,
     * or {@code null} if no appropriate value could be found
     */
    @Nullable
    String build(final I18nUtils i18nUtils, final String locale, boolean defaultLocale);

    /**
     * Builds a localized string resource using the provided I18nUtils instance and the specified locale,
     * and a default fallback behavior of false.
     *
     * @param i18nUtils an instance of I18nUtils providing utilities and configurations for localization
     * @param locale    the specific locale for which the string resource needs to be built
     * @return the localized string resource corresponding to the specified locale with fallback enabled,
     * or {@code null} if no appropriate value could be found
     */
    @Nullable
    default String build(final I18nUtils i18nUtils, final String locale) {
        return this.build(i18nUtils, locale, true);
    }
}
