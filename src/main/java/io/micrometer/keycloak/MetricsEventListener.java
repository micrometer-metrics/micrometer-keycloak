package io.micrometer.keycloak;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class MetricsEventListener implements EventListenerProvider {
    public final static String ID = "metrics-listener";

    private final static String PROVIDER_KEYCLOAK_OPENID = "keycloak";

    private final static MeterRegistry meterRegistry = MeterRegistryHolder.getInstance().getMeterRegistry();

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case LOGIN:
                meterRegistry.counter("keycloak.logins", Tags.of(
                        "realm", nullToUnknown(event.getRealmId()),
                        "client.id", nullToUnknown(event.getClientId()),
                        "status", "success"
                ).and(getIdentityProviderAsTag(event))).increment();
                break;
            case LOGIN_ERROR:
                meterRegistry.counter("keycloak.logins", Tags.of(
                        "realm", nullToUnknown(event.getRealmId()),
                        "client.id", nullToUnknown(event.getClientId()),
                        "error", event.getError(),
                        "status", "error"
                ).and(getIdentityProviderAsTag(event))).increment();
                break;
            case REGISTER:
                meterRegistry.counter("keycloak.registrations", Tags.of(
                        "realm", nullToUnknown(event.getRealmId()),
                        "client.id", nullToUnknown(event.getClientId()),
                        "status", "success"
                ).and(getIdentityProviderAsTag(event))).increment();
                break;
            case REGISTER_ERROR:
                meterRegistry.counter("keycloak.registrations", Tags.of(
                        "realm", nullToUnknown(event.getRealmId()),
                        "client.id", nullToUnknown(event.getClientId()),
                        "error", event.getError(),
                        "status", "error"
                ).and(getIdentityProviderAsTag(event))).increment();
                break;
            default:
                meterRegistry.counter("keycloak.events", Tags.of(
                        Tag.of("realm", nullToUnknown(event.getRealmId())),
                        Tag.of("type", event.getType().toString()),
                        getIdentityProviderAsTag(event)
                )).increment();
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        meterRegistry.counter("keycloak.admin.events", Tags.of(
                "realm", nullToUnknown(event.getRealmId()),
                "operation.type", event.getOperationType().toString(),
                "resource.type", nullToUnknown(event.getResourceTypeAsString())
        )).increment();
    }

    private String nullToUnknown(String value) {
        return value == null ? "unknown" : value;
    }

    /**
     * Retrieve the identity prodiver name from event details or
     * default to {@value #PROVIDER_KEYCLOAK_OPENID}.
     *
     * @param event User event
     * @return Identity provider tag
     */
    private Tag getIdentityProviderAsTag(Event event) {
        String identityProvider = null;
        if (event.getDetails() != null) {
            identityProvider = event.getDetails().get("identity_provider");
        }
        if (identityProvider == null) {
            identityProvider = PROVIDER_KEYCLOAK_OPENID;
        }
        return Tag.of("provider", identityProvider);
    }


    @Override
    public void close() {
        // unused
    }
}
