package org.akazukin.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.util.ext.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Getter
@Setter
public class I18nUtils implements Reloadable {
    private static final Pattern REGEX_I18N = Pattern.compile("<\\$[a-zA-Z0-9.]+>");
    private static final Pattern REGEX_ARGS = Pattern.compile("<args\\[[0-9]+]>");

    @NotNull
    final Map<String, Properties> language = new ConcurrentHashMap<>();

    @NotNull
    final ClassLoader classLoader;
    @NotNull
    final String domain;
    @NotNull
    final String appId;
    @NotNull
    final File dataFolder;
    @NotNull
    String defaultLocale;
    @NotNull
    String[] locales;

    public I18nUtils(@NotNull final ClassLoader classLoader, @NotNull final String domain, @NotNull final String appId, final @NotNull File dataFolder, @NotNull final String defaultLocale, @NotNull final String... locales) {
        this.classLoader = classLoader;
        this.domain = domain;
        this.appId = appId;
        this.dataFolder = dataFolder;
        this.setDefaultLocale(defaultLocale);
        this.setLocales(locales);

        this.load();
    }

    private void load() {
        for (final String locale : this.locales) {
            final String defaultDir = "assets/" + this.domain.replace(".", "/") + "/" + this.appId + "/";

            final String langsFile = "langs/" + locale + ".lang";
            final File file = new File(this.dataFolder, langsFile);

            final Properties props = new Properties();

            try (final InputStream is = this.classLoader.getResourceAsStream(defaultDir + langsFile)) {
                if (is != null) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } else {
                    log.warn("Failed to load the default locale set for " + locale);
                }
            } catch (final IOException e) {
                log.warn("Failed to load localization file | " + langsFile, e);
            }

            if (file.exists()) {
                try (final InputStream is = Files.newInputStream(file.toPath())) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } catch (final IOException e) {
                    log.warn("Failed to load custom localization file | " + langsFile, e);
                }
            }

            if (!props.isEmpty()) {
                this.language.put(locale, props);
            }
        }

        if (this.locales.length == 0) {
            final IllegalStateException e = new IllegalStateException("No locales loaded");
            log.error("Failed to load localization file", e);
            throw e;
        }

        log.info("Loaded " + this.language.size() + " languages");
    }

    public void setLocales(final String... locales) {
        this.locales = Arrays.stream(locales).map(String::toLowerCase).toArray(String[]::new);
    }

    public void setDefaultLocale(@NotNull final String defaultLocale) {
        this.defaultLocale = defaultLocale.toLowerCase();
    }

    @Override
    public void reload() {
        this.language.clear();
        this.load();
    }

    @Nullable
    public String get(@NotNull final I18n i18n) {
        return this.get(this.defaultLocale, false, i18n);
    }

    @Nullable
    public String get(@NotNull final String locale, final boolean defaultLocale, @NotNull final I18n i18n) {
        return this.get(locale, defaultLocale, i18n.getId(), i18n.getArgs());
    }

    @Nullable
    public String get(@NotNull final String locale, @NotNull final I18n i18n) {
        return this.get(locale, true, i18n.getId(), i18n.getArgs());
    }

    @Nullable
    public String get(@NotNull final String locale, @NotNull final String id, @NotNull final Object... args) {
        if (!this.language.containsKey(locale)) {
            return null;
        }

        final Properties localeSet = this.language.get(locale);
        if (!localeSet.containsKey(id)) {
            return null;
        }

        String i18n = localeSet.getProperty(id);
        if (i18n == null) {
            return null;
        }

        final Matcher m2 = I18nUtils.REGEX_I18N.matcher(i18n);
        if (m2.find()) {
            i18n = m2.replaceAll(this.get(locale, m2.group().substring(2, m2.group().length() - 1)));
        }

        for (int i = 0; i < args.length; i++) {
            if (!i18n.contains("<args[" + i + "]>")) {
                continue;
            }

            if (args[i] instanceof I18nObject) {
                args[i] = ((I18nObject) args[i]).build(this, locale);
            }

            if (args[i] == null) {
                continue;
            }

            i18n = i18n.replace("<args[" + i + "]>", String.valueOf(args[i]));
        }

        i18n = i18n.replace("\\n", "\n");

        return i18n;
    }

    @Nullable
    public String get(@NotNull final String locale, final boolean defaultLocale, @NotNull final String id, @NotNull final Object... args) {
        final String result = this.get(locale, id, args);
        if (result != null || !defaultLocale) {
            return result;
        }
        return this.get(this.defaultLocale, id, args);
    }
}
