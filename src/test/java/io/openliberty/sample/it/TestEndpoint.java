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
package io.openliberty.sample.it;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
public class TestEndpoint {
    
    public BlockingQueue<Integer> scheduleQueue = new ArrayBlockingQueue<Integer>(10);
    public BlockingQueue<String> flowQueue = new ArrayBlockingQueue<String>(1);

    @OnOpen
    public void onOpen(Session session)
    {
        System.out.println("Socket Connected: " + session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException
    {
        JsonObject json = Json.createReader(new StringReader(message)).readObject();
        if (json.containsKey("schedule")) scheduleQueue.add(json.getInt("schedule"));
        else if (json.containsKey("contextualFlow")) flowQueue.add(json.getString("contextualFlow"));
    }

    @OnClose
    public void onClose(CloseReason reason)
    {
        System.out.println(reason.toString());
    }

    @OnError
    public void onError(Throwable t)
    {
        t.printStackTrace(System.err);
    }

}
