package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/**
 * Platform-independent notification content for FCM.
 * Rendered as a visible push notification on both Android and iOS.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmNotification {

    /** Notification title. */
    String title;

    /** Notification body text. */
    String body;

    /**
     * URL of the image to display in the notification.
     * Requires FCM v1 and platform support.
     */
    String image;
}
