package org.akazukin.i18n.object;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.I18nManager;
import org.akazukin.util.interfaces.Reloadable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A holder class for managing a collection of {@link I18nManager} instances.
 * Provides methods to retrieve internationalized text based on the provided {@link I18nObject} and locale.
 * Implements the {@link Reloadable} interface to support reloading functionality.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nUtilsHolder implements Reloadable {
    List<I18nManager> i18nUtils = new CopyOnWriteArrayList<>();

    public I18nUtilsHolder(final I18nManager... i18nUtils) {
        this.i18nUtils.addAll(Arrays.asList(i18nUtils));
    }

    /**
     * Retrieves a localized string for the specified {@link I18nObject}.
     * The method iterates through the internal collection of {@link I18nManager} instances to build the localized string.
     * If a result is found, it is immediately returned. If no matching result is found,
     * the method will return null.
     *
     * @param i18n the {@link I18nObject} representing the resource to be localized
     * @return the localized string for the given locale, or null if not found
     */
    @Nullable
    public String get(final I18nObject i18n) {
        for (final I18nManager i18nUtil : this.i18nUtils) {
            final String result = i18n.build(i18nUtil);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Retrieves a localized string for the specified locale and {@link I18nObject}.
     * The method iterates through the internal collection of {@link I18nManager} instances to build the localized string.
     * If a matching localized string is not found,
     * it attempts to retrieve the string using the default locale.
     *
     * @param locale the locale to use for retrieving the localized string
     * @param i18n   the {@link I18nObject} representing the resource to be localized
     * @return the localized string for the given locale, or null if not found
     */
    @Nullable
    public String get(final String locale, final I18nObject i18n) {
        return this.get(locale, i18n, true);
    }

    /**
     * Retrieves a localized string for the specified locale and {@link I18nObject}.
     * The method iterates through the internal collection of {@link I18nManager} instances to build the localized string.
     * If a matching localized string is not found and the `defaultLocale` flag is true,
     * it attempts to retrieve the string using the default locale.
     *
     * @param locale        the locale to use for retrieving the localized string
     * @param i18n          the {@link I18nObject} representing the resource to be localized
     * @param defaultLocale a boolean flag indicating whether to use the default locale as a fallback if no match is found
     * @return the localized string for the given locale, or null if not found
     */
    @Nullable
    public String get(final String locale, final I18nObject i18n, final boolean defaultLocale) {
        for (final I18nManager i18nUtil : this.i18nUtils) {
            final String result = i18n.build(i18nUtil, locale);
            if (result != null) {
                return result;
            }
        }

        if (defaultLocale) {
            for (final I18nManager i18nUtil : this.i18nUtils) {
                if (locale.equalsIgnoreCase(i18nUtil.getDefaultLocale())) {
                    continue;
                }

                final String result = i18n.build(i18nUtil);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Reloads all {@link I18nManager} instances contained within this holder.
     * This method iterates over the collection of {@link I18nManager} objects
     * and invokes their respective {@code reload} methods to refresh any
     * internal state or cached data related to internationalization.
     */
    @Override
    public void reload() {
        this.i18nUtils.forEach(I18nManager::reload);
    }
}
