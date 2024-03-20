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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
public class Endpoint {
    
    public BlockingQueue<String> messages = new ArrayBlockingQueue<String>(10);

    @OnOpen
    public void onOpen(Session session)
    {
        System.out.println("Socket Connected: " + session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException
    {
        messages.add(message);
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
