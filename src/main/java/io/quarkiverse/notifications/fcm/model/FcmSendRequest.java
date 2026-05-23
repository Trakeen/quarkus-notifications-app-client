package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/**
 * Root request body for {@code POST /v1/projects/{projectId}/messages:send}.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmSendRequest {

    /** The message to send. */
    FcmMessage message;

    /**
     * When {@code true} the request is validated but the message is not sent.
     * Useful for checking payload correctness without consuming quota.
     */
    Boolean validateOnly;
}
