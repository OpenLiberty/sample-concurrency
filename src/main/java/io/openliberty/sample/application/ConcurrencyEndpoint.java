package io.openliberty.sample.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint(value = "/concurrencyEndpoint")
public class ConcurrencyEndpoint {

    @Inject
    ConcurrencyBean concurrency;
    
    @Inject
    WebSocketSessionsBean sessions;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("addSession" + session);
        sessions.addSession(session);
        concurrency.addShips();
    }

    @OnClose
    public void onClose(Session session) {
        sessions.removeSession(session);
    }

}
