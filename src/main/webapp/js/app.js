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

function openWebSocket() {

    socket = new WebSocket("ws://localhost:9080/jakartaconcurrencysample/concurrencyEndpoint");

    var i = 0;
    socket.onopen = function(e) {
        var data = {"test" : "data"};
        for (var j = 0; j < 1; j++)
            socket.send(JSON.stringify(data));
    };
      
      socket.onmessage = function(event) {
        //console.log(`[message] Data received from server: ${event.data}`);
        i++;
      };
      
    socket.onclose = function(e) {
        console.log("Socket closed: " + i);
    }
}

function toast(message, index) {
	var length = 3000;
	var toast = document.getElementById("toast");
	setTimeout(function(){ toast.innerText = message; toast.className = "show"; }, length*index);
	setTimeout(function(){ toast.className = toast.className.replace("show",""); }, length + length*index);
}