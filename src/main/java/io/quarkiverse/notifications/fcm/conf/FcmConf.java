package io.quarkiverse.notifications.fcm.conf;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Typed configuration for the FCM v1 REST client.
 *
 * <h2>application.properties</h2>
 * <pre>
 * fcm.service-account-path=/secrets/firebase-service-account.json
 * quarkus.rest-client.fcm.url=https://fcm.googleapis.com/v1/projects/{your-firebase-project-id}
 * quarkus.rest-client.fcm.providers=io.quarkiverse.notifications.auth.FcmTokenFilter
 * </pre>
 */
@ConfigMapping(prefix = "fcm")
public interface FcmConf {

    /**
     * Filesystem path to the Firebase service-account JSON file.
     *
     * <p>Download it from the Firebase console:
     * Project settings → Service accounts → Generate new private key.
     *
     * <p>Example: {@code /secrets/firebase-service-account.json}.
     */
    @WithDefault("") String serviceAccountPath();
}
