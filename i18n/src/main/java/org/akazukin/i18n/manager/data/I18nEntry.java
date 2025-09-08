package org.akazukin.i18n.manager.data;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entry in i18n.
 * Each entry is associated with a specific language and contains a collection of localized strings.
 * <p>
 * Instances of this class are immutable with respect to the {@link II18nLang}.
 * However, the map of entries can be modified after creation.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public final class I18nEntry implements II18nEntry {
    @NonNull
    final II18nLang lang;
    @Setter
    @Nullable
    Map<String, String> entries;

    public I18nEntry(@NonNull final II18nLang lang) {
        this.lang = lang;
        this.entries = new HashMap<>();
    }

    @Override
    public synchronized @Nullable String getEntry(@NotNull final String id) {
        if (this.entries == null) {
            return null;
        }
        return this.entries.get(id);
    }

    @Override
    public synchronized boolean hasEntryId(@NotNull final String id) {
        if (this.entries == null) {
            return false;
        }
        return this.entries.containsKey(id);
    }

    @Override
    public synchronized @NotNull String[] getEntryIds() {
        if (this.entries == null) {
            return Constants.EMPTY_STR_ARR;
        }
        return this.entries.keySet()
                .toArray(Constants.EMPTY_STR_ARR);
    }

    @Override
    public int hashCode() {
        return this.lang.hashCode();
    }
}
