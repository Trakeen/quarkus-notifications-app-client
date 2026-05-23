package io.quarkiverse.notifications.apns;

/**
 * Values for the {@code apns-push-type} header required by APNs since iOS 13 / watchOS 6.
 *
 * <p>The string representation (returned by {@link #toString()}) matches the lowercase
 * value expected by APNs and is used directly as the JAX-RS {@code @HeaderParam} value.
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns#Set-the-apns-push-type-header">
 *      APNs — Set the apns-push-type header</a>
 */
public enum ApnsPushType {

    /** Visible notification displayed to the user. Requires {@code alert}, {@code badge}, or {@code sound} in the aps dict. */
    ALERT("alert"),

    /** Silent background wake-up. Requires {@code content-available: 1} and no {@code alert}/{@code sound}/{@code badge}. */
    BACKGROUND("background"),

    /** VoIP call notification (requires a VoIP services certificate or entitlement). */
    VOIP("voip"),

    /** watchOS complication update. */
    COMPLICATION("complication"),

    /** File Provider extension update. */
    FILEPROVIDER("fileprovider"),

    /** Mobile Device Management command. */
    MDM("mdm"),

    /** Location-related notification (requires {@code com.apple.developer.location.push} entitlement). */
    LOCATION("location"),

    /** Live Activity update (requires the ActivityKit framework). */
    LIVEACTIVITY("liveactivity"),

    /** Push-to-Talk session notification (requires the PushToTalk framework). */
    PUSHTOTALK("pushtotalk");

    private final String value;

    ApnsPushType(final String value) {
        this.value = value;
    }

    /**
     * Returns the lowercase string value expected by the APNs {@code apns-push-type} header.
     * JAX-RS uses this method when serialising the {@code @HeaderParam}.
     */
    @Override
    public String toString() {
        return value;
    }
}
