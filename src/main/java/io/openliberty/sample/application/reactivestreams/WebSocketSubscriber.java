/*******************************************************************************
* Copyright (c) 2024 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v2.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.sample.application.reactivestreams;

import java.io.IOException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mongodb.client.result.InsertOneResult;

import jakarta.websocket.Session;

public class WebSocketSubscriber implements Subscriber<InsertOneResult> {

    private Subscription subscription;

    private Session session;

    public WebSocketSubscriber(Session s) {
        session = s;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        System.out.println("Subscribed - WebSocketSubscriber");
        subscription.request(10);
    }

    @Override
    public void onNext(InsertOneResult item) {
        System.out.println("next - WebSocketSubscriber");
        System.out.println("post: " + Thread.currentThread().getName() + " " + Thread.currentThread().threadId());

        if (item.wasAcknowledged())
            try {
                session.getBasicRemote().sendText((String) new InitialContext().lookup("java:comp/env/replyMessage"));
            } catch (IOException | NamingException e) {
                e.printStackTrace(System.out);
            }
        else System.out.println("Insert not acknowledged");
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace(System.out);
    }

    @Override
    public void onComplete() {
        System.out.println("complete - WebSocketSubscriber");
    }
    
}
