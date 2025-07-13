package org.akazukin.i18n.object;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.I18nManager;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.util.interfaces.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A holder class for managing a collection of {@link II18nFormatter} instances.
 * Provides methods to retrieve internationalized text based on the provided {@link I18nObject} and locale,
 * trying each formatter in sequence until a result is found.
 * This class serves as a multi-formatter wrapper that enhances the flexibility of i18n processing
 * by supporting multiple formatting strategies.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nFormatterHolder {
    List<II18nFormatter> formatters = new CopyOnWriteArrayList<>();

    public I18nFormatterHolder(final II18nFormatter... formatters) {
        this.formatters.addAll(Arrays.asList(formatters));
    }

    /**
     * Builds a localized string using only the fallback locale across all formatters.
     * <p>
     * This method attempts to resolve the localized string using the fallback locale only,
     * trying each formatter in sequence until a result is found.
     * If none of the formatters can provide a result, {@code null} is returned.
     *
     * @param i18n the i18n object to build
     * @return the localized string for the fallback locale, or {@code null} if the message cannot be resolved
     */
    @Nullable
    public String buildByFallback(final I18nObject i18n) {
        return this.build(i18n, new I18nLang[]{I18nLang.FALLBACK});
    }

    /**
     * Builds a localized string for the specified locales across all formatters.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * trying each formatter in sequence for each locale until a result is found.
     * If none of the formatters can provide a result for any locale, {@code null} is returned.
     *
     * @param i18n  the i18n object to build
     * @param langs the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale, or {@code null} if none can be resolved
     */
    @Nullable
    public String build(final I18nObject i18n, final I18nLang[] langs) {
        for (final I18nLang lang : langs) {
            for (final II18nFormatter formatter : this.formatters) {
                final String result = i18n.build(formatter, new I18nLang[]{lang});
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Builds a localized string for the specified locales with fallback support across all formatters.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * with the fallback locale automatically appended as the final option.
     * Each locale is tried across all formatters in sequence until a result is found.
     * If none of the formatters can provide a result for any locale (including fallback), {@code null} is returned.
     *
     * @param i18n  the i18n object to build
     * @param langs the array of preferred locales, to which the fallback locale will be appended
     * @return the localized string for the first available locale with fallback, or {@code null} if none can be resolved
     */
    @Nullable
    public String buildWithFallback(final I18nObject i18n, final I18nLang[] langs) {
        final I18nLang[] newLocales = new I18nLang[langs.length + 1];
        System.arraycopy(langs, 0, newLocales, 0, langs.length);
        newLocales[langs.length] = I18nLang.FALLBACK;

        return this.build(i18n, newLocales);
    }

    /**
     * Builds a localized string using only the fallback locale across all formatters, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string using the fallback locale only,
     * trying each formatter in sequence until a result is found.
     * Guarantees a non-null result or throws an exception.
     *
     * @param i18n the i18n object to build
     * @return the localized string for the fallback locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for the fallback locale across all formatters
     */
    @NotNull
    public String buildRequiredByFallback(final I18nObject i18n) throws I18nLocaleNotFoundException {
        return this.buildRequired(i18n, new I18nLang[]{I18nLang.FALLBACK});
    }

    /**
     * Builds a localized string for the specified locales across all formatters, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * trying each formatter in sequence for each locale until a result is found.
     * Guarantees a non-null result or throws an exception.
     *
     * @param i18n  the i18n object to build
     * @param langs the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale across all formatters
     */
    @NotNull
    public String buildRequired(final I18nObject i18n, final I18nLang[] langs) throws I18nLocaleNotFoundException {
        for (final I18nLang lang : langs) {
            for (final II18nFormatter formatter : this.formatters) {
                final String result = i18n.build(formatter, new I18nLang[]{lang});
                if (result != null) {
                    return result;
                }
            }
        }

        throw new I18nLocaleNotFoundException(langs, i18n);
    }

    /**
     * Builds a localized string for the specified locales with fallback support across all formatters, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string
     * using the provided locales in order of preference,
     * with the fallback locale automatically appended as the final option.
     * Each locale is tried across all formatters in sequence until a result is found.
     * Guarantees a non-null result or throws an exception.
     *
     * @param i18n  the i18n object to build
     * @param langs the array of preferred locales, to which the fallback locale will be appended
     * @return the localized string for the first available locale with fallback. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale with fallback across all formatters
     */
    @NotNull
    public String buildRequiredWithFallback(final I18nObject i18n, final I18nLang[] langs) throws I18nLocaleNotFoundException {
        final I18nLang[] newLocales = new I18nLang[langs.length + 1];
        System.arraycopy(langs, 0, newLocales, 0, langs.length);
        newLocales[langs.length] = I18nLang.FALLBACK;

        return this.buildRequired(i18n, newLocales);
    }
}
