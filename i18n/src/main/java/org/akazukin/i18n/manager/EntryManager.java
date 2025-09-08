package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.i18n.Constants;
import org.akazukin.i18n.config.II18nResourceConfig;
import org.akazukin.i18n.exception.I18nLocaleAlreadyExistsException;
import org.akazukin.i18n.exception.IllegalI18nKeyException;
import org.akazukin.i18n.manager.data.I18nEntry;
import org.akazukin.i18n.manager.data.II18nEntry;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.utils.I18nValidatorUtils;
import org.akazukin.resource.exception.ResourceFetchException;
import org.akazukin.resource.exception.ResourceNotFoundException;
import org.akazukin.resource.identifier.IResourceIdentifier;
import org.akazukin.resource.identifier.PathResourceIdentifier;
import org.akazukin.resource.identifier.ResourceResourceIdentifier;
import org.akazukin.resource.resource.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public final class EntryManager implements IEntryManager {
    HashSet<II18nEntry> entries = new HashSet<>();
    II18nResourceConfig config;

    public EntryManager(@NotNull final II18nResourceConfig config) {
        this.config = config;
    }

    @Override
    public synchronized @Nullable II18nEntry getEntry(@NotNull final II18nLang lang) {
        return this.entries.stream()
                .filter(e -> e.getLang().equalsId(lang))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized void removeEntry(@NotNull final II18nLang lang) {
        this.entries.removeIf(e -> e.getLang().equalsId(lang));
    }

    @Override
    public synchronized @NotNull II18nLang[] getLangs() {
        return this.entries.stream()
                .map(II18nEntry::getLang)
                .toArray(II18nLang[]::new);
    }

    @Override
    public void load(@NotNull final II18nLang... langs) {
        for (final II18nLang lang : langs) {
            this.load(lang);
        }
    }

    @Override
    public @NotNull II18nEntry[] getEntries() {
        return this.entries.toArray(II18nEntry.EMPTY_ARR);
    }

    @Override
    public synchronized void load(@NotNull final II18nLang lang)
            throws I18nLocaleAlreadyExistsException, IllegalI18nKeyException {
        if (this.hasEntry(lang)) {
            throw new I18nLocaleAlreadyExistsException(lang);
        } else {
            this.forceLoad(lang);
        }
    }

    private synchronized void forceLoad(@NotNull final II18nLang lang)
            throws IllegalI18nKeyException {
        final Map<String, String> newProps;
        {
            {
                final Properties props = new Properties();
                for (final IResourceIdentifier resId :
                        this.getResourceIdentifiers(lang)) {
                    try (final IResource res = resId.getResource();
                         final InputStreamReader isr
                                 = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
                        props.load(isr);
                    } catch (final ResourceNotFoundException e) {
                        log.warn("The localization resource is not found. | " + resId);
                    } catch (final IOException | ResourceFetchException e) {
                        log.warn("Failed to load localization resource. | " + resId, e);
                    }
                }

                newProps = props.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> String.valueOf(e.getValue())));
            }

            final Set<String> invalids = newProps.keySet()
                    .stream()
                    .filter(key -> !I18nValidatorUtils.isValidId(key))
                    .collect(Collectors.toSet());
            if (!invalids.isEmpty()) {
                throw new IllegalI18nKeyException(lang, invalids.toArray(Constants.EMPTY_STR_ARR));
            }
        }

        final II18nEntry entry = new I18nEntry(lang);
        entry.setEntries(newProps);
        this.putEntry(entry);
    }

    @Override
    public void reload(@NotNull final II18nLang lang) {
        this.forceLoad(lang);
    }

    @Override
    public synchronized void reload() {
        for (final II18nEntry e : this.entries) {
            this.forceLoad(e.getLang());
        }
    }

    @Override
    public synchronized void putEntry(@NotNull final II18nEntry entry) {
        if (this.hasEntry(entry.getLang())) {
            this.removeEntry(entry.getLang());
        }
        this.entries.add(entry);
    }

    @Override
    public synchronized boolean hasEntry(@NotNull final II18nLang lang) {
        return this.entries.stream()
                .anyMatch(e -> e.getLang().equalsId(lang));
    }

    private @NotNull IResourceIdentifier[] getResourceIdentifiers(@NotNull final II18nLang lang) {
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
