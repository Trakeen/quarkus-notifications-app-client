package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * APNs-specific overrides inside an FCM message.
 * Useful when sending through FCM to iOS devices and needing APNs-specific headers or payload fields.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmApnsConfig {

    /**
     * APNs HTTP request headers (e.g. {@code apns-priority}, {@code apns-collapse-id}).
     * Keys and values must be strings.
     */
    Map<String, String> headers;

    /**
     * APNs payload as a free-form map, serialised directly into the APNs JSON body.
     * Use to set {@code aps.badge}, {@code aps.sound}, custom keys, etc.
     */
    Map<String, Object> payload;
}
