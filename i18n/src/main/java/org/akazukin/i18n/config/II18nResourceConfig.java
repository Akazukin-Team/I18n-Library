package org.akazukin.i18n.config;

import java.io.File;

/**
 * Defines the core resource configuration contract for internationalization (i18n) components.
 * This interface provides the essential configuration parameters required for loading and managing
 * localized resources in the i18n library.
 */
public interface II18nResourceConfig {
    /**
     * Returns the ClassLoader used for loading i18n resource files from the classpath.
     *
     * @return the ClassLoader for resource loading operations. Must not be {@code null}.
     */
    ClassLoader getClassLoader();

    /**
     * Returns the domain identifier for the i18n resource namespace.
     *
     * @return the domain identifier string. Must not be {@code null}.
     */
    String getDomain();

    /**
     * Returns the unique application identifier used for resource organization.
     *
     * @return the application identifier string. Must not be {@code null}.
     */
    String getAppId();

    /**
     * Returns the data folder where custom i18n resource files are stored.
     *
     * @return the data folder File object. Must not be {@code null}.
     */
    File getDataFolder();
}
