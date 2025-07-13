package org.akazukin.i18n.exception;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.data.I18nLang;

/**
 * Exception thrown when attempting to load an i18n locale that already exists.
 * <p>
 * This exception is thrown when trying to load a language entry that has already
 * been loaded into the entry manager. This prevents duplicate entries and ensures
 * data integrity in the i18n system.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nLocaleAlreadyExistsException extends IllegalStateException {
    private static final long serialVersionUID = 5450135238787834806L;
    I18nLang lang;

    /**
     * Constructs a new I18nLocaleAlreadyExistsException with the specified language.
     *
     * @param lang the language that already exists in the entry manager
     * @throws NullPointerException if lang is null
     */
    public I18nLocaleAlreadyExistsException(@NonNull final I18nLang lang) {
        super("The i18n key is not found.  | Lang: " + lang);
        this.lang = lang;
    }
}
