package io.quarkiverse.notifications.auth;

import io.quarkiverse.notifications.apns.conf.ApnsConf;
import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

/**
 * Injects a cached ES256 APNs provider token as an {@code authorization} header on every
 * outbound request to the APNs REST client.
 *
 * <p>Reads credentials from {@link ApnsConf} ({@code apns.*} properties).
 * APNs provider tokens are valid for 60 minutes; this filter regenerates one
 * 5 minutes early to avoid silent rejections at the boundary.
 *
 * <p>Register via {@code application.properties}:
 * <pre>
 * quarkus.rest-client.apns.providers=io.quarkiverse.notifications.auth.ApnsTokenFilter
 * </pre>
 *
 * @see <a href="https://developer.apple.com/documentation/usernotifications/establishing-a-token-based-connection-to-apns">
 *      APNs Token-Based Authentication</a>
 */
@ApplicationScoped
public class ApnsTokenFilter implements ResteasyReactiveClientRequestFilter {

    @Inject ApnsConf apnsConf;

    /** Regenerate 5 min before the 60-min APNs token expiry. */
    private static final Duration JWT_TTL = Duration.ofMinutes(55);

    private volatile PrivateKey privateKey;
    private volatile String     cachedJwt;
    private volatile Instant    jwtIssuedAt = Instant.EPOCH;

    @Override
    public void filter(final ResteasyReactiveClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("authorization", "bearer " + providerToken());
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private String providerToken() {
        ensureKeyLoaded();
        return cachedJwt();
    }

    private void ensureKeyLoaded() {
        if (privateKey != null) return;
        if (apnsConf.keyId().isBlank() || apnsConf.teamId().isBlank() || apnsConf.keyPath().isBlank()) {
            throw new IllegalStateException(
                    "APNs not configured — set apns.key-id, apns.team-id and apns.key-path");
        }
        synchronized (this) {
            if (privateKey == null) {
                try {
                    final String pem = Files.readString(Path.of(apnsConf.keyPath()));
                    final String b64 = pem
                            .replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "")
                            .replaceAll("\\s+", "");
                    final byte[] der = Base64.getDecoder().decode(b64);
                    privateKey = KeyFactory.getInstance("EC")
                                           .generatePrivate(new PKCS8EncodedKeySpec(der));
                } catch (final Exception e) {
                    throw new RuntimeException(
                            "Failed to load APNs private key from " + apnsConf.keyPath(), e);
                }
            }
        }
    }

    private synchronized String cachedJwt() {
        if (cachedJwt != null && Instant.now().isBefore(jwtIssuedAt.plus(JWT_TTL))) {
            return cachedJwt;
        }
        cachedJwt = Jwt.claims()
                       .issuer(apnsConf.teamId())
                       .issuedAt(Instant.now().getEpochSecond())
                       .jws()
                       .keyId(apnsConf.keyId())
                       .algorithm(SignatureAlgorithm.ES256)
                       .sign(privateKey);
        jwtIssuedAt = Instant.now();
        return cachedJwt;
    }
}
