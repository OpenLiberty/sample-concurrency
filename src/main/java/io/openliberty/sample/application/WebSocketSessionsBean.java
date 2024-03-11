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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

@ApplicationScoped
public class WebSocketSessionsBean {
    
    private Set<Session> sessions = new CopyOnWriteArraySet<Session>();

    public void addSession(Session s) {
        System.out.println("BeanAddSession");
        sessions.add(s);
    }

    public void removeSession(Session s) {
        sessions.remove(s);
    }

}
