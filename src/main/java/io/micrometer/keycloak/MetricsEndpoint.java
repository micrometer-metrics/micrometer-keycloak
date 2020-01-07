package io.micrometer.keycloak;

import com.google.common.base.Charsets;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class MetricsEndpoint implements RealmResourceProvider {

    // The ID of the provider is also used as the name of the endpoint
    public final static String ID = "metrics";

    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response get() {
        final StreamingOutput stream = output -> output.write(
                MeterRegistryHolder.getInstance().getPrometheusMeterRegistry().scrape().getBytes(Charsets.UTF_8)
        );
        return Response.ok(stream).build();
    }

    @Override
    public void close() {
        // Nothing to do, no resources to close
    }
}
