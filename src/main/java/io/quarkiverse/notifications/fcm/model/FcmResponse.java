package io.quarkiverse.notifications.fcm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Successful response from {@code POST /v1/projects/{projectId}/messages:send}.
 *
 * <p>On error FCM returns a standard Google API error JSON with HTTP 4xx / 5xx.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FcmResponse(

    /**
     * Unique message identifier in the form
     * {@code projects/{project_id}/messages/{message_id}}.
     */
    String name
) {}
