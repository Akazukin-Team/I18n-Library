package org.akazukin.i18n.manager.data;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a language used for i18n.
 * Encapsulates the language identifier and its display name.
 * <p>
 * This class is immutable.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@ToString
@EqualsAndHashCode
public final class I18nLang implements II18nLang {
    String id;
    String displayName;

    public I18nLang(
            @NotNull final String id, @Nullable final String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public boolean equalsId(@NotNull final II18nLang other) {
        return Objects.equals(this.getId(), other.getId());
    }
}
