package org.akazukin.i18n.manager;

import org.akazukin.i18n.exception.I18nLocaleAlreadyExistsException;
import org.akazukin.i18n.exception.IllegalI18nKeyException;
import org.akazukin.i18n.manager.data.I18nEntry;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.util.interfaces.Reloadable;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for managing internationalization (i18n) entries across different languages.
 * This interface provides the core functionality for loading, storing, and managing
 * localized data entries for various locales in an i18n system.
 * <p>
 * This interface extends {@link Reloadable} to provide reload functionality
 * for refreshing all loaded entries from their source files.
 */
public interface IEntryManager extends Reloadable {
    /**
     * Loads localization data for the specified language.
     * Creates a new {@link I18nEntry} and loads its content from the
     * corresponding language resource file.
     *
     * @param lang the language to load. Must not be {@code null}.
     * @throws I18nLocaleAlreadyExistsException if an entry for this language already exists
     * @throws IllegalI18nKeyException          if the loaded data contains invalid i18n keys
     */
    void load(I18nLang lang) throws I18nLocaleAlreadyExistsException, IllegalI18nKeyException;

    /**
     * Loads localization data for multiple languages in a single operation.
     * This is a convenience method that calls {@link #load(I18nLang)}
     * for each provided language.
     *
     * @param langs the languages to load. Must not be {@code null}.
     * @throws I18nLocaleAlreadyExistsException if any entry for these languages already exists
     * @throws IllegalI18nKeyException          if any loaded data contains invalid i18n keys
     */
    void load(I18nLang... langs) throws I18nLocaleAlreadyExistsException, IllegalI18nKeyException;

    void reload(I18nLang lang);

    /**
     * Reloads all currently loaded entries from their source files.
     * This method refreshes the localization data
     * by re-reading all loaded language files from disk/classpath.
     * Useful for updating translations without restarting the application.
     * <p>
     * All existing entries are refreshed with their latest file contents.
     * If a file is no longer available, the entry will be cleared but not removed.
     *
     * @throws IllegalI18nKeyException if any reloaded data contains invalid i18n keys
     */
    @Override
    void reload();

    /**
     * Retrieves the i18n entry for the specified language.
     * Returns the entry containing all localized strings for the given language.
     *
     * @param lang the language to retrieve. Must not be {@code null}.
     * @return the i18n entry for the specified language, or {@code null} if no entry exists
     */
    @Nullable
    I18nEntry getEntry(I18nLang lang);

    /**
     * Stores or updates an i18n entry in the manager.
     * If an entry for the same language already exists, it will be replaced.
     * This method allows programmatic addition of localization entries.
     *
     * @param entry the entry to store. Must not be {@code null}.
     */
    void putEntry(I18nEntry entry);

    /**
     * Removes the i18n entry for the specified language from the manager.
     * The entry is removed from memory but the source file remains unchanged.
     *
     * @param lang the language whose entry should be removed. Must not be {@code null}.
     */
    void removeEntry(I18nLang lang);

    /**
     * Checks whether an entry exists for the specified language.
     * This method can be used to verify if a language is currently loaded
     * before attempting to retrieve its entry.
     *
     * @param lang the language to check. Must not be {@code null}.
     * @return {@code true} if an entry exists for the specified language, {@code false} otherwise
     */
    boolean hasEntry(I18nLang lang);

    /**
     * Retrieves all currently loaded i18n entries.
     * Returns an array containing all entries that have been loaded into the manager.
     * The returned array is a copy and modifications to it will not affect the manager.
     *
     * @return an array of all loaded i18n entries. Never {@code null}, but may be empty.
     */
    I18nEntry[] getEntries();
}
