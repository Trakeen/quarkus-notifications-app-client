package io.quarkiverse.notifications.apns.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

/**
 * Root payload sent to the APNs HTTP/2 API.
 *
 * <p>The {@code aps} key is the required APNs dictionary.
 * Additional custom keys can be added via {@link #customData} and are
 * serialized at the top level (alongside {@code aps}).
 *
 * <p>Example — simple alert:
 * <pre>{@code
 * ApnsPayload payload = ApnsPayload.builder()
 *     .aps(ApnsAps.builder()
 *         .alert(ApnsAlert.builder()
 *             .title("New message")
 *             .body("David: Hello!")
 *             .build())
 *         .sound("default")
 *         .badge(1)
 *         .build())
 *     .build();
 * }</pre>
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApnsPayload {

    /** Required APNs dictionary. */
    ApnsAps aps;

    /**
     * Optional custom data entries serialized as top-level JSON keys.
     * Use {@code .customData("key", value)} in the builder for each entry.
     */
    @Singular("customData")
    @JsonAnyGetter
    Map<String, Object> customData;
}
