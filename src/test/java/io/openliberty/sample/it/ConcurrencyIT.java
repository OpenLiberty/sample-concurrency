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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

public class ConcurrencyIT {
    
    private static WebSocketContainer wsContainer;
    private static Session wsSession;
    private static TestEndpoint endpoint;
    
    private static Client restClient;


    private static String baseURL;
    
    @BeforeAll
    public static void setup() throws Exception {             
        String port = System.getProperty("http.port");
        baseURL = "http://localhost:" + port + "/concurrency/api/";

        wsContainer = ContainerProvider.getWebSocketContainer();
        
        endpoint = new TestEndpoint();
        wsSession = wsContainer.connectToServer(endpoint, URI.create("ws://localhost:9080/concurrencyEndpoint"));

        restClient = ClientBuilder.newClient();
    }

    @AfterAll
    public static void teardown() throws Exception {
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
        int first = endpoint.scheduleQueue.poll(5, TimeUnit.SECONDS);
        int second = endpoint.scheduleQueue.poll(5, TimeUnit.SECONDS);

        assertTrue(first + 1 == second, (first + 1) + " != " + second + ". Values were first: " + first + ", and second: " + second);
    }

    /**
     * Calls the contextualFlow endpoint, and validates that the contextual string
     * can be retrieved successfully.
     */
    @Test
    public void contextualFlowTest() throws InterruptedException {
        restClient.target(baseURL + "contextualFlow").request().get();
        assertEquals("Hello from java:comp/env! The document was inserted successfully!", endpoint.flowQueue.poll(5, TimeUnit.SECONDS));
    }

    /**
     * Calls the virtual threads test and confirms that the threads complete and in
     * a (relatively) timely manner. Plenty of overhead is left for slow systems.
     */
    @Test
    public void virtualThreadsTest() {
        Response response = restClient.target(baseURL + "virtualThreads").request().get();
        Double testTime = response.readEntity(Double.class);
        assertTrue(testTime > 0, "Test should have taken more than 0 seconds");
        assertTrue(testTime < 60, "Test should have taken less than 60 seconds"); 
    }
}