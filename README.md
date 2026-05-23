# quarkus-notifications-app-client

Quarkus REST client library for sending push notifications to **Apple (APNs)** and **Google (FCM v1)** from a Quarkus application.

Built on top of `quarkus-rest-client-jackson` (RESTEasy Reactive / MicroProfile REST Client) — fully compatible with **Quarkus native mode**.

---

## Dependency

Install locally first:
```bash
mvn install -DskipTests
```

Then add to your project's `pom.xml`:
```xml
<dependency>
    <groupId>io.quarkiverse.notifications</groupId>
    <artifactId>quarkus-notifications-app-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## APNs — Apple Push Notification service

### 1 · Configure

The production URL (`https://api.push.apple.com`) is the default — set by `@RegisterRestClient(baseUri = …)` in the library.
Override only for sandbox (development / TestFlight):

```properties
# application.properties

# APNs credentials
apns.key-id=XXXXXXXXXX
apns.team-id=XXXXXXXXXX
apns.bundle-id=org.example.myapp
apns.key-path=/secrets/AuthKey_XXXXXXXXXX.p8

# Sandbox (dev / TestFlight) — omit in production, the library default is used
%dev.quarkus.rest-client.apns.url=https://api.sandbox.push.apple.com

# APNs requires HTTP/2
quarkus.rest-client.apns.http2=true

# Wire in the built-in token filter
quarkus.rest-client.apns.providers=io.quarkiverse.notifications.auth.ApnsTokenFilter
```

The library's `ApnsTokenFilter` reads `ApnsConf`, loads the `.p8` key once, and caches
the ES256 JWT — regenerating it 5 minutes before the 60-minute expiry. No Java code is required on your side.

### 2 · Inject and send

```java
import io.quarkiverse.notifications.apns.ApnsClient;
import io.quarkiverse.notifications.apns.ApnsPushType;
import io.quarkiverse.notifications.apns.model.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PushService {

    @Inject @RestClient ApnsClient apnsClient;

    public void sendAlert(String deviceToken, String title, String body) {
        ApnsPayload payload = ApnsPayload.builder()
            .aps(ApnsAps.builder()
                .alert(ApnsAlert.builder().title(title).body(body).build())
                .sound("default")
                .badge(1)
                .build())
            .build();

        try (Response response = apnsClient.send(
                deviceToken,
                "org.example.myapp",  // apns-topic = bundle ID
                ApnsPushType.ALERT,   // apns-push-type
                10,                   // apns-priority (10 = immediate)
                0L,                   // apns-expiration (0 = discard if offline)
                payload)) {

            if (response.getStatus() != 200) {
                ApnsError error = response.readEntity(ApnsError.class);
                if (error.isStaleToken()) removeDeviceToken(deviceToken);
            }
        }
    }
}
```

---

## FCM — Firebase Cloud Messaging v1

### 1 · Configure

`FcmConf` centralises the Firebase project ID — use it to drive the REST client URL
via a [property expression](https://quarkus.io/guides/config-reference#property-expressions)
so it is defined only once:

```properties
# application.properties

# Firebase credentials
fcm.project-id=my-firebase-project-id
fcm.service-account-path=/secrets/firebase-service-account.json

# REST client base URL — project ID injected from the property above
quarkus.rest-client.fcm.url=https://fcm.googleapis.com/v1/projects/${fcm.project-id}

# Wire in the built-in token filter
quarkus.rest-client.fcm.providers=io.quarkiverse.notifications.auth.FcmTokenFilter
```

The library's `FcmTokenFilter` reads `FcmConf`, loads the service-account JSON once, and
calls `refreshIfExpired()` before each request so the Google OAuth 2.0 token is never stale.
No Java code is required on your side.

The service-account JSON is downloaded from the Firebase console:
**Project settings → Service accounts → Generate new private key**.

### 2 · Inject and send

```java
import io.quarkiverse.notifications.fcm.FcmClient;
import io.quarkiverse.notifications.fcm.model.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PushService {

    @Inject @RestClient FcmClient fcmClient;

    public void sendAlert(String deviceToken, String title, String body) {
        FcmSendRequest request = FcmSendRequest.builder()
            .message(FcmMessage.builder()
                .token(deviceToken)
                .notification(FcmNotification.builder()
                    .title(title)
                    .body(body)
                    .build())
                .build())
            .build();

        FcmResponse response = fcmClient.send(request);
        // response.name() = "projects/{project}/messages/{id}"
    }
}
```

---

## Project structure

```
src/main/java/io/quarkiverse/notifications/
├── apns/
│   ├── ApnsClient.java              REST client interface (APNs HTTP/2)
│   ├── ApnsPushType.java            Enum for the apns-push-type header
│   ├── conf/
│   │   └── ApnsConf.java            @ConfigMapping(prefix = "apns")
│   └── model/
│       ├── ApnsPayload.java         Root payload (@Builder, @JsonInclude NON_NULL)
│       ├── ApnsAps.java             aps dictionary
│       ├── ApnsAlert.java           alert sub-object
│       └── ApnsError.java           Error response (record)
├── fcm/
│   ├── FcmClient.java               REST client interface (FCM v1)
│   ├── conf/
│   │   └── FcmConf.java             @ConfigMapping(prefix = "fcm")
│   └── model/
│       ├── FcmSendRequest.java      Root request (@Builder)
│       ├── FcmMessage.java          message object (token / topic / condition)
│       ├── FcmNotification.java     Cross-platform notification content
│       ├── FcmAndroidConfig.java    Android-specific overrides
│       ├── FcmApnsConfig.java       APNs-specific overrides (for FCM→iOS)
│       └── FcmResponse.java         Successful response (record)
└── auth/
    ├── ApnsTokenFilter.java         ES256 JWT — reads ApnsConf, caches 55 min
    └── FcmTokenFilter.java          Google OAuth2 — reads FcmConf, auto-refreshes
```
