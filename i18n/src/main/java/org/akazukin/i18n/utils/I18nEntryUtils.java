package org.akazukin.i18n.utils;

import lombok.experimental.UtilityClass;
import org.akazukin.i18n.manager.IEntryManager;
import org.akazukin.i18n.manager.data.I18nEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class I18nEntryUtils {
    public Collection<String> getLocaleKeys(final IEntryManager entryMgr) {
        return Arrays.stream(entryMgr.getEntries())
                .map(I18nEntry::getEntryIds)
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
