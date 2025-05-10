package org.akazukin.i18n;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Represents an internationalization (i18n) resource that encapsulates a message identifier and
 * optional arguments for dynamic content generation.
 */
@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class I18n implements I18nObject {
    String id;
    Object[] args;

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

    /**
     * Builds the localized string representation of this internationalization (i18n) object
     * using the specified utilities and locale information.
     *
     * @param i18nUtils     the utility to fetch localized messages
     * @param locale        the locale identifier specifying the target language and region
     * @param defaultLocale a flag indicating whether to use a default fallback locale
     * @return the localized message string for this i18n object
     */
    @Override
    public String build(final I18nUtils i18nUtils, final String locale, final boolean defaultLocale) {
        return i18nUtils.get(locale, defaultLocale, this);
    }
}
