# Micrometer Keycloak SPI

Inspired by @aerogear's [Keycloak Metrics SPI](https://github.com/aerogear/keycloak-metrics-spi).

A [Service Provider](https://www.keycloak.org/docs/4.8/server_development/index.html#_providers) that adds a metrics endpoint to Keycloak. The endpoint returns metrics data ready to be scraped by [Prometheus](https://prometheus.io/).

Two distinct providers are defined:

* `MetricsEventListener` to record the internal Keycloak events
* `MetricsEndpoint` to expose the data through a custom endpoint

The endpoint lives under `<url>/auth/realms/<realm>/metrics`. It will return data for all realms, no matter which realm
you use in the URL (you can just default to `/auth/realms/master/metrics`).

## Usage

Add the jar into the _providers_ subdirectory of your Keycloak installation.

To enable the event listener via the GUI interface, go to _Manage -> Events -> Config_. The _Event Listeners_ configuration should have an entry named `metrics-listener`.

To enable the event listener via the Keycloak CLI, such as when building a Docker container, use these commands. (These commands assume /opt/jboss is the Keycloak home directory, which is used on the _jboss/keycloak_ reference container on Docker Hub.)

    /opt/jboss/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user $KEYCLOAK_USER --password $KEYCLOAK_PASSWORD
    /opt/jboss/keycloak/bin/kcadm.sh update events/config -s "eventsEnabled=true" -s "adminEventsEnabled=true" -s "eventsListeners+=metrics-listener"
    /usr/bin/rm -f /opt/jboss/.keycloak/kcadm.config