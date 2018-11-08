package co.infinum.princeofversions;

/**
 * Type of notification used to determine if already notified updates should be notified again for optional version.
 */
public enum NotificationType {
    /**
     * Describes that update should be notified only first time
     */
    ONCE,
    /**
     * Describes that update should be notified every time
     */
    ALWAYS
}
