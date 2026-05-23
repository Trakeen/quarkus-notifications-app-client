package io.quarkiverse.notifications.apns.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/**
 * APNs {@code alert} dictionary — displayed as the notification text.
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/generating-a-remote-notification">
 *      APNs payload reference</a>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApnsAlert {

    /** Short description of the reason for the notification (bold line). */
    String title;

    /** Supplementary description below the title. */
    @JsonProperty("subtitle")
    String subTitle;

    /** The content of the notification (body text). */
    String body;

    /** Filename of an image in the app bundle shown when the user taps the action button. */
    @JsonProperty("launch-image")
    String launchImage;
}
