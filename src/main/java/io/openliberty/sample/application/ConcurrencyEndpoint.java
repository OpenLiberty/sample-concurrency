/*******************************************************************************
* Copyright (c) 2024 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.sample.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
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
        //concurrency.addShips();
        concurrency.messagesRecieved();
    }

    @OnClose
    public void onClose(Session session) {
        sessions.removeSession(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("WS Message: " + message);
        concurrency.processMessage(session, message);
    }
}
