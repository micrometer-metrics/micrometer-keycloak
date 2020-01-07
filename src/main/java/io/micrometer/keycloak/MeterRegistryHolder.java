package io.micrometer.keycloak;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MeterRegistryHolder {
    private static boolean isTest = false;
    private final MeterRegistry testMeterRegistry = new SimpleMeterRegistry();

    private final PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    private final static MeterRegistryHolder INSTANCE = new MeterRegistryHolder();

    private MeterRegistryHolder() {
        MeterRegistry meterRegistry = getMeterRegistry();
        new ProcessorMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmThreadMetrics().bindTo(meterRegistry);
        new FileDescriptorMetrics().bindTo(meterRegistry);
        new ClassLoaderMetrics().bindTo(meterRegistry);
        new UptimeMetrics().bindTo(meterRegistry);
    }

    public static MeterRegistryHolder getInstance() {
        return INSTANCE;
    }

    public static MeterRegistryHolder asTestHarness() {
        isTest = true;
        return INSTANCE;
    }

    /**
     * @return A composite meter registry containing all of the configured monitoring system backends
     * to which metrics will be published.
     */
    public MeterRegistry getMeterRegistry() {
        return isTest ? testMeterRegistry : prometheusMeterRegistry;
    }

    public PrometheusMeterRegistry getPrometheusMeterRegistry() {
        return prometheusMeterRegistry;
    }
}
