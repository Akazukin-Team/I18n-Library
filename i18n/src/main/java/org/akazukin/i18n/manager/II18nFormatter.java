package org.akazukin.i18n.manager;

import org.akazukin.i18n.manager.data.I18nLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface II18nFormatter {
    /**
     * Gets the fallback language used when a translation is not found.
     * The fallback language provides a default locale for message formatting
     * when the requested language is unavailable.
     *
     * @return the fallback language, or null if no fallback is set
     */
    @Nullable
    I18nLang getFallbackLang();

    /**
     * Sets the default locale for the application.
     * The provided locale string will be transformed to lowercase and stored as the default locale.
     * If the provided locale string is null, doesn't process with fallback.
     *
     * @param lang the fallback locale string to be set.
     */
    void setFallbackLang(I18nLang lang);

    /**
     * Formats a message using the specified message ID and language preferences.
     * This method attempts to find a translation for the given ID
     * in the provided languages and formats it with the specified arguments.
     * If no translation is found, returns null instead of throwing an exception.
     *
     * @param id the message identifier to look up
     * @param langs array of preferred languages in order of preference
     * @param args optional arguments for message formatting
     * @return the formatted message, or null if the message ID is not found
     */
    @Nullable
    String formatMessage(String id, @NotNull I18nLang[] langs, Object... args);

    /**
     * Formats a message using the specified message ID and language preferences.
     * This method attempts to find a translation for the given ID
     * in the provided languages and formats it with the specified arguments.
     * Unlike formatMessage, this method throws an exception if the message ID is not found.
     *
     * @param id the message identifier to look up
     * @param langs array of preferred languages in order of preference
     * @param args optional arguments for message formatting
     * @return the formatted message (never null)
     * @throws RuntimeException if the message ID is not found
     */
    @NotNull
    String formatMessageThrown(String id, @NotNull I18nLang[] langs, Object... args);
}
