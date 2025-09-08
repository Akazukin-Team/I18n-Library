package org.akazukin.i18n.utils;

import lombok.experimental.UtilityClass;
import org.akazukin.i18n.manager.data.II18nEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

@UtilityClass
public class I18nValidatorUtils {
    private final String KEY_REGEX = "[a-z0-9][a-zA-Z0-9_-]*";
    public final String ID_REGEX = KEY_REGEX + "(\\." + KEY_REGEX + ")*";
    private final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    public boolean isValid(@NotNull final II18nEntry entry) {
        return entry.getEntries()
                .keySet()
                .stream()
                .allMatch(I18nValidatorUtils::isValidId);
    }

    public boolean isValidId(@NotNull final String id) {
        return ID_PATTERN.matcher(id).matches();
    }

    public boolean isValidIds(@NotNull final String... ids) {
        return Arrays.stream(ids)
                .allMatch(I18nValidatorUtils::isValidId);
    }

    public boolean isValidIds(@NotNull final Collection<String> ids) {
        return ids.stream()
                .allMatch(I18nValidatorUtils::isValidId);
    }
}
