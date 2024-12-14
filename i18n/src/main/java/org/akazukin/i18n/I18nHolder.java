package org.akazukin.i18n;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.akazukin.util.utils.ListUtils;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public final class I18nHolder implements I18nObject {
    final I18nObject[] i18ns;
    String first = null;
    I18nObject firstI18n = null;
    String last = null;
    I18nObject lastI18n = null;
    String concat = null;
    I18nObject concatI18n = null;
    String before = null;
    I18nObject beforeI18n = null;
    String after = null;
    I18nObject afterI18n = null;

    public static I18nHolder of(final I18nObject[] i18ns) {
        return new I18nHolder(i18ns);
    }

    public I18nHolder setFirst(final String first) {
        this.first = first;
        this.firstI18n = null;
        return this;
    }

    public I18nHolder setFirst(final I18nObject first) {
        this.first = null;
        this.firstI18n = first;
        return this;
    }

    public I18nHolder setLast(final String last) {
        this.last = last;
        this.lastI18n = null;
        return this;
    }

    public I18nHolder setLast(final I18nObject last) {
        this.last = null;
        this.lastI18n = last;
        return this;
    }

    public I18nHolder setConcat(final String concat) {
        this.concat = concat;
        this.concatI18n = null;
        return this;
    }

    public I18nHolder setConcat(final I18nObject concat) {
        this.concat = null;
        this.concatI18n = concat;
        return this;
    }

    public I18nHolder setAfter(final String after) {
        this.after = after;
        this.afterI18n = null;
        return this;
    }

    public I18nHolder setAfter(final I18nObject after) {
        this.after = null;
        this.afterI18n = after;
        return this;
    }

    public I18nHolder setBefore(final String before) {
        this.before = before;
        this.beforeI18n = null;
        return this;
    }

    public I18nHolder setBefore(final I18nObject before) {
        this.before = null;
        this.beforeI18n = before;
        return this;
    }

    @Override
    public String build(final I18nUtils i18nUtils, final String locale, final boolean defaultLocale) {
        return (this.first != null ? this.first : (this.firstI18n != null ? this.firstI18n.build(i18nUtils, locale, defaultLocale) :
                "")) +
                ListUtils.join(
                        (this.concat != null ? this.concat : (this.concatI18n != null ? this.concatI18n.build(i18nUtils, locale, defaultLocale) : "")),
                        Arrays
                                .stream(this.i18ns)
                                .map(i18n ->
                                        (this.before != null ? this.before : (this.beforeI18n != null ? this.beforeI18n.build(i18nUtils, locale, defaultLocale) : "")) +
                                                i18n.build(i18nUtils, locale, defaultLocale)
                                                + (this.after != null ? this.after : (this.afterI18n != null ? this.afterI18n.build(i18nUtils, locale, defaultLocale) : "")))
                                .toArray(String[]::new)) +
                (this.last != null ? this.last : (this.lastI18n != null ? this.lastI18n.build(i18nUtils, locale, defaultLocale) : ""));
    }
}
