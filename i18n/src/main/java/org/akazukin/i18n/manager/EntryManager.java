package org.akazukin.i18n.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.i18n.Constants;
import org.akazukin.i18n.exception.IllegalI18nKeyException;
import org.akazukin.i18n.manager.data.I18nEntry;
import org.akazukin.i18n.manager.data.II18nEntry;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.utils.I18nValidatorUtils;
import org.akazukin.resource.exception.ResourceFetchException;
import org.akazukin.resource.exception.ResourceNotFoundException;
import org.akazukin.resource.identifier.IResourceIdentifier;
import org.akazukin.resource.resource.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public final class EntryManager implements IEntryManager {
    Collection<II18nEntry> entries = new HashSet<>();
    Collection<II18nLang> entriesLangs = new HashSet<>();
    Collection<IResourceIdentifier> entriesIdentifiers = new HashSet<>();

    public synchronized void load(@NotNull final II18nLang lang, @NotNull final IResourceIdentifier identifier)
            throws IllegalI18nKeyException {
        if (this.hasEntry(identifier, lang)) {
            this.removeEntry(identifier, lang);
        }
        this.forceLoad(identifier.toRelativeIdentifier(lang.getId()), lang);
    }

    private synchronized void forceLoad(final IResourceIdentifier identifier, @NotNull final II18nLang lang)
            throws IllegalI18nKeyException {
        log.debug("Loading localization resource. | Lang: " + lang.getId() + ", " + identifier);

        final Map<String, String> newProps;
        {
            // load resource as props
            final Properties props = new Properties();
            try (final IResource res = identifier.getResource();
                 final InputStreamReader isr
                         = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
                props.load(isr);
            } catch (final ResourceNotFoundException e) {
                log.warn("The localization resource is not found. | " + identifier);
            } catch (final IOException | ResourceFetchException e) {
                log.warn("Failed to load localization resource. | " + identifier, e);
            }

            // Convert props to map
            newProps = props.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> String.valueOf(e.getKey()),
                            e -> String.valueOf(e.getValue())));
        }

        {
            // Validate keys
            final Set<String> invalids = newProps.keySet()
                    .stream()
                    .filter(key -> !I18nValidatorUtils.isValidId(key))
                    .collect(Collectors.toSet());
            if (!invalids.isEmpty()) {
                throw new IllegalI18nKeyException(lang, invalids.toArray(Constants.EMPTY_STR_ARR));
            }
        }

        final II18nEntry entry = new I18nEntry(lang, identifier);
        entry.setEntries(newProps);
        this.putEntry(entry);
    }

    @Override
    public synchronized void load(@NotNull final II18nLang lang)
            throws IllegalI18nKeyException {
        if (!this.hasEntry(lang)) {
            this.entriesLangs.add(lang);
        }
        this.forceLoad(lang);
    }

    @Override
    public synchronized void load(@NotNull final IResourceIdentifier identifier)
            throws IllegalI18nKeyException {
        if (!this.hasEntry(identifier)) {
            this.entriesIdentifiers.add(identifier);
        }
        for (final II18nLang lang : this.entriesLangs) {
            this.forceLoad(identifier.toRelativeIdentifier(lang.getId()), lang);
        }
    }

    @Override
    public synchronized void removeEntry(@NotNull final IResourceIdentifier identifier, @NotNull final II18nLang lang) {
        this.entries.removeIf(e -> e.getIdentifier().equals(identifier) && e.getLang().equalsId(lang));
    }

    @Override
    public synchronized @Nullable II18nEntry[] getEntries(@NotNull final II18nLang lang) {
        return this.entries.stream()
                .filter(e -> e.getLang().equalsId(lang))
                .toArray(II18nEntry[]::new);
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
    public synchronized void putEntry(@NotNull final II18nEntry entry) {
        if (this.hasEntry(entry.getIdentifier(), entry.getLang())) {
            this.removeEntry(entry.getIdentifier(), entry.getLang());
        }
        this.entries.removeIf(e -> e.getIdentifier().equals(entry.getIdentifier()) && e.getLang().equalsId(entry.getLang()));
        this.entries.add(entry);
    }

    @Override
    public synchronized @NotNull II18nEntry[] getEntries() {
        return this.entries.toArray(II18nEntry.EMPTY_ARR);
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
    public synchronized boolean hasEntry(@NotNull final IResourceIdentifier identifier, @NotNull final II18nLang lang) {
        return this.hasEntry(identifier) && this.hasEntry(lang);
    }

    @Override
    public synchronized boolean hasEntry(@NotNull final II18nLang lang) {
        return this.entries.stream()
                .anyMatch(e -> e.getLang().equalsId(lang));
    }

    @Override
    public boolean hasEntry(final @NotNull IResourceIdentifier identifier) {
        return this.entriesIdentifiers.contains(identifier);
    }

    private void forceLoad(@NotNull final II18nLang lang)
            throws IllegalI18nKeyException {
        for (final IResourceIdentifier identifier : this.entriesIdentifiers) {
            this.forceLoad(identifier.toRelativeIdentifier(lang.getId()), lang);
        }
    }
}
