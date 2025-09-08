package org.akazukin.i18n.object;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.I18nManager;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.util.utils.ArrayUtils;
import org.akazukin.util.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * A holder class that implements {@link II18nObject} and is designed to manage and compose multiple
 * {@link II18nObject} instances, allowing for flexible customization and assembly of internationalized text.
 * <p>
 * Instances of this class provide methods to set additional text or {@link II18nObject}
 * elements before, after, or between the managed {@link II18nObject} instances. It enables
 * the creation of complex, localized messages by combining multiple pieces of text and
 * {@link II18nObject}s.
 * <p>
 * The {@code build} method constructs the final localized string using the specified locale,
 * concatenating the elements in the desired sequence with any optional prefixes, suffixes, or
 * connectors specified within the instance.
 * <p>
 * This class relies on {@link I18nManager} for formatting or localizing during the build process.
 */
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class I18nHolder implements II18nObject {
    final II18nObject[] i18ns;
    String first;
    II18nObject firstI18n;
    String last;
    II18nObject lastI18n;
    String concat;
    II18nObject concatI18n;
    String before;
    II18nObject beforeI18n;
    String after;
    II18nObject afterI18n;

    private I18nHolder(@NotNull final II18nObject[] i18ns) {
        this.i18ns = i18ns;
    }

    /**
     * Creates a new instance of {@link  I18nHolder} with the specified {@link II18nObject} elements.
     *
     * @param i18ns one or more {@link II18nObject} instances to be managed by the {@link I18nHolder}.
     *              Must not be null.
     * @return a new {@link I18nHolder} containing the provided {@link II18nObject} elements.
     */
    public static @NotNull I18nHolder of(@NotNull final II18nObject... i18ns) {
        return new I18nHolder(i18ns);
    }

    /**
     * Sets the first element in the sequence to the specified plain text.
     * <p>
     * This method allows you to prepend a custom plain text at the beginning of the sequence managed by this holder.
     * If this method is used, any previously set {@link II18nObject} for the first element will be cleared.
     * During the {@code build} process, this text will be directly added at the start of the final composed string
     * without further localization.
     * </p>
     *
     * @param first the plain text to set as the first element in the sequence
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setFirst(@Nullable final String first) {
        this.first = first;
        this.firstI18n = null;
        return this;
    }

    /**
     * Sets the first element in the sequence to the specified {@link II18nObject}.
     * <p>
     * This method allows you to prepend a custom {@link II18nObject} element at the beginning of the sequence managed by this holder.
     * If this method is used, any previously set plain text for the first element will be cleared.
     * During the {@code build} process, the value of this element will be localized using the provided locale settings
     * and added at the start of the final composed string.
     * </p>
     *
     * @param first the {@link II18nObject} to set as the first element in the sequence
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setFirst(@Nullable final II18nObject first) {
        this.first = null;
        this.firstI18n = first;
        return this;
    }

    /**
     * Sets the last element in the sequence to the specified plain text.
     * <p>
     * This method allows you to append a custom plain text as the last element in the sequence managed by this holder.
     * If this method is used, any previously set {@link II18nObject} for the last element will be cleared.
     * During the {@code build} process, this text will be directly added at the end of the final composed string without further localization.
     * </p>
     *
     * @param last the plain text to set as the last element in the sequence
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setLast(@Nullable final String last) {
        this.last = last;
        this.lastI18n = null;
        return this;
    }

    /**
     * Sets the last element in the sequence to the specified {@link II18nObject}.
     * <p>
     * This method allows you to append a custom {@link II18nObject} element at the end of the sequence managed by this holder.
     * If this method is used, any previously set plain text for the last element will be cleared.
     * During the {@code build} process, the value of this element will be localized using the provided locale settings
     * and added at the end of the final composed string.
     * </p>
     *
     * @param last the {@link II18nObject} to set as the last element in the sequence
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setLast(@Nullable final II18nObject last) {
        this.last = null;
        this.lastI18n = last;
        return this;
    }

    /**
     * Sets the connector element to the specified plain text.
     * <p>
     * This method allows you to specify a custom plain text to act as a connector between
     * the elements managed by this holder. If this method is used, any previously set {@link II18nObject} for the connector
     * will be cleared.
     * During the {@code build} process, this text will be directly added as the connector between consecutive elements
     * without further localization.
     * </p>
     *
     * @param concat the plain text to set as the connector element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setConcat(@Nullable final String concat) {
        this.concat = concat;
        this.concatI18n = null;
        return this;
    }

    /**
     * Sets the connector element to the specified {@link II18nObject}.
     * <p>
     * This method allows you to specify a custom {@link II18nObject} to act as a connector between
     * the elements managed by this holder. If this method is used, any previously set plain text for the connector
     * will be cleared.
     * During the {@code build} process, the value of this connector will be localized and inserted
     * between consecutive elements in the sequence.
     * </p>
     *
     * @param concat the {@link II18nObject} to set as the connector element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setConcat(@Nullable final II18nObject concat) {
        this.concat = null;
        this.concatI18n = concat;
        return this;
    }

    /**
     * Sets a suffix element to the specified plain text, to be added after each managed element.
     * <p>
     * This method allows you to specify a custom plain text that will act as a suffix
     * for every managed element. If this method is used, any previously set {@link II18nObject} for the suffix
     * will be cleared.
     * During the {@code build} process, this text will be directly added as a suffix
     * for each element in the sequence without further localization.
     * </p>
     *
     * @param after the plain text to set as the suffix for each managed element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setAfter(@Nullable final String after) {
        this.after = after;
        this.afterI18n = null;
        return this;
    }

    /**
     * Sets a suffix element to the specified {@link II18nObject}, to be added after each managed element.
     * <p>
     * This method allows you to specify a custom {@link II18nObject} that will act as a suffix
     * for every managed element. If this method is used, any previously set plain text for the suffix
     * will be cleared.
     * During the {@code build} process, the value of this suffix will be localized and added
     * after each element in the sequence.
     * </p>
     *
     * @param after the {@link II18nObject} to set as the suffix for each managed element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setAfter(@Nullable final II18nObject after) {
        this.after = null;
        this.afterI18n = after;
        return this;
    }

    /**
     * Sets a prefix element to the specified plain text, to be added before each managed element.
     * <p>
     * This method allows you to specify a custom plain text that will act as a prefix
     * for every managed element. If this method is used, any previously set {@link II18nObject} for the prefix
     * will be cleared.
     * During the {@code build} process, this text will be directly added as a prefix
     * for each element in the sequence without further localization.
     * </p>
     *
     * @param before the plain text to set as the prefix for each managed element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setBefore(@Nullable final String before) {
        this.before = before;
        this.beforeI18n = null;
        return this;
    }

    /**
     * Sets a prefix element to the specified {@link II18nObject}, to be added before each managed element.
     * <p>
     * This method allows you to specify a custom {@link II18nObject} that will act as a prefix
     * for every managed element. If this method is used, any previously set plain text for the prefix
     * will be cleared.
     * During the {@code build} process, the value of this prefix will be localized and added
     * before each element in the sequence.
     * </p>
     *
     * @param before the {@link II18nObject} to set as the prefix for each managed element
     * @return the current {@link I18nHolder} instance for method chaining
     */
    public @NotNull I18nHolder setBefore(@Nullable final II18nObject before) {
        this.before = null;
        this.beforeI18n = before;
        return this;
    }

    /**
     * Builds a localized string for the specified locales.
     * <p>
     * This method attempts to resolve the localized string by composing all managed {@link II18nObject} instances
     * using the provided locales in order of preference.
     * The method constructs the final string by concatenating
     * the first element, the managed elements with their prefixes and suffixes, connectors between them,
     * and the last element in the configured sequence.
     * If none of the locales can provide a result, {@code null} is returned.
     *
     * @param formatter the formatter to use for building the localized string.
     *                  Must not be {@code null}.
     * @param locales   the array of locales to attempt, in order of preference.
     *                  Must not be {@code null}.
     * @return the localized string for the first available locale, or {@code null} if none can be resolved
     */
    @Override
    public @NotNull String build(
            @NotNull final II18nFormatter formatter, @NotNull final II18nLang... locales) {
        final String first = StringUtils.toStringOrEmpty(
                this.firstI18n != null
                        ? this.firstI18n.build(formatter, locales)
                        : this.first);
        final String concat = StringUtils.toStringOrEmpty(
                this.concatI18n != null
                        ? this.concatI18n.build(formatter, locales)
                        : this.concat);
        final String before = StringUtils.toStringOrEmpty(
                this.beforeI18n != null
                        ? this.beforeI18n.build(formatter, locales)
                        : this.before);
        final String after = StringUtils.toStringOrEmpty(
                this.afterI18n != null
                        ? this.afterI18n.build(formatter, locales)
                        : this.after);
        final String last = StringUtils.toStringOrEmpty(
                this.lastI18n != null
                        ? this.lastI18n.build(formatter, locales)
                        : this.last);

        return first
                + ArrayUtils.join(
                concat,
                Arrays
                        .stream(this.i18ns)
                        .map(i18n ->
                                before
                                        + StringUtils.toStringOrEmpty(i18n.build(formatter, locales))
                                        + after)
                        .toArray(String[]::new))
                + last;
    }

    /**
     * Builds a localized string for the specified locales, throwing an exception if unsuccessful.
     * <p>
     * This method attempts to resolve the localized string by composing all managed {@link II18nObject} instances
     * using the provided locales in order of preference.
     * The method constructs the final string by concatenating
     * the first element, the managed elements with their prefixes and suffixes, connectors between them,
     * and the last element in the configured sequence.
     * Guarantees a non-null result or throws an exception.
     *
     * @param formatter the formatter to use for building the localized string.
     *                  Must not be {@code null}.
     * @param locales   the array of locales to attempt, in order of preference.
     *                  Must not be {@code null}.
     * @return the localized string for the first available locale. Must not be {@code null}.
     * @throws I18nLocaleNotFoundException if the message cannot be resolved for any locale
     */
    @Override
    public @NotNull String buildRequired(
            @NotNull final II18nFormatter formatter, @NotNull final II18nLang... locales)
            throws I18nLocaleNotFoundException {
        final String first = this.firstI18n != null
                ? this.firstI18n.buildRequired(formatter, locales)
                : StringUtils.toStringOrEmpty(this.first);
        final String concat = this.concatI18n != null
                ? this.concatI18n.buildRequired(formatter, locales)
                : StringUtils.toStringOrEmpty(this.concat);
        final String before = this.beforeI18n != null
                ? this.beforeI18n.buildRequired(formatter, locales)
                : StringUtils.toStringOrEmpty(this.before);
        final String after = this.afterI18n != null
                ? this.afterI18n.buildRequired(formatter, locales)
                : StringUtils.toStringOrEmpty(this.after);
        final String last = this.lastI18n != null
                ? this.lastI18n.buildRequired(formatter, locales)
                : StringUtils.toStringOrEmpty(this.last);

        return first
                + ArrayUtils.join(
                concat,
                Arrays
                        .stream(this.i18ns)
                        .map(i18n ->
                                before
                                        + i18n.buildRequired(formatter, locales)
                                        + after)
                        .toArray(String[]::new))
                + last;
    }
}
