package io.openliberty.sample.application;

import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.concurrent.Schedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.json.Json;

@ApplicationScoped
public class ConcurrencyBean {
    int ships = 0;

    @Inject
    WebSocketSessionsBean sessions;

    @Inject
    ManagedExecutorService mes;
    
    @Asynchronous(runAt = { @Schedule(cron = "*/5 * * * * *")})
    void addShips() {
        System.out.println("addShips");
        ships = ships + 1 % 10;
        sessions.broadcast(Json.createObjectBuilder().add("ships", ships).build());
    }
}
