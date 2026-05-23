package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

/**
 * An FCM v1 message.
 *
 * <p>Exactly one of {@link #token}, {@link #topic}, or {@link #condition} must be set.
 *
 * <p>Example — device token + cross-platform notification:
 * <pre>{@code
 * FcmMessage.builder()
 *     .token(deviceToken)
 *     .notification(FcmNotification.builder()
 *         .title("New message")
 *         .body("David: Hello!")
 *         .build())
 *     .build()
 * }</pre>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmMessage {

    /**
     * Device registration token.  Use to send to a single device.
     * Mutually exclusive with {@link #topic} and {@link #condition}.
     */
    String token;

    /**
     * FCM topic name (without the {@code /topics/} prefix).
     * Use to fan-out to all devices subscribed to the topic.
     * Mutually exclusive with {@link #token} and {@link #condition}.
     */
    String topic;

    /**
     * FCM condition expression for multi-topic fan-out
     * (e.g. {@code "'TopicA' in topics && 'TopicB' in topics"}).
     * Mutually exclusive with {@link #token} and {@link #topic}.
     */
    String condition;

    /** Visible notification shown on both Android and iOS. */
    FcmNotification notification;

    /**
     * Custom key-value pairs delivered to the app.
     * For Android these arrive in the intent extras; for iOS in the APNs payload.
     */
    @Singular("dataEntry")
    Map<String, String> data;

    /** Android-specific message overrides (channel, priority, TTL…). */
    FcmAndroidConfig android;

    /** APNs-specific header and payload overrides for iOS delivery. */
    FcmApnsConfig apns;
}
