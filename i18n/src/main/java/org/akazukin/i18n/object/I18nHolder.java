package org.akazukin.i18n.object;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.I18nManager;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.util.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A holder class that implements {@link I18nObject} and is designed to manage and compose multiple
 * {@code I18nObject} instances, allowing for flexible customization and assembly of internationalized text.
 * <p>
 * Instances of this class provide methods to set additional text or {@code I18nObject}
 * elements before, after, or between the managed {@link I18nObject} instances. It enables
 * the creation of complex, localized messages by combining multiple pieces of text and
 * {@link I18nObject}s.
 * <p>
 * The {@code build} method constructs the final localized string using the specified locale,
 * concatenating the elements in the desired sequence with any optional prefixes, suffixes, or
 * connectors specified within the instance.
 * <p>
 * This class relies on {@link I18nManager} for formatting or localizing during the build process.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public final class I18nHolder implements I18nObject {
    final I18nObject[] i18ns;
    String first;
    I18nObject firstI18n;
    String last;
    I18nObject lastI18n;
    String concat;
    I18nObject concatI18n;
    String before;
    I18nObject beforeI18n;
    String after;
    I18nObject afterI18n;

    public static I18nHolder of(final I18nObject... i18ns) {
        return new I18nHolder(i18ns);
    }

    /**
     * Sets the first element in the sequence to the specified plain text.
     * <p>
     * This method allows you to prepend a custom plain text at the beginning of the sequence managed by this holder.
     * If this method is used, any previously set {@code I18nObject} for the first element will be cleared.
     * During the {@code build} process, this text will be directly added at the start of the final composed string
     * without further localization.
     * </p>
     *
     * @param first the plain text to set as the first element in the sequence
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setFirst(final String first) {
        this.first = first;
        this.firstI18n = null;
        return this;
    }

    /**
     * Sets the first element in the sequence to the specified {@code I18nObject}.
     * <p>
     * This method allows you to prepend a custom {@code I18nObject} element at the beginning of the sequence managed by this holder.
     * If this method is used, any previously set plain text for the first element will be cleared.
     * During the {@code build} process, the value of this element will be localized using the provided locale settings
     * and added at the start of the final composed string.
     * </p>
     *
     * @param first the {@code I18nObject} to set as the first element in the sequence
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setFirst(final I18nObject first) {
        this.first = null;
        this.firstI18n = first;
        return this;
    }

    /**
     * Sets the last element in the sequence to the specified plain text.
     * <p>
     * This method allows you to append a custom plain text as the last element in the sequence managed by this holder.
     * If this method is used, any previously set {@code I18nObject} for the last element will be cleared.
     * During the {@code build} process, this text will be directly added at the end of the final composed string without further localization.
     * </p>
     *
     * @param last the plain text to set as the last element in the sequence
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setLast(final String last) {
        this.last = last;
        this.lastI18n = null;
        return this;
    }

    /**
     * Sets the last element in the sequence to the specified {@code I18nObject}.
     * <p>
     * This method allows you to append a custom {@code I18nObject} element at the end of the sequence managed by this holder.
     * If this method is used, any previously set plain text for the last element will be cleared.
     * During the {@code build} process, the value of this element will be localized using the provided locale settings
     * and added at the end of the final composed string.
     * </p>
     *
     * @param last the {@code I18nObject} to set as the last element in the sequence
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setLast(final I18nObject last) {
        this.last = null;
        this.lastI18n = last;
        return this;
    }

    /**
     * Sets the connector element to the specified plain text.
     * <p>
     * This method allows you to specify a custom plain text to act as a connector between
     * the elements managed by this holder. If this method is used, any previously set {@code I18nObject} for the connector
     * will be cleared.
     * During the {@code build} process, this text will be directly added as the connector between consecutive elements
     * without further localization.
     * </p>
     *
     * @param concat the plain text to set as the connector element
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setConcat(final String concat) {
        this.concat = concat;
        this.concatI18n = null;
        return this;
    }

    /**
     * Sets the connector element to the specified {@code I18nObject}.
     * <p>
     * This method allows you to specify a custom {@code I18nObject} to act as a connector between
     * the elements managed by this holder. If this method is used, any previously set plain text for the connector
     * will be cleared.
     * During the {@code build} process, the value of this connector will be localized and inserted
     * between consecutive elements in the sequence.
     * </p>
     *
     * @param concat the {@code I18nObject} to set as the connector element
     * @return the current {@code I18nHolder} instance for method chaining
     */

    public I18nHolder setConcat(final I18nObject concat) {
        this.concat = null;
        this.concatI18n = concat;
        return this;
    }

    /**
     * Sets a suffix element to the specified plain text, to be added after each managed element.
     * <p>
     * This method allows you to specify a custom plain text that will act as a suffix
     * for every managed element. If this method is used, any previously set {@code I18nObject} for the suffix
     * will be cleared.
     * During the {@code build} process, this text will be directly added as a suffix
     * for each element in the sequence without further localization.
     * </p>
     *
     * @param after the plain text to set as the suffix for each managed element
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setAfter(final String after) {
        this.after = after;
        this.afterI18n = null;
        return this;
    }

    /**
     * Sets a suffix element to the specified {@code I18nObject}, to be added after each managed element.
     * <p>
     * This method allows you to specify a custom {@code I18nObject} that will act as a suffix
     * for every managed element. If this method is used, any previously set plain text for the suffix
     * will be cleared.
     * During the {@code build} process, the value of this suffix will be localized and added
     * after each element in the sequence.
     * </p>
     *
     * @param after the {@code I18nObject} to set as the suffix for each managed element
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setAfter(final I18nObject after) {
        this.after = null;
        this.afterI18n = after;
        return this;
    }

    /**
     * Sets a prefix element to the specified plain text, to be added before each managed element.
     * <p>
     * This method allows you to specify a custom plain text that will act as a prefix
     * for every managed element. If this method is used, any previously set {@code I18nObject} for the prefix
     * will be cleared.
     * During the {@code build} process, this text will be directly added as a prefix
     * for each element in the sequence without further localization.
     * </p>
     *
     * @param before the plain text to set as the prefix for each managed element
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setBefore(final String before) {
        this.before = before;
        this.beforeI18n = null;
        return this;
    }

    /**
     * Sets a prefix element to the specified {@code I18nObject}, to be added before each managed element.
     * <p>
     * This method allows you to specify a custom {@code I18nObject} that will act as a prefix
     * for every managed element. If this method is used, any previously set plain text for the prefix
     * will be cleared.
     * During the {@code build} process, the value of this prefix will be localized and added
     * before each element in the sequence.
     * </p>
     *
     * @param before the {@code I18nObject} to set as the prefix for each managed element
     * @return the current {@code I18nHolder} instance for method chaining
     */
    public I18nHolder setBefore(final I18nObject before) {
        this.before = null;
        this.beforeI18n = before;
        return this;
    }

    /**
     * Builds a localized string for the specified locales.
     * <p>
     * This method attempts to resolve the localized string by composing all managed {@link I18nObject} instances
     * using the provided locales in order of preference.
     * The method constructs the final string by concatenating
     * the first element, the managed elements with their prefixes and suffixes, connectors between them,
     * and the last element in the configured sequence.
     * If none of the locales can provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale, or {@code null} if none can be resolved
     */
    @Override
    public String build(final II18nFormatter formatter, final I18nLang[] locales) {
        return (this.first != null
                ? this.first
                : (this.firstI18n != null ? this.firstI18n.build(formatter, locales) : ""))
                + ArrayUtils.join(
                (this.concat != null
                        ? this.concat
                        : (this.concatI18n != null ? this.concatI18n.build(formatter, locales) : "")),
                Arrays
                        .stream(this.i18ns)
                        .map(i18n ->
                                (this.before != null
                                        ? this.before
                                        : (this.beforeI18n != null
                                                ? this.beforeI18n.build(formatter, locales)
                                                : "")) +
                                        i18n.build(formatter, locales)
                                        + (this.after != null
                                        ? this.after
                                        : (this.afterI18n != null
                                                ? this.afterI18n.build(formatter, locales)
                                                : "")))
                        .toArray(String[]::new))
                + (this.last != null
                ? this.last
                : (this.lastI18n != null ? this.lastI18n.build(formatter, locales) : ""));
    }

    /**
     * Builds a localized string for the specified locales, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string by composing all managed {@link I18nObject} instances
     * using the provided locales in order of preference.
     * The method constructs the final string by concatenating
     * the first element, the managed elements with their prefixes and suffixes, connectors between them,
     * and the last element in the configured sequence.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string
     * @param locales   the array of locales to attempt, in order of preference
     * @return the localized string for the first available locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale
     */
    @Override
    public @NotNull String buildRequired(final II18nFormatter formatter, final I18nLang[] locales) throws I18nLocaleNotFoundException {
        return (this.first != null
                ? this.first
                : (this.firstI18n != null ? this.firstI18n.buildRequired(formatter, locales) : ""))
                + ArrayUtils.join(
                (this.concat != null
                        ? this.concat
                        : (this.concatI18n != null ? this.concatI18n.buildRequired(formatter, locales) : "")),
                Arrays
                        .stream(this.i18ns)
                        .map(i18n ->
                                (this.before != null
                                        ? this.before
                                        : (this.beforeI18n != null
                                                ? this.beforeI18n.buildRequired(formatter, locales)
                                                : "")) +
                                        i18n.buildRequired(formatter, locales)
                                        + (this.after != null
                                        ? this.after
                                        : (this.afterI18n != null
                                                ? this.afterI18n.buildRequired(formatter, locales)
                                                : "")))
                        .toArray(String[]::new))
                + (this.last != null
                ? this.last
                : (this.lastI18n != null ? this.lastI18n.buildRequired(formatter, locales) : ""));
    }
}
