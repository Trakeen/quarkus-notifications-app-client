package io.quarkiverse.notifications.apns.conf;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Typed configuration for the APNs provider-token flow.
 *
 * <p>All properties default to an empty string so that APNs remains optional:
 * when {@link #bundleId()} is blank the consumer service should no-op silently.
 *
 * <h2>application.properties</h2>
 * <pre>
 * apns.key-id=XXXXXXXXXX            # 10-char key ID — Apple Developer console
 * apns.team-id=XXXXXXXXXX           # 10-char Team ID — Apple Developer console
 * apns.bundle-id=org.example.myapp  # App bundle identifier
 * apns.key-path=/path/to/AuthKey_XXXXXXXXXX.p8
 * </pre>
 */
@ConfigMapping(prefix = "apns")
public interface ApnsConf {

    /**
     * 10-character Key ID from the Apple Developer console
     * (Certificates, Identifiers &amp; Profiles → Keys).
     */
    @WithDefault("") String keyId();

    /**
     * 10-character Team ID from the Apple Developer console
     * (top-right account menu or Membership details).
     */
    @WithDefault("") String teamId();

    /**
     * App bundle identifier used as the {@code apns-topic} header value
     * (e.g. {@code org.example.myapp}).
     */
    @WithDefault("") String bundleId();

    /**
     * Filesystem path to the PKCS#8 PEM file downloaded from Apple
     * (e.g. {@code /secrets/AuthKey_XXXXXXXXXX.p8}).
     */
    @WithDefault("") String keyPath();
}
