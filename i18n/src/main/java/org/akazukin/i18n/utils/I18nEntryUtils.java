package org.akazukin.i18n.utils;

import lombok.experimental.UtilityClass;
import org.akazukin.i18n.manager.IEntryManager;
import org.akazukin.i18n.manager.data.II18nEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class I18nEntryUtils {
    public @NotNull Collection<String> getLocaleKeys(
            @NotNull final IEntryManager entryMgr) {
        return Arrays.stream(entryMgr.getEntries())
                .map(II18nEntry::getEntryIds)
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
