package org.akazukin.i18n.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.data.I18nLang;
import org.jetbrains.annotations.NotNull;

import java.io.File;


/**
 * Implementation of {@link II18nManagerConfig} that provides configuration settings for i18n manager components.
 * This class serves as an immutable configuration container using the builder pattern to construct
 * instances with all required properties for managing internationalization operations.
 * <p>
 * The configuration encompasses essential parameters including ClassLoader for resource loading,
 * domain and application identifiers for resource organization, data folder for custom resources,
 * supported languages, and fallback language settings.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder(setterPrefix = "set")
@Getter
public class I18nManagerConfig implements II18nManagerConfig {
    @NotNull
    ClassLoader classLoader;
    @NotNull
    String domain;
    @NotNull
    String appId;
    @NotNull
    File dataFolder;
    @NotNull
    I18nLang fallbackLang;
    @NotNull
    I18nLang[] langs;
}
