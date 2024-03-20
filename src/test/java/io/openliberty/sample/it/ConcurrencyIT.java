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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class ConcurrencyIT {
    
    private WebSocketContainer wsContainer;
    private Session wsSession;
    private Endpoint endpoint;
    
    private Client restClient;


    private static String baseURL;
    
    @BeforeAll
    public static void init() {             
        String port = System.getProperty("http.port");
        baseURL = "http://localhost:" + port + "/concurrency/api/";
    }


    @BeforeEach
    public void setup() throws Exception {
        wsContainer = ContainerProvider.getWebSocketContainer();
        
        endpoint = new Endpoint();
        wsSession = wsContainer.connectToServer(endpoint, URI.create("ws://localhost:9080/concurrencyEndpoint"));

        restClient = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() throws Exception {
        wsSession.close();
        restClient.close();
    }

    /**
     * Calls the schedule endpoint, and confirms the first two messages over the
     * websocket are available and increment.
     */
    @Test
    public void scheduleTest() throws InterruptedException {
        restClient.target(baseURL + "schedule").request().get();
        int first = Json.createReader(new StringReader(endpoint.messages.poll(5, TimeUnit.SECONDS))).readObject().getInt("schedule");
        int second = Json.createReader(new StringReader(endpoint.messages.poll(5, TimeUnit.SECONDS))).readObject().getInt("schedule");

        assertTrue(first + 1 == second);
    }
}