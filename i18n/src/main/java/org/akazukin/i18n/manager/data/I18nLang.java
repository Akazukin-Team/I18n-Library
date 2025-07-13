package org.akazukin.i18n.manager.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * Represents a language used for i18n.
 * Encapsulates the language identifier and its display name.
 * <p>
 * This class is immutable.
 */
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class I18nLang {
    public static final I18nLang FALLBACK = new I18nLang("default", "Default");
    public static final I18nLang[] EMPTY_ARR = {};

    @NonNull
    String id;
    String displayName;

    public boolean equalsId(final I18nLang other) {
        return Objects.equals(this.id, other.id);
    }
}
