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

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicLong;


import org.bson.Document;
import org.reactivestreams.FlowAdapters;

import com.mongodb.reactivestreams.client.MongoCollection;

import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class MongoSubscriber implements Subscriber<Message> {
    
    @Inject
    MongoCollection<Document> mongo;

    @Inject
    private ContextService contextService;

    private Subscription subscription;

    private AtomicLong messageCount = new AtomicLong(0);

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        System.out.println("Subscribed - MongoSubscriber");
        subscription.request(10);
    }

    @Override
    public void onNext(Message item) {
        System.out.println(item);
        System.out.println("next - MongoSubscriber");
        System.out.println("pre: " + Thread.currentThread().getName() + " " + Thread.currentThread().threadId());

        mongo.insertOne(Document.parse(item.json())).subscribe(FlowAdapters.toSubscriber(contextService.contextualSubscriber(new WebSocketSubscriber(item.session()))));
        //mongo.insertOne(Document.parse(item.json())).subscribe(FlowAdapters.toSubscriber(new WebSocketSubscriber(item.session())));
        messageCount.getAndIncrement();
        System.out.println("next - MongoSubscriber end");
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace(System.out);
    }

    @Override
    public void onComplete() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onComplete'");
    }

}
