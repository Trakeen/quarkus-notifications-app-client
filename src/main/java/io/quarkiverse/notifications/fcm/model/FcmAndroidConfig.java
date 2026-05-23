package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/**
 * Android-specific message configuration for FCM.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmAndroidConfig {

    /**
     * Collapse key — at most one message per key is kept on the device when offline.
     * Maps to {@code collapse_key}.
     */
    @JsonProperty("collapse_key")
    String collapseKey;

    /**
     * Message priority: {@code "normal"} (default) or {@code "high"}.
     * High priority wakes the device immediately.
     */
    String priority;

    /**
     * How long (in seconds, as a duration string like {@code "3600s"}) FCM stores
     * the message when the device is offline.  Max 4 weeks.
     */
    @JsonProperty("ttl")
    String ttl;

    /** Android notification overrides (channel ID, icon, colour, etc.). */
    FcmAndroidNotification notification;

    /**
     * Android-specific notification configuration (channel, icon, colour…).
     */
    @Value
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FcmAndroidNotification {

        /** Android notification channel ID (required for Android 8+). */
        @JsonProperty("channel_id")
        String channelId;

        /** Small icon resource name (e.g. {@code "ic_notification"}). */
        String icon;

        /** Icon tint colour as {@code #RRGGBB}. */
        String color;

        /** Sound file name in {@code res/raw/}, without extension. */
        String sound;

        /** Notification tag for replacing existing notifications. */
        String tag;

        /** Click action — maps to an {@code intent-filter} action in the app. */
        @JsonProperty("click_action")
        String clickAction;
    }
}
