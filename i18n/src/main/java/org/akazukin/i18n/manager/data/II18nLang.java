package org.akazukin.i18n.manager.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface II18nLang {
    II18nLang FALLBACK = new II18nLang() {
        @Override
        public boolean equalsId(@NotNull final II18nLang other) {
            return Objects.equals(other.getId(), this.getId());
        }

        @Override
        public @NotNull String getId() {
            return "fallback";
        }

        @Override
        public String getDisplayName() {
            return "Fallback";
        }
    };

    boolean equalsId(@NotNull II18nLang other);

    @Override
    boolean equals(@NotNull Object o);

    @NotNull String getId();

    @Nullable String getDisplayName();
}
