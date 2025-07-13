package org.akazukin.i18n.manager.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
 * Instances of this class are immutable with respect to the {@link I18nLang}.
 * However, the map of entries can be modified after creation.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class I18nEntry {
    public static final I18nEntry[] EMPTY_ARR = {};

    @NonNull
    final I18nLang lang;
    @Setter
    @Nullable
    Map<String, String> entries = new HashMap<>();

    @Nullable
    public String getEntry(final String id) {
        synchronized (this) {
            if (this.entries == null) {
                return null;
            }
            return this.entries.get(id);
        }
    }

    public boolean hasEntryId(final String id) {
        synchronized (this) {
            if (this.entries == null) {
                return false;
            }
            return this.entries.containsKey(id);
        }
    }

    @NotNull
    public String[] getEntryIds() {
        synchronized (this) {
            if (this.entries == null) {
                return Constants.EMPTY_STR_ARR;
            }
            return this.entries.keySet().toArray(Constants.EMPTY_STR_ARR);
        }
    }

    @Override
    public int hashCode() {
        return this.lang.hashCode();
    }
}
