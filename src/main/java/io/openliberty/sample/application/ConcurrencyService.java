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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bson.Document;
import org.reactivestreams.FlowAdapters;

import com.mongodb.reactivestreams.client.MongoCollection;

import io.openliberty.sample.application.cdi.WithVirtualThreads;
import io.openliberty.sample.application.reactivestreams.MongoSubscriber;

import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ManagedExecutorDefinition(name = "java:module/concurrent/virtual-executor",
                           qualifiers = WithVirtualThreads.class,
                           virtual = true)
@Path("/api")
@ApplicationScoped
public class ConcurrencyService {

    @Inject
    ConcurrencyEndpoint sessions;

    @Inject
    MongoCollection<Document> mongo;

    @Inject
    @WithVirtualThreads
    ManagedExecutorService virtualManagedExecutor;

    @Inject
    ContextService contextService;

    @Inject
    MongoSubscriber subscriber;

    @Inject
    ConcurrencyBean bean;


    @Path("/schedule")
    @GET
    public void schedule() {
            bean.counter();
    }

    @Path("/contextualFlow")
    @GET
    public void contextualFlow() {
        mongo.insertOne(Document.parse("{\"test\" : \"data\"}"))
             .subscribe(FlowAdapters.toSubscriber(contextService.contextualSubscriber(subscriber)));
    } 

    @Path("/virtualThreads")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public double virtualThreads() throws InterruptedException, ExecutionException {
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>(100_000);
        long start = System.nanoTime();
        for (int i = 1; i < 100_000; i++) {

            futures.add(virtualManagedExecutor.submit(() -> {
            
                try {
					Thread.sleep(Duration.ofSeconds(1));                 
				} catch (InterruptedException e) {
					e.printStackTrace(System.out);
				}
                return Integer.valueOf(1);
            }));
        }
        for (Future<Integer> f : futures) f.get();
        return ((double)System.nanoTime()-start) / 1_000_000_000;
	}
}
