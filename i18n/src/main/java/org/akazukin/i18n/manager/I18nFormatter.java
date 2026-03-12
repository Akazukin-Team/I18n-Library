package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.data.II18nEntry;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.II18nObject;
import org.akazukin.i18n.utils.I18nValidatorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class I18nFormatter implements II18nFormatter {
    private static final Pattern REGEX_I18N = Pattern.compile("<\\$(" + I18nValidatorUtils.ID_REGEX + ")>");

    final IEntryManager entryMgr;
    @Setter
    II18nLang fallbackLang;

    public I18nFormatter(@NotNull final IEntryManager entryMgr) {
        this.entryMgr = entryMgr;
    }

    @Override
    public @Nullable String formatMessage(
            @NotNull final String id, @NotNull final II18nLang[] langs, final Object... args) {
        String i18n = null;
        for (II18nLang lang : langs) {
            if (lang.equalsId(II18nLang.FALLBACK)) {
                if (this.fallbackLang == null) {
                    continue;
                }
                lang = this.fallbackLang;
            }

            final II18nEntry[] entries = this.entryMgr.getEntries(lang);
            for (final II18nEntry entry : entries) {
                i18n = entry.getEntry(id);
                if (i18n != null) {
                    break;
                }
            }
        }
        if (i18n == null) {
            return null;
        }

        i18n = i18n.replace("\\n", "\n");


        Matcher m;
        while ((m = REGEX_I18N.matcher(i18n)).find()) {
            i18n = m.replaceFirst(String.valueOf(this.formatMessage(m.group(1), langs)));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] instanceof II18nObject) {
                args[i] = ((II18nObject) args[i]).build(this, langs);
            }

            i18n = i18n.replace("<args[" + i + "]>", String.valueOf(args[i]));
        }

        return i18n;
    }

    @Override
    public @NotNull String formatMessageThrown(
            @NotNull final String id, @NotNull final II18nLang[] langs, @NonNull final Object... args)
            throws I18nLocaleNotFoundException {
        String i18n = null;
        for (II18nLang lang : langs) {
            if (lang.equalsId(II18nLang.FALLBACK)) {
                if (this.fallbackLang == null) {
                    continue;
                }
                lang = this.fallbackLang;
            }

            final II18nEntry[] entries = this.entryMgr.getEntries(lang);
            for (final II18nEntry entry : entries) {
                i18n = entry.getEntry(id);
                if (i18n != null) {
                    break;
                }
            }
        }
        if (i18n == null) {
            throw new I18nLocaleNotFoundException(langs, id);
        }

        i18n = i18n.replace("\\n", "\n");


        Matcher m;
        while ((m = REGEX_I18N.matcher(i18n)).find()) {
            i18n = m.replaceFirst(this.formatMessageThrown(m.group(1), langs));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] instanceof II18nObject) {
                args[i] = ((II18nObject) args[i]).buildRequired(this, langs);
            }

            i18n = i18n.replace("<args[" + i + "]>", String.valueOf(args[i]));
        }

        return i18n;
    }
}
