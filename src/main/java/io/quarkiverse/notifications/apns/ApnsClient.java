package io.quarkiverse.notifications.apns;

import io.quarkiverse.notifications.apns.model.ApnsPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client for the <strong>APNs HTTP/2 Push Notification API</strong>.
 *
 * <p>The {@code authorization} header (ES256 provider token) is injected automatically
 * by the consumer's implementation of
 * {@link io.quarkiverse.notifications.auth.AbstractApnsTokenFilter}.
 *
 * <p>The <strong>device token</strong> ({@code deviceToken}) is intentionally a
 * method parameter and not a configuration value: it identifies the <em>target device</em>
 * and is therefore different for every call.  Configuration values are fixed for the
 * lifetime of an application instance; device tokens are per-user / per-device.
 *
 * <h2>Consumer configuration (application.properties)</h2>
 * <pre>
 * # Production URL is the default (set by @RegisterRestClient baseUri).
 * # Override only for sandbox (development / TestFlight):
 * %dev.quarkus.rest-client.apns.url=https://api.sandbox.push.apple.com
 *
 * # HTTP/2 is required by APNs
 * quarkus.rest-client.apns.http2=true
 *
 * # Wire in your AbstractApnsTokenFilter implementation
 * quarkus.rest-client.apns.providers=com.example.YourApnsTokenFilter
 * </pre>
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/sending-notification-requests-to-apns">
 *      Sending notification requests to APNs</a>
 */
@RegisterRestClient(configKey = "apns", baseUri = "https://api.push.apple.com")
@ApplicationScoped
public interface ApnsClient {

    /**
     * Send a push notification to a single iOS / macOS device.
     *
     * <p>APNs returns HTTP 200 with an empty body on success.
     * On error it returns 4xx / 5xx with a JSON body — deserialise with
     * {@link io.quarkiverse.notifications.apns.model.ApnsError}.
     *
     * @param deviceToken  device push token (hex string, 64 chars, no spaces or brackets) —
     *                     per-device value, different for every call
     * @param topic        app bundle ID (e.g. {@code org.example.myapp}); fixed per application,
     *                     typically injected from a {@code @ConfigProperty} by the caller
     * @param pushType     push type — use {@link ApnsPushType#ALERT} for visible notifications,
     *                     {@link ApnsPushType#BACKGROUND} for silent wake-ups
     * @param priority     {@code 10} for immediate delivery (required for {@code alert}),
     *                     {@code 5} for power-friendly best-effort delivery
     * @param expiration   Unix timestamp (seconds) after which APNs discards the notification
     *                     if the device is unreachable; {@code 0} = discard immediately
     * @param payload      APNs JSON payload
     * @return the raw JAX-RS {@link Response} — check {@code getStatus()} for 200 / 4xx / 5xx
     */
    @POST
    @Path("/3/device/{deviceToken}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response send(
        @PathParam("deviceToken")       String deviceToken,
        @HeaderParam("apns-topic")      String topic,
        @HeaderParam("apns-push-type")  ApnsPushType pushType,
        @HeaderParam("apns-priority")   int    priority,
        @HeaderParam("apns-expiration") long   expiration,
        ApnsPayload payload
    );
}
