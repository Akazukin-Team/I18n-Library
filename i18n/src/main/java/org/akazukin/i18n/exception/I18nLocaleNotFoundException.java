package org.akazukin.i18n.exception;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.i18n.object.I18nObject;

import java.util.Arrays;

/**
 * Exception thrown when a requested i18n locale is not found.
 * <p>
 * This exception is thrown when attempting to retrieve i18n content for a specific
 * language or key combination that does not exist in the available locale data.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nLocaleNotFoundException extends IllegalStateException {
    private static final long serialVersionUID = -4960131954566653551L;
    I18nLang[] langs;
    String keys;
    I18nObject i18n;

    /**
     * Constructs a new I18nLocaleNotFoundException with the specified languages and key.
     *
     * @param langs the array of languages where the key was not found
     * @param key the key that was not found in the specified languages
     * @throws NullPointerException if langs or key is null
     */
    public I18nLocaleNotFoundException(@NonNull final I18nLang[] langs, @NonNull final String key) {
        super("The i18n key is not found.  | Lang: " + Arrays.toString(langs) + "  | Key: " + key);
        this.keys = key;
        this.i18n = null;
        this.langs = langs;
    }

    /**
     * Constructs a new I18nLocaleNotFoundException with the specified languages and i18n object.
     *
     * @param langs the array of languages where the i18n object was not found
     * @param i18n the i18n object that was not found in the specified languages
     * @throws NullPointerException if langs or i18n is null
     */
    public I18nLocaleNotFoundException(@NonNull final I18nLang[] langs, @NonNull final I18nObject i18n) {
        super("The i18n key is not found.  | Lang: " + Arrays.toString(langs) + "  | I18n: " + i18n);
        this.keys = null;
        this.i18n = i18n;
        this.langs = langs;
    }
}
