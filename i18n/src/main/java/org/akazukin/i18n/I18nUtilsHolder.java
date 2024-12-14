package org.akazukin.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.util.ext.Reloadable;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class I18nUtilsHolder implements Reloadable {
    List<I18nUtils> i18nUtils;

    public I18nUtilsHolder(final I18nUtils... i18nUtils) {
        this.i18nUtils = new CopyOnWriteArrayList<>();
        this.i18nUtils.addAll(Arrays.asList(i18nUtils));
    }

    @Nullable
    public String get(final I18n i18n) {
        for (final I18nUtils i18nUtil : this.i18nUtils) {
            final String result = i18n.build(i18nUtil);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Nullable
    public String get(final String locale, final I18n i18n) {
        return this.get(locale, i18n, true);
    }

    @Nullable
    public String get(final String locale, final I18n i18n, final boolean defaultLocale) {
        for (final I18nUtils i18nUtil : this.i18nUtils) {
            final String result = i18n.build(i18nUtil, locale);
            if (result != null) {
                return result;
            }
        }

        if (defaultLocale) {
            for (final I18nUtils i18nUtil : this.i18nUtils) {
                if (locale.equalsIgnoreCase(i18nUtil.getDefaultLocale())) {
                    continue;
                }

                final String result = i18n.build(i18nUtil);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public void reload() {
        this.i18nUtils.forEach(I18nUtils::reload);
    }
}
