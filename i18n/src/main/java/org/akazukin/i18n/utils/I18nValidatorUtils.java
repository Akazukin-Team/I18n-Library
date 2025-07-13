package org.akazukin.i18n.utils;

import lombok.experimental.UtilityClass;
import org.akazukin.i18n.manager.data.I18nEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

@UtilityClass
public class I18nValidatorUtils {
    private static final String KEY_REGEX = "[a-z0-9][a-zA-Z0-9]*";
    public static final String ID_REGEX = KEY_REGEX + "(" + KEY_REGEX + ")*";
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    public boolean isValid(final I18nEntry entry) {
        return entry.getEntries()
                .keySet()
                .stream()
                .allMatch(I18nValidatorUtils::isValidId);
    }

    public boolean isValidId(final String id) {
        return ID_PATTERN.matcher(id).matches();
    }

    public boolean isValidIds(final String... ids) {
        return Arrays.stream(ids)
                .allMatch(I18nValidatorUtils::isValidId);
    }

    public boolean isValidIds(final Collection<String> ids) {
        return ids.stream()
                .allMatch(I18nValidatorUtils::isValidId);
    }
}
