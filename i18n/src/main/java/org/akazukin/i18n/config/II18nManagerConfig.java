package org.akazukin.i18n.config;

import org.akazukin.i18n.manager.data.I18nLang;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration interface for i18n manager components that extends the base resource configuration.
 * This interface defines the essential configuration properties required for managing
 * internationalization operations, including language settings and fallback behavior.
 * <p>
 * This interface extends {@link II18nResourceConfig} to inherit the core resource configuration
 * parameters while adding manager-specific language configuration capabilities.
 * The configuration is used by {@link org.akazukin.i18n.manager.I18nManager} to initialize
 * and manage localization services.
 */
public interface II18nManagerConfig extends II18nResourceConfig {
    /**
     * Returns the array of supported languages for the i18n system.
     *
     * @return an array of supported languages. Must not be {@code null} or empty.
     */
    I18nLang[] getLangs();

    /**
     * Returns the fallback language used when a translation is not found in the requested language.
     *
     * @return the fallback language. Can be {@code null} if no fallback is set.
     */
    @Nullable
    I18nLang getFallbackLang();
}
