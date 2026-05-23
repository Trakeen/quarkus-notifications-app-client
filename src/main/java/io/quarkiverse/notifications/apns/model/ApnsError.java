package io.quarkiverse.notifications.apns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Error body returned by the APNs HTTP/2 API on 4xx / 5xx responses.
 *
 * <p>Common {@code reason} values:
 * <ul>
 *   <li>{@code BadDeviceToken} — the device token is syntactically invalid</li>
 *   <li>{@code Unregistered}   — the device has unregistered; remove the token</li>
 *   <li>{@code ExpiredProviderToken} — renew the provider JWT (ES256)</li>
 *   <li>{@code InvalidProviderToken} — wrong key/team/bundle configuration</li>
 *   <li>{@code BadTopic} — the apns-topic does not match the app bundle ID</li>
 * </ul>
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns">
 *      APNs error responses</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApnsError(

    /** Machine-readable error code. */
    String reason,

    /**
     * Unix timestamp (ms) at which APNs confirmed the device unregistered.
     * Only present when {@code reason} is {@code Unregistered}.
     */
    Long timestamp
) {
    /** Returns {@code true} when the device token should be removed from the database. */
    public boolean isStaleToken() {
        return "BadDeviceToken".equals(reason) || "Unregistered".equals(reason);
    }
}
