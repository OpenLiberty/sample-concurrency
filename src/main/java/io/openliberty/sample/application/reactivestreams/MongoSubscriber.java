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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mongodb.client.result.InsertOneResult;

import io.openliberty.sample.application.ConcurrencyEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@ApplicationScoped
public class MongoSubscriber implements Subscriber<InsertOneResult> {

    private Subscription subscription;

    @Inject
    ConcurrencyEndpoint sessions;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(10);
    }

    @Override
    public void onNext(InsertOneResult item) {
        if (item.wasAcknowledged()) {
			try {
				
                String replySuccess = (String) new InitialContext().lookup("java:comp/env/replySuccess");
                
                JsonObject response = Json.createObjectBuilder().add("contextualFlow", replySuccess).build();
                sessions.broadcast(response.toString());
			} catch (NamingException e) {
				System.out.println("Application context not available on Flow.Subscriber");
			}

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
    }
    
}
