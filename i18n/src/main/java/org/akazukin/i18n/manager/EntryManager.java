package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.i18n.Constants;
import org.akazukin.i18n.config.II18nResourceConfig;
import org.akazukin.i18n.exception.I18nLocaleAlreadyExistsException;
import org.akazukin.i18n.exception.IllegalI18nKeyException;
import org.akazukin.i18n.manager.data.I18nEntry;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.i18n.utils.I18nValidatorUtils;
import org.akazukin.resource.exception.ResourceFetchException;
import org.akazukin.resource.identifier.IResourceIdentifier;
import org.akazukin.resource.identifier.PathResourceIdentifier;
import org.akazukin.resource.identifier.ResourceResourceIdentifier;
import org.akazukin.resource.resource.IResource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class EntryManager implements IEntryManager {
    HashSet<I18nEntry> entries = new HashSet<>();
    II18nResourceConfig config;

    public EntryManager(final II18nResourceConfig config) {
        this.config = config;
    }

    @Override
    public void load(final I18nLang... langs) {
        for (final I18nLang lang : langs) {
            this.load(lang);
        }
    }

    @Override
    public I18nEntry[] getEntries() {
        return this.entries.toArray(I18nEntry.EMPTY_ARR);
    }

    @Override
    public void load(final I18nLang lang) throws I18nLocaleAlreadyExistsException, IllegalI18nKeyException {
        synchronized (this) {
            if (this.hasEntry(lang)) {
                throw new I18nLocaleAlreadyExistsException(lang);
            } else {
                this.forceLoad(lang);
            }
        }
    }

    private void forceLoad(final I18nLang lang) throws IllegalI18nKeyException {
        final Properties props = new Properties();
        final IResourceIdentifier[] resources = this.getResourceIdentifiers(lang);
        for (final IResourceIdentifier resId : resources) {
            try (final IResource res = resId.getResource()) {
                try (final InputStreamReader isr = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
                    props.load(isr);
                }
            } catch (final IOException | ResourceFetchException e) {
                log.warn("Failed to load localization resource. | " + resId, e);
            }
        }

        @NotNull final Map<String, @NotNull String> newProps = props.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())));

        if (!I18nValidatorUtils.isValidIds(newProps.keySet())) {
            throw new IllegalI18nKeyException(lang, newProps.keySet().toArray(Constants.EMPTY_STR_ARR));
        }

        final I18nEntry entry = new I18nEntry(lang);
        entry.setEntries(newProps);
        this.putEntry(entry);
    }

    @Override
    public void reload(final I18nLang lang) {
        synchronized (this) {
            this.forceLoad(lang);
        }
    }

    @Override
    public void reload() {
        synchronized (this) {
            for (final I18nEntry e : this.entries) {
                this.forceLoad(e.getLang());
            }
        }
    }

    @Override
    public I18nEntry getEntry(final I18nLang lang) {
        synchronized (this) {
            return this.entries.stream()
                    .filter(e -> e.getLang().equalsId(lang))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public void putEntry(final I18nEntry entry) {
        synchronized (this) {
            if (this.hasEntry(entry.getLang())) {
                this.removeEntry(entry.getLang());
            }
            this.entries.add(entry);
        }
    }

    @Override
    public void removeEntry(final I18nLang lang) {
        synchronized (this) {
            this.entries.removeIf(e -> e.getLang().equalsId(lang));
        }
    }

    @Override
    public boolean hasEntry(final I18nLang lang) {
        synchronized (this) {
            return this.entries.stream()
                    .anyMatch(e -> e.getLang().equalsId(lang));
        }
    }

    private IResourceIdentifier[] getResourceIdentifiers(final I18nLang lang) {
        return new IResourceIdentifier[]{
                new ResourceResourceIdentifier("assets/"
                        + this.config.getDomain().replace(".", "/") + "/"
                        + this.config.getAppId()
                        + "/langs/"
                        + lang.getId() + ".lang",
                        this.config.getClassLoader()),
                new PathResourceIdentifier(
                        new File(this.config.getDataFolder(),
                                "langs/" + lang.getId() + ".lang")
                                .getAbsolutePath())
        };
    }
}
