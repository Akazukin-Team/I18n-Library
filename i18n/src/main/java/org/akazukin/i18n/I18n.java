package org.akazukin.i18n;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class I18n implements I18nObject {
    private final String id;
    private final Object[] args;

    public I18n(final String id) {
        this(id, new Object[]{});
    }

    public I18n(final String id, final Object... args) {
        this.id = id;
        this.args = args;
    }

    public static I18n of(final String id, final Object... args) {
        return new I18n(id, args);
    }

    @Override
    public String build(final I18nUtils i18nUtils, final String locale, final boolean defaultLocale) {
        final String data = i18nUtils.get(locale, defaultLocale, this);
        return data != null ? data : this.toString();
    }
}
