package io.quarkiverse.notifications.auth;

import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

/**
 * Injects a Google OAuth 2.0 access token as an {@code Authorization} header on
 * every outbound request to the FCM v1 REST client.
 *
 * <p>Register the concrete implementation via:
 * <pre>
 * quarkus.rest-client.fcm.providers=com.example.YourFcmTokenFilter
 * </pre>
 *
 * <p>The access token is a short-lived OAuth 2.0 Bearer token obtained from
 * the Google Auth Library using a service-account key file.  Callers are
 * responsible for caching it and refreshing before expiry (typically 1 hour).
 *
 * @see <a href="https://firebase.google.com/docs/cloud-messaging/auth-server">
 *      Authorize Send Requests to FCM</a>
 */
public abstract class AbstractFcmTokenFilter implements ResteasyReactiveClientRequestFilter {

    @Override
    public void filter(final ResteasyReactiveClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("Authorization", "Bearer " + getAccessToken());
    }

    /**
     * Return the current Google OAuth 2.0 access token.
     * Called once per request — implement caching here or upstream.
     *
     * @return Bearer token without the {@code Bearer} prefix
     */
    protected abstract String getAccessToken();
}
