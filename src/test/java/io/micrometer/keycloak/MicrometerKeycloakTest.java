package io.micrometer.keycloak;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import java.util.Collections;
import java.util.Map;

class MicrometerKeycloakTest {
    private static final String DEFAULT_REALM = "myrealm";
    private static final String CLIENT_ID = "THE_CLIENT_ID";
    private static final String IDENTITY_PROVIDER = "THE_ID_PROVIDER";

    private MeterRegistry meterRegistry = MeterRegistryHolder.asTestHarness().getMeterRegistry();

    @BeforeEach
    void beforeEach() {
        meterRegistry.clear();
    }

    @Test
    void shouldCorrectlyCountLoginWhenIdentityProviderIsDefined() {
        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(createEvent(EventType.LOGIN, IDENTITY_PROVIDER));
        meterRegistry.get("keycloak.logins")
                .tag("provider", IDENTITY_PROVIDER)
                .tag("realm", DEFAULT_REALM)
                .tag("client.id", CLIENT_ID)
                .tag("status", "success")
                .counter();
    }

    @Test
    void shouldCorrectlyCountLoginWhenIdentityProviderIsNotDefined() {
        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(createEvent(EventType.LOGIN));
        meterRegistry.get("keycloak.logins")
                .tag("provider", "keycloak")
                .tag("realm", DEFAULT_REALM)
                .tag("client.id", CLIENT_ID)
                .tag("status", "success")
                .counter();
    }

    @Test
    void shouldCorrectlyCountLoginError() {
        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(createEvent(EventType.LOGIN_ERROR, IDENTITY_PROVIDER, "user_not_found"));
        meterRegistry.get("keycloak.logins")
                .tag("provider", IDENTITY_PROVIDER)
                .tag("realm", DEFAULT_REALM)
                .tag("client.id", CLIENT_ID)
                .tag("error", "user_not_found")
                .tag("status", "error")
                .counter();
    }

    @Test
    void shouldCorrectlyCountRegister() {
        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(createEvent(EventType.REGISTER, IDENTITY_PROVIDER));
        meterRegistry.get("keycloak.registrations")
                .tag("provider", IDENTITY_PROVIDER)
                .tag("realm", DEFAULT_REALM)
                .tag("client.id", CLIENT_ID)
                .tag("status", "success")
                .counter();
    }

    @Test
    void shouldCorrectlyRecordGenericEvents() {
        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(createEvent(EventType.UPDATE_EMAIL));
        meterRegistry.get("keycloak.events")
                .tag("realm", DEFAULT_REALM)
                .tag("type", EventType.UPDATE_EMAIL.toString())
                .tag("provider", "keycloak")
                .counter();
    }

    @Test
    void shouldCorrectlyRecordGenericAdminEvents() {
        AdminEvent event = new AdminEvent();
        event.setOperationType(OperationType.ACTION);
        event.setResourceType(ResourceType.AUTHORIZATION_SCOPE);
        event.setRealmId(DEFAULT_REALM);

        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(event, true);
        meterRegistry.get("keycloak.admin.events")
                .tag("realm", DEFAULT_REALM)
                .tag("operation.type", OperationType.ACTION.toString())
                .tag("resource.type", ResourceType.AUTHORIZATION_SCOPE.toString())
                .counter();
    }

    @Test
    void shouldTolerateNullLabels() {
        Event event = new Event();
        event.setType(EventType.CLIENT_DELETE);
        event.setRealmId(null);

        MetricsEventListener listener = new MetricsEventListener();
        listener.onEvent(event);
        meterRegistry.get("keycloak.events")
                .tag("type", EventType.CLIENT_DELETE.toString())
                .tag("realm", "unknown")
                .counter();
    }

    private Event createEvent(EventType type, String provider, String error) {
        Event event = new Event();
        event.setType(type);
        event.setRealmId(DEFAULT_REALM);
        event.setClientId(CLIENT_ID);
        event.setDetails(Collections.emptyMap());

        if (provider != null) {
           event.setDetails(Map.of("identity_provider", provider));
        }

        if (error != null) {
            event.setError(error);
        }

        return event;
    }

    private Event createEvent(EventType type, String provider) {
        return createEvent(type, provider, null);
    }

    private Event createEvent(EventType type) {
        return createEvent(type, null, null);
    }
}
