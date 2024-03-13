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
package io.openliberty.sample.application.cdi;

import org.bson.Document;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class MongoProducerRS {

    //TODO add config when there's an EE11 compatible MPConfig
    @Produces
    public MongoClient createMongo() {
        return MongoClients.create("mongodb://localhost");
    }

    @Produces
    public MongoDatabase createDB(MongoClient client) {
        return client.getDatabase("testdb");
    }

    @Produces
    public MongoCollection<Document> createDocument(MongoDatabase db) {
        return db.getCollection("testCollection");
    }

    public void close(@Disposes MongoClient toClose) {
        toClose.close();
    }
}