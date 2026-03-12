package org.akazukin.i18n.manager.data;

import org.akazukin.resource.identifier.IResourceIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface II18nEntry {
    II18nEntry[] EMPTY_ARR = {};

    @NotNull IResourceIdentifier getIdentifier();

    @NotNull II18nLang getLang();

    @Nullable String getEntry(@NotNull String id);

    boolean hasEntryId(@NotNull String id);

    @NotNull String[] getEntryIds();

    @NotNull Map<String, String> getEntries();

    void setEntries(@NotNull Map<String, String> entries);
}
