package io.quarkiverse.notifications.apns.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/**
 * The APNs {@code aps} dictionary — the required root object of every APNs payload.
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/generating-a-remote-notification">
 *      APNs payload reference</a>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApnsAps {

    /** Alert text shown to the user. Required for visible notifications. */
    ApnsAlert alert;

    /**
     * Sound to play. Use {@code "default"} for the system default sound,
     * or a filename in the app bundle.  Omit for silent notifications.
     */
    String sound;

    /** Badge number to display on the app icon. Set to {@code 0} to clear. */
    Integer badge;

    /**
     * Set to {@code 1} to wake the app in the background for a background update.
     * When set, {@code alert}, {@code sound}, and {@code badge} should be omitted.
     */
    @JsonProperty("content-available")
    Integer contentAvailable;

    /**
     * Set to {@code 1} to indicate that the notification contains mutable content
     * that a Notification Service Extension may modify before delivery.
     */
    @JsonProperty("mutable-content")
    Integer mutableContent;

    /**
     * Identifier for grouping related notifications in Notification Center.
     * Maps to {@code threadIdentifier} in UNNotificationContent.
     */
    @JsonProperty("thread-id")
    String threadId;

    /**
     * Category identifier defined in the app's UNUserNotificationCenter for
     * custom action buttons.
     */
    String category;
}
