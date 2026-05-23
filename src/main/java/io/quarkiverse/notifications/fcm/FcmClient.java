package io.quarkiverse.notifications.fcm;

import io.quarkiverse.notifications.fcm.model.FcmResponse;
import io.quarkiverse.notifications.fcm.model.FcmSendRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client for the <strong>Firebase Cloud Messaging (FCM) v1 API</strong>.
 *
 * <p>The {@code Authorization} header (Google OAuth 2.0 Bearer token) is injected
 * automatically by the consumer's implementation of
 * {@link io.quarkiverse.notifications.auth.AbstractFcmTokenFilter}.
 *
 * <p>The <strong>Firebase project ID</strong> is part of the base URL and belongs
 * in configuration: it is fixed for the lifetime of an application instance, just
 * like the Mapbox or APNs server hostnames.  The consumer therefore sets the full
 * project-scoped URL once in {@code application.properties} rather than passing
 * the project ID on every call.
 *
 * <p>No {@code baseUri} default is provided: the Firebase project ID is part of the base URL
 * and differs per consumer, so {@code quarkus.rest-client.fcm.url} is always required.
 *
 * <h2>Consumer configuration (application.properties)</h2>
 * <pre>
 * # Replace {your-firebase-project-id} with the project ID from the Firebase console
 * quarkus.rest-client.fcm.url=https://fcm.googleapis.com/v1/projects/{your-firebase-project-id}
 *
 * # Wire in your AbstractFcmTokenFilter implementation
 * quarkus.rest-client.fcm.providers=com.example.YourFcmTokenFilter
 * </pre>
 *
 * @see <a href="https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send">
 *      FCM v1 — projects.messages.send</a>
 */
@RegisterRestClient(configKey = "fcm")
@ApplicationScoped
public interface FcmClient {

    /**
     * Send a message via FCM to a device, topic, or condition.
     *
     * <p>Returns the message resource name on success (HTTP 200).
     * On error FCM returns a standard Google API error JSON with HTTP 4xx / 5xx;
     * a {@link jakarta.ws.rs.WebApplicationException} is thrown in that case.
     *
     * @param request the send request wrapping the {@link io.quarkiverse.notifications.fcm.model.FcmMessage}
     * @return the message name returned by FCM,
     *         e.g. {@code projects/{project}/messages/{message_id}}
     */
    @POST
    @Path("/messages:send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FcmResponse send(FcmSendRequest request);
}
