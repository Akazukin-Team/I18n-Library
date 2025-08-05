package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.i18n.config.II18nManagerConfig;
import org.akazukin.i18n.utils.I18nValidatorUtils;
import org.akazukin.util.interfaces.Reloadable;
import org.jetbrains.annotations.NotNull;

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
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nManager implements II18nManager {
    public static final Pattern REGEX_I18N = Pattern.compile("<\\$(" + I18nValidatorUtils.ID_REGEX + ")>");

    IEntryManager entryMgr;
    II18nFormatter formatter;

    private I18nManager(@NotNull final II18nManagerConfig config,
                        @NotNull final IEntryManager entryMgr, @NotNull final II18nFormatter formatter) {
        this.entryMgr = entryMgr;
        this.entryMgr.load(config.getLangs());
        this.formatter = formatter;
        this.formatter.setFallbackLang(config.getFallbackLang());
    }

    /**
     * Creates a new {@link II18nManager} instance using the specified configuration.
     * This method initializes the necessary parts for managing and formatting
     * internationalized entries, such as the entry manager and formatter.
     *
     * @param config the configuration for the i18n manager.
     *               Must not be {@code null}.
     * @return a new {@link II18nManager} instance configured using the provided parameters.
     */
    public static II18nManager create(@NotNull final II18nManagerConfig config) {
        final IEntryManager entryMgr = new EntryManager(config);
        final II18nFormatter formatter = new I18nFormatter(entryMgr);
        return create(config, entryMgr, formatter);
    }

    /**
     * Creates a new {@link II18nManager} instance using the specified configuration and components.
     *
     * @param config    the configuration for the i18n manager.
     *                  Must not be {@code null}.
     * @param entryMgr  the entry manager responsible for managing internationalization entries.
     *                  Must not be {@code null}.
     * @param formatter the formatter used to process and format localized messages.
     *                  Must not be {@code null}.
     * @return a new {@link II18nManager} instance initialized with the given parameters.
     */
    public static II18nManager create(@NotNull final II18nManagerConfig config,
                                      @NotNull final IEntryManager entryMgr, @NotNull final II18nFormatter formatter) {
        return new I18nManager(config, entryMgr, formatter);
    }
}
