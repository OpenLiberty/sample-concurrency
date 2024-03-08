package io.openliberty.sample.application;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonObject;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;

@ApplicationScoped
public class WebSocketSessionsBean {
    
    private Set<Session> sessions = new CopyOnWriteArraySet<Session>();

    public void addSession(Session s) {
        System.out.println("BeanAddSession");
        sessions.add(s);
    }

    public void removeSession(Session s) {
        sessions.remove(s);
    }

    public void broadcast(JsonObject json) {
        sessions.forEach( s -> {
            try {
                s.getBasicRemote().sendObject(json);
            } catch (IOException | EncodeException e) { //TODO better error handling
                e.printStackTrace();
            } 
        });
    }
}
