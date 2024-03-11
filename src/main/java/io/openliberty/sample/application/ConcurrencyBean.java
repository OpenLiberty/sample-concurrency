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

import java.util.concurrent.SubmissionPublisher;

import java.util.concurrent.atomic.AtomicLong;



import io.openliberty.sample.application.reactivestreams.Message;
import io.openliberty.sample.application.reactivestreams.MongoSubscriber;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.concurrent.Schedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

@ApplicationScoped
public class ConcurrencyBean {
    int ships = 0;

    @Inject
    WebSocketSessionsBean sessions;

    @Inject
    ManagedExecutorService mes;

    @Inject
    MongoSubscriber subscriber;

    SubmissionPublisher<Message> publisher;

    private AtomicLong messageCount = new AtomicLong(0);

    @PostConstruct
    public void init() {
        publisher = new SubmissionPublisher<Message>(mes, 1000);
        //publisher = new SubmissionPublisher<Message>(Executors.newFixedThreadPool(3), 1000);
       //publisher = new SubmissionPublisher<Message>(Runnable::run, 1000);
        publisher.subscribe(subscriber);
        subscriber.messagesRecieved();
    }

    //@Asynchronous(runAt = { @Schedule(cron = "*/3 * * * * *")})
    void messagesRecieved() {
        System.out.println("MessageCount" + messageCount.get());
    }
    
    //@Asynchronous(runAt = { @Schedule(cron = "*/3 * * * * *")})
    /*
    void addShips() {
        System.out.println("addShips");
        ships = ships + 1 % 10;
        //sessions.broadcast(Json.createObjectBuilder().add("ships", ships).build());
    }
    */
    

    public void processMessage(Session session, String message) {
        messageCount.getAndIncrement();
        publisher.offer(new Message(message, session), null);
    }


}
