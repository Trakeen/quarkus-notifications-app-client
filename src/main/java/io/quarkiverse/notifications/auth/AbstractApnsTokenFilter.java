package io.quarkiverse.notifications.auth;

import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

/**
 * Injects the APNs provider token as an {@code authorization} header on every
 * outbound request to the APNs REST client.
 *
 * <p>Register the concrete implementation via:
 * <pre>
 * quarkus.rest-client.apns.providers=com.example.YourApnsTokenFilter
 * </pre>
 *
 * <p>The provider token is an ES256-signed JWT built from the Apple Developer key.
 * It is valid for 60 minutes; callers are responsible for caching and renewing it.
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/establishing-a-token-based-connection-to-apns">
 *      APNs Token-Based Authentication</a>
 */
public abstract class AbstractApnsTokenFilter implements ResteasyReactiveClientRequestFilter {

    @Override
    public void filter(final ResteasyReactiveClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("authorization", "bearer " + getProviderToken());
    }

    /**
     * Return the current APNs provider token (ES256 JWT).
     * Called once per request — implement caching here or upstream.
     *
     * @return Bearer token without the {@code bearer} prefix
     */
    protected abstract String getProviderToken();
}
