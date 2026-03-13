package org.akazukin.i18n.config;

import org.akazukin.i18n.manager.data.II18nLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration interface for i18n manager components that extends the base resource configuration.
 * This interface defines the essential configuration properties required for managing
 * internationalization operations, including language settings and fallback behavior.
 */
public interface II18nManagerConfig {
    /**
     * Returns the array of supported languages for the i18n system.
     *
     * @return an array of supported languages. Must not be {@code null} or empty.
     */
    @NotNull II18nLang[] getLangs();

    /**
     * Returns the fallback language used when a translation is not found in the requested language.
     *
     * @return the fallback language. Can be {@code null} if no fallback is set.
     */
    @Nullable II18nLang getFallbackLang();
}
