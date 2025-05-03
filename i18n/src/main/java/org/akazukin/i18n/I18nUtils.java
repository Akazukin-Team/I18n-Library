package org.akazukin.i18n;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.util.interfaces.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for managing and retrieving internationalized strings (i18n).
 * This class provides functionality to load, store, and fetch localized strings
 * for different languages based on the application's configuration.
 * <p>
 * The localized data is stored in `Properties` files and can be loaded
 * either from classpath resources or external files.
 * <p>
 * The utility supports:
 * - Replacement of placeholders in localized strings with arguments.
 * - Hierarchical fallback to default locale strings when a key is unavailable
 * in the desired locale.
 * - Dynamic reloading of localization data.
 * <p>
 * Implements the {@link Reloadable} interface to provide reload functionality.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Getter
@Setter
public class I18nUtils implements Reloadable {
    private static final Pattern REGEX_I18N = Pattern.compile("<\\$[a-zA-Z0-9.]+>");
    private static final Pattern REGEX_ARGS = Pattern.compile("<args\\[[0-9]+]>");

    @NotNull
    final Map<String, Properties> language = new ConcurrentHashMap<>();

    @NotNull
    final ClassLoader classLoader;
    @NotNull
    final String domain;
    @NotNull
    final String appId;
    @NotNull
    final File dataFolder;
    @NotNull
    String defaultLocale;
    @NotNull
    String[] locales;

    public I18nUtils(@NotNull final ClassLoader classLoader, @NotNull final String domain, @NotNull final String appId, final @NotNull File dataFolder, @NotNull final String defaultLocale, @NotNull final String... locales) {
        this.classLoader = classLoader;
        this.domain = domain;
        this.appId = appId;
        this.dataFolder = dataFolder;
        this.setDefaultLocale(defaultLocale);
        this.setLocales(locales);

        this.load();
    }

    /**
     * Loads and initializes localization properties for the application's supported locales.
     * <p>
     * This method processes each locale configured for the application, attempting to load
     * both default and custom localization files corresponding to the locale. The method performs
     * the following steps for each locale:
     * <p>
     * 1. Converts the locale string to lowercase for consistent handling.
     * 2. Constructs the path to the default localization resource within the application's assets directory.
     * 3. Attempts to load default localization properties from the resource files in the classpath.
     * 4. Attempts to load custom localization properties from files in the local data folder.
     * 5. Merges the loaded properties (if any) into the application's localization data structure.
     * <p>
     * If no locales are successfully loaded, an {@code IllegalStateException} is thrown, with
     * an error logged. The number of successfully loaded localization sets is logged at the end.
     * <p>
     * Thread-Safety:
     * This method is synchronized to ensure thread-safe access to shared resources like the language map.
     * <p>
     * Error Handling:
     * - Logs warnings when either default or custom localization files cannot be loaded for a specific locale.
     * - Throws an exception if no locales are successfully loaded.
     */
    private synchronized void load() {
        for (String locale : this.locales) {
            locale = locale.toLowerCase();

            final String defaultDir = "assets/" + this.domain.replace(".", "/") + "/" + this.appId + "/";

            final String langsFile = "langs/" + locale + ".lang";
            final File file = new File(this.dataFolder, langsFile);

            final Properties props = new Properties();

            try (final InputStream is = this.classLoader.getResourceAsStream(defaultDir + langsFile)) {
                if (is != null) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } else {
                    log.warn("Failed to load the default locale set for " + locale);
                }
            } catch (final IOException e) {
                log.warn("Failed to load localization file | " + langsFile, e);
            }

            if (file.exists()) {
                try (final InputStream is = Files.newInputStream(file.toPath())) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } catch (final IOException e) {
                    log.warn("Failed to load custom localization file | " + langsFile, e);
                }
            }

            if (!props.isEmpty()) {
                this.language.put(locale, props);
            }
        }

        if (this.locales.length == 0) {
            final IllegalStateException e = new IllegalStateException("No locales loaded");
            log.error("Failed to load localization file", e);
            throw e;
        }

        log.info("Loaded " + this.language.size() + " languages");
    }

    /**
     * Sets the list of locales for the application.
     * Converts all supplied locale strings to lowercase and stores them in the locales array.
     *
     * @param locales an array of locale strings to be set for the application
     */
    public void setLocales(final String... locales) {
        this.locales = Arrays.stream(locales).map(String::toLowerCase).toArray(String[]::new);
    }

    /**
     * Sets the default locale for the application.
     * The provided locale string will be transformed to lowercase and stored as the default locale.
     *
     * @param defaultLocale the default locale string to be set; must not be null
     */
    public void setDefaultLocale(@NotNull final String defaultLocale) {
        this.defaultLocale = defaultLocale.toLowerCase();
    }

    /**
     * Reloads the localization properties for the application.
     * <p>
     * This method clears the current localization data and re-initializes it by invoking the
     * {@code load} method. It ensures the latest localization information is fetched and updated
     * for all configured locales. This is particularly useful for dynamic updates or reloading
     * localization files without restarting the application.
     * <p>
     * Thread-Safety:
     * This method is synchronized to ensure thread-safe access and modification of the shared
     * localization data structure.
     */
    @Override
    public synchronized void reload() {
        this.language.clear();
        this.load();
    }

    /**
     * Retrieves a localized string based on the default locale, identifier, and optional arguments.
     * This method supports dynamic message replacement and nested localization lookups.
     *
     * @param i18n an {@link I18n} object containing the identifier and optional arguments; must not be null
     * @return the localized string, or null if it could not be retrieved for the given locale and fallback logic
     */
    @Nullable
    public String get(@NotNull final I18n i18n) {
        return this.get(this.defaultLocale, false, i18n);
    }

    /**
     * Retrieves a localized string based on the provided locale, identifier, and optional arguments.
     * This method supports dynamic message replacement and nested localization lookups.
     *
     * @param locale the locale identifier to use for localization; must not be null
     * @param id     the identifier for the localized string; must not be null
     * @param args   the optional arguments to format the localized string; must not be null
     * @return the localized string, or null if it could not be retrieved for the given locale and fallback logic
     */
    @Nullable
    private String get(@NotNull final String locale, @NotNull final String id, @NotNull final Object... args) {
        final Properties localeSet = this.language.get(locale.toLowerCase());
        if (localeSet == null) {
            return null;
        }

        String i18n = localeSet.getProperty(id);
        if (i18n == null) {
            return null;
        }

        final Matcher m2 = I18nUtils.REGEX_I18N.matcher(i18n);
        if (m2.find()) {
            i18n = m2.replaceAll(this.get(locale, m2.group().substring(2, m2.group().length() - 1)));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] instanceof I18nObject) {
                args[i] = ((I18nObject) args[i]).build(this, locale);
            }

            if (args[i] == null) {
                continue;
            }

            i18n = i18n.replace("<args[" + i + "]>", String.valueOf(args[i]));
        }

        i18n = i18n.replace("\\n", "\n");

        return i18n;
    }

    /**
     * Retrieves a localized string based on the provided locale, optional fallback to default locale,
     * and an {@link I18n} object containing the identifier and optional arguments.
     * This method supports dynamic message replacement and nested localization lookups.
     * If no string is found for the given locale and {@code defaultLocale} is true, it attempts to retrieve
     * the string using the default locale specified in the containing class.
     *
     * @param locale        the locale identifier to use for localization; must not be null
     * @param defaultLocale a flag indicating whether to fall back to the default locale if {@code locale} is unavailable
     * @param i18n          an {@link I18n} object containing the identifier and optional arguments; must not be null
     * @return the localized string, or null if it could not be retrieved for the given locale and fallback logic
     */
    @Nullable
    public String get(@NotNull final String locale, final boolean defaultLocale, @NotNull final I18n i18n) {
        final String result = this.get(locale, i18n);
        if (result != null || !defaultLocale) {
            return result;
        }
        return this.get(this.defaultLocale, i18n);
    }

    /**
     * Retrieves a localized string based on the provided locale, identifier, and optional arguments.
     * This method supports dynamic message replacement and nested localization lookups.
     *
     * @param locale the locale identifier to use for localization; must not be null
     * @param i18n   an {@link I18n} object containing the identifier and optional arguments; must not be null
     * @return the localized string, or null if it could not be retrieved for the given locale and fallback logic
     */
    @Nullable
    public String get(@NotNull final String locale, @NotNull final I18n i18n) {
        return this.get(locale, true, i18n);
    }
}
