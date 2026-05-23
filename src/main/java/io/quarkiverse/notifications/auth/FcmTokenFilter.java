package io.quarkiverse.notifications.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import io.quarkiverse.notifications.fcm.conf.FcmConf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Injects a Google OAuth 2.0 access token as an {@code Authorization} header on
 * every outbound request to the FCM v1 REST client.
 *
 * <p>Reads credentials from {@link FcmConf} ({@code fcm.*} properties).
 * The service-account credentials are loaded once and then auto-refreshed via
 * {@link GoogleCredentials#refreshIfExpired()} before each request.
 *
 * <p>Register via {@code application.properties}:
 * <pre>
 * quarkus.rest-client.fcm.providers=io.quarkiverse.notifications.auth.FcmTokenFilter
 * </pre>
 *
 * @see <a href="https://firebase.google.com/docs/cloud-messaging/auth-server">
 *      Authorize Send Requests to FCM</a>
 */
@ApplicationScoped
public class FcmTokenFilter implements ResteasyReactiveClientRequestFilter {

    private static final List<String> FCM_SCOPES =
            List.of("https://www.googleapis.com/auth/firebase.messaging");

    @Inject FcmConf fcmConf;

    private volatile GoogleCredentials credentials;

    @Override
    public void filter(final ResteasyReactiveClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("Authorization", "Bearer " + accessToken());
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private String accessToken() {
        ensureCredentialsLoaded();
        try {
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (final IOException e) {
            throw new RuntimeException("Failed to obtain FCM access token", e);
        }
    }

    private void ensureCredentialsLoaded() {
        if (credentials != null) return;
        if (fcmConf.serviceAccountPath().isBlank()) {
            throw new IllegalStateException(
                    "FCM not configured — set fcm.service-account-path");
        }
        synchronized (this) {
            if (credentials == null) {
                try (final var stream = Files.newInputStream(Path.of(fcmConf.serviceAccountPath()))) {
                    credentials = ServiceAccountCredentials
                            .fromStream(stream)
                            .createScoped(FCM_SCOPES);
                } catch (final IOException e) {
                    throw new RuntimeException(
                            "Failed to load FCM service account from " + fcmConf.serviceAccountPath(), e);
                }
            }
        }
    }
}
