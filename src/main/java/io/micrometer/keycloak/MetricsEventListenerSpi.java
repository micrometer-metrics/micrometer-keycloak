package io.micrometer.keycloak;

import org.keycloak.events.EventListenerSpi;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;

public class MetricsEventListenerSpi extends EventListenerSpi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "Prometheus Metrics Provider";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return MetricsEventListener.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return MetricsEventListenerFactory.class;
    }
}
