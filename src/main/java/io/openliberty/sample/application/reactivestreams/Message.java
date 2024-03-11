package io.openliberty.sample.application.reactivestreams;

import jakarta.websocket.Session;

public record Message(String json, Session session) {
    
}
