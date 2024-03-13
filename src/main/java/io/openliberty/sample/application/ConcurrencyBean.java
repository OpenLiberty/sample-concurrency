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

import jakarta.enterprise.concurrent.Asynchronous;
import jakarta.enterprise.concurrent.Schedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;


@ApplicationScoped
public class ConcurrencyBean {
   
    @Inject
    ConcurrencyEndpoint sessions;

    private int count = 0;

    @Asynchronous(runAt = { @Schedule(cron = "*/3 * * * * *")}) // Every 3 Seconds
    void counter() {
        count++;
        JsonObject response = Json.createObjectBuilder().add("schedule", count).build();
        sessions.broadcast(response.toString());
    }

}
