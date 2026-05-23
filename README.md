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

### 1 · Configure the REST client

The production URL (`https://api.push.apple.com`) is the default — set by `@RegisterRestClient(baseUri = …)` in the library.
Override only for sandbox (development / TestFlight):

```properties
# application.properties

# Sandbox (dev / TestFlight) — omit in production, the library default is used
%dev.quarkus.rest-client.apns.url=https://api.sandbox.push.apple.com

# APNs requires HTTP/2
quarkus.rest-client.apns.http2=true

# Wire in your token filter (see step 2)
quarkus.rest-client.apns.providers=com.example.ApnsTokenFilter
```

### 2 · Implement the token filter

The library generates no tokens itself. Provide an ES256 JWT signed with your
Apple Developer `.p8` key (e.g. using SmallRye JWT Build):

```java
import io.quarkiverse.notifications.auth.AbstractApnsTokenFilter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApnsTokenFilter extends AbstractApnsTokenFilter {

    @Override
    protected String getProviderToken() {
        return myApnsTokenService.currentToken(); // your caching + signing logic
    }
}
```

### 3 · Inject and send

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

### 1 · Configure the REST client

The Firebase project ID is part of the base URL — configure it once here instead of passing it on every call:

```properties
# application.properties

# Replace {your-firebase-project-id} with the project ID from the Firebase console
quarkus.rest-client.fcm.url=https://fcm.googleapis.com/v1/projects/{your-firebase-project-id}

# Wire in your token filter (see step 2)
quarkus.rest-client.fcm.providers=com.example.FcmTokenFilter
```

### 2 · Implement the token filter

FCM v1 requires a short-lived Google OAuth 2.0 Bearer token obtained from a
service-account key using the Google Auth Library:

```java
import io.quarkiverse.notifications.auth.AbstractFcmTokenFilter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FcmTokenFilter extends AbstractFcmTokenFilter {

    @Override
    protected String getAccessToken() {
        return myGoogleAuthService.currentToken(); // your OAuth2 token logic
    }
}
```

### 3 · Inject and send

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
│   ├── ApnsClient.java                   REST client interface (APNs HTTP/2)
│   ├── ApnsPushType.java                 Enum for the apns-push-type header
│   └── model/
│       ├── ApnsPayload.java              Root payload (@Builder, @JsonInclude NON_NULL)
│       ├── ApnsAps.java                  aps dictionary
│       ├── ApnsAlert.java                alert sub-object
│       └── ApnsError.java                Error response (record)
├── fcm/
│   ├── FcmClient.java                    REST client interface (FCM v1)
│   └── model/
│       ├── FcmSendRequest.java           Root request (@Builder)
│       ├── FcmMessage.java               message object (token / topic / condition)
│       ├── FcmNotification.java          Cross-platform notification content
│       ├── FcmAndroidConfig.java         Android-specific overrides
│       ├── FcmApnsConfig.java            APNs-specific overrides (for FCM→iOS)
│       └── FcmResponse.java              Successful response (record)
└── auth/
    ├── AbstractApnsTokenFilter.java      Inject ES256 bearer token into APNs requests
    └── AbstractFcmTokenFilter.java       Inject OAuth2 bearer token into FCM requests
```
