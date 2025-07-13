package org.akazukin.i18n.object;

import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.I18nLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents an internationalization (i18n) object 
 * that provides the core contract for building localized string resources.
 * This interface serves as the foundation for all i18n
 * components in the library, enabling flexible and consistent handling of multilingual content.
 * <p>
 * The interface provides both nullable and non-null variants of build methods, allowing consumers
 * to choose the appropriate level of error handling based on their requirements. All methods
 * support locale fallback mechanisms to ensure graceful degradation 
 * when specific locales are unavailable.
 */
public interface I18nObject {
    /**
     * Builds a localized string using only the fallback locale.
     * <p>
     * This method attempts to resolve the localized string using the fallback locale only.
     * If the fallback locale cannot provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string
     * @return the localized string for the fallback locale, or {@code null} if the message cannot be resolved
     */
    @Nullable
    default String buildByFallback(final II18nFormatter formatter) {
        return this.build(formatter, new I18nLang[]{I18nLang.FALLBACK});
    }

    /**
     * Builds a localized string for the specified locales.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference.
     * If none of the locales can provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale, or {@code null} if none can be resolved
     */
    @Nullable
    String build(final II18nFormatter formatter, final I18nLang[] locales);

    /**
     * Builds a localized string for the specified locales with fallback support.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * with the fallback locale automatically appended as the final option.
     * If none of the locales (including fallback) can provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of preferred locales, to which the fallback locale will be appended
     * @return the localized string for the first available locale with fallback, or {@code null} if none can be resolved
     */
    @Nullable
    default String buildWithFallback(final II18nFormatter formatter, final I18nLang[] locales) {
        final I18nLang[] newLocales = new I18nLang[locales.length + 1];
        System.arraycopy(locales, 0, newLocales, 0, locales.length);
        newLocales[locales.length] = I18nLang.FALLBACK;

        return this.build(formatter, newLocales);
    }

    /**
     * Builds a localized string using only the fallback locale, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string using the fallback locale only.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string
     * @return the localized string for the fallback locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for the fallback locale
     */
    @NotNull
    default String buildRequiredByFallback(final II18nFormatter formatter) throws I18nLocaleNotFoundException {
        return this.buildRequired(formatter, new I18nLang[]{I18nLang.FALLBACK});
    }

    /**
     * Builds a localized string for the specified locales, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale
     */
    @NotNull
    String buildRequired(final II18nFormatter formatter, final I18nLang[] locales) throws I18nLocaleNotFoundException;

    /**
     * Builds a localized string for the specified locales with fallback support, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * with the fallback locale automatically appended as the final option.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of preferred locales, to which the fallback locale will be appended
     * @return the localized string for the first available locale with fallback. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale with fallback
     */
    @NotNull
    default String buildRequiredWithFallback(final II18nFormatter formatter, final I18nLang[] locales) throws I18nLocaleNotFoundException {
        final I18nLang[] newLocales = new I18nLang[locales.length + 1];
        System.arraycopy(locales, 0, newLocales, 0, locales.length);
        newLocales[locales.length] = I18nLang.FALLBACK;

        return this.buildRequired(formatter, newLocales);
    }
}
