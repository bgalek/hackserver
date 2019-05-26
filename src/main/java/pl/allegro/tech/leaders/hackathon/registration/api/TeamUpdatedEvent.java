package pl.allegro.tech.leaders.hackathon.registration.api;

import pl.allegro.tech.leaders.hackathon.infrastructure.events.DomainEvent;

import java.net.InetSocketAddress;

public class TeamUpdatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "TEAM_UPDATED";

    public TeamUpdatedEvent(String name, InetSocketAddress remoteAddress) {
        super(EVENT_TYPE, new AddressUpdatePayload(name, remoteAddress));
    }

    public TeamUpdatedEvent(String name, HealthStatus healthStatus) {
        super(EVENT_TYPE, new HealthUpdatePayload(name, healthStatus.isWorking()));
    }

    private static class AddressUpdatePayload {

        private final String name;
        private final InetSocketAddress remoteAddress;

        AddressUpdatePayload(String name, InetSocketAddress remoteAddress) {
            this.name = name;
            this.remoteAddress = remoteAddress;
        }

        public String getName() {
            return name;
        }

        public InetSocketAddress getRemoteAddress() {
            return remoteAddress;
        }
    }

    private static class HealthUpdatePayload {

        private final String name;
        private final boolean health;

        HealthUpdatePayload(String name, boolean health) {
            this.name = name;
            this.health = health;
        }

        public String getName() {
            return name;
        }

        public boolean isHealth() {
            return health;
        }
    }
}