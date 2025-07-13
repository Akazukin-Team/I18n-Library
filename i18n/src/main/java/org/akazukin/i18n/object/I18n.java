package org.akazukin.i18n.object;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.I18nLang;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Constructs a new I18n instance with the specified message identifier and optional arguments.
     * <p>
     * This constructor creates an immutable internationalization object that
     * can be used to generate localized messages.
     * The arguments are stored as-is and will be used for formatting the message.
     *
     * @param id   the message identifier used to look up the localized message template
     * @param args optional arguments for dynamic content generation in the message
     */
    public I18n(final String id, final Object... args) {
        this.id = id;
        this.args = args;
    }

    /**
     * Creates a new I18n instance with the specified message identifier and optional arguments.
     * <p>
     * This static factory method provides a convenient way
     * to create I18n instances without using the {@code new} keyword.
     * It is functionally equivalent to calling the constructor directly
     * but offers better readability in method chaining scenarios.
     *
     * @param id   the message identifier used to look up the localized message template
     * @param args optional arguments for dynamic content generation in the message
     * @return a new I18n instance configured with the provided identifier and arguments
     */
    public static I18n of(final String id, final Object... args) {
        return new I18n(id, args);
    }

    /**
     * Builds a localized string for the specified locales.
     * <p>
     * This method attempts to resolve the localized string for the message identifier
     * using the provided locales in order of preference.
     * The message arguments are formatted according to the locale-specific formatting rules.
     * If none of the locales can provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale, or {@code null} if none can be resolved
     */
    @Override
    public String build(final II18nFormatter formatter, final I18nLang[] locales) {
        return formatter.formatMessage(this.id, locales, this.args);
    }

    /**
     * Builds a localized string for the specified locales, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string for the message identifier
     * using the provided locales in order of preference.
     * The message arguments are formatted according to the locale-specific formatting rules.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale
     */
    @Override
    public @NotNull String buildRequired(final II18nFormatter formatter, final I18nLang[] locales) throws I18nLocaleNotFoundException {
        return formatter.formatMessageThrown(this.id, locales, this.args);
    }
}
