package org.akazukin.i18n.exception;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.data.I18nLang;

import java.util.Arrays;


/**
 * Exception thrown when an invalid i18n key is encountered.
 * <p>
 * This exception is thrown when attempting to use a key that does not conform
 * to the expected format or contains invalid characters for i18n operations.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IllegalI18nKeyException extends IllegalStateException {
    private static final long serialVersionUID = -4960131954566653551L;
    I18nLang lang;
    String[] keys;

    /**
     * Constructs a new IllegalI18nKeyException with the specified language and key.
     *
     * @param lang the language context where the invalid key was encountered
     * @param key the invalid key that caused this exception
     * @throws NullPointerException if lang or key is null
     */
    public IllegalI18nKeyException(@NonNull final I18nLang lang, @NonNull final String key) {
        this(lang, new String[]{key});
    }

    /**
     * Constructs a new IllegalI18nKeyException with the specified language and multiple keys.
     *
     * @param lang the language context where the invalid keys were encountered
     * @param keys the invalid keys that caused this exception
     * @throws NullPointerException if lang or keys is null
     */
    public IllegalI18nKeyException(@NonNull final I18nLang lang, @NonNull final String[] keys) {
        super("The i18n key is invalid.  | Langs: " + lang + "  | Key: " + Arrays.toString(keys));
        this.keys = keys;
        this.lang = lang;
    }
}
