package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.exception.I18nLocaleNotFoundException;
import org.akazukin.i18n.manager.data.I18nEntry;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.i18n.object.I18nObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class I18nFormatter implements II18nFormatter {
    final IEntryManager entryMgr;
    @Setter
    @Getter
    I18nLang fallbackLang;

    public I18nFormatter(final IEntryManager entryMgr) {
        this.entryMgr = entryMgr;
    }

    @Override
    public @Nullable String formatMessage(final String id, final @NotNull I18nLang[] langs, final Object... args) {
        String i18n = null;
        for (I18nLang l : langs) {
            if (l == I18nLang.FALLBACK) {
                if (this.fallbackLang == null) {
                    continue;
                }
                l = this.fallbackLang;
            }

            final I18nEntry localeSet = this.entryMgr.getEntry(l);
            if (localeSet == null) {
                continue;
            }
            i18n = localeSet.getEntry(id);
            if (i18n != null) {
                break;
            }
        }
        if (i18n == null) {
            return null;
        }

        i18n = i18n.replace("\\n", "\n");


        Matcher m;
        while ((m = I18nManager.REGEX_I18N.matcher(i18n)).find()) {
            i18n = m.replaceFirst(String.valueOf(this.formatMessage(m.group(1), langs)));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] != null && args[i] instanceof I18nObject) {
                args[i] = ((I18nObject) args[i]).build(this, langs);
            }

            i18n = i18n.replace("<args[" + i + "]>",
                    args[i] != null ? String.valueOf(args[i]) : "");
        }

        return i18n;
    }

    @Override
    public @NotNull String formatMessageThrown(final String id, final @NotNull I18nLang[] langs, final Object... args) throws I18nLocaleNotFoundException {
        String i18n = null;
        for (I18nLang l : langs) {
            if (l == I18nLang.FALLBACK) {
                if (this.fallbackLang == null) {
                    continue;
                }
                l = this.fallbackLang;
            }

            final I18nEntry localeSet = this.entryMgr.getEntry(l);
            if (localeSet == null) {
                continue;
            }
            i18n = localeSet.getEntry(id);
            if (i18n != null) {
                break;
            }
        }
        if (i18n == null) {
            throw new I18nLocaleNotFoundException(langs, id);
        }

        i18n = i18n.replace("\\n", "\n");


        Matcher m;
        while ((m = I18nManager.REGEX_I18N.matcher(i18n)).find()) {
            i18n = m.replaceFirst(this.formatMessageThrown(m.group(1), langs));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] != null && args[i] instanceof I18nObject) {
                args[i] = ((I18nObject) args[i]).buildRequired(this, langs);
            }

            i18n = i18n.replace("<args[" + i + "]>",
                    args[i] != null ? String.valueOf(args[i]) : "");
        }

        return i18n;
    }
}
