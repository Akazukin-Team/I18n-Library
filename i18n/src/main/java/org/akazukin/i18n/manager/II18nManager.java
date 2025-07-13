package org.akazukin.i18n.manager;

/**
 * Central interface for managing internationalization (i18n) operations.
 * This interface provides access to the core components of the i18n system,
 * including entry management and message formatting functionality.
 * <p>
 * The {@code II18nManager} serves as the main entry point for all i18n operations,
 * coordinating between the entry manager that handles locale data storage and retrieval,
 * and the formatter that processes localized messages with dynamic content.
 */
public interface II18nManager {
    /**
     * Retrieves the entry manager responsible for loading, storing, and managing
     * internationalization entries for different locales.
     *
     * @return the entry manager instance. Must not be {@code null}.
     */
    IEntryManager getEntryMgr();

    /**
     * Retrieves the formatter responsible for processing and formatting
     * internationalized messages with dynamic content.
     *
     * @return the formatter instance. Must not be {@code null}.
     */
    II18nFormatter getFormatter();
}
