/**
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
