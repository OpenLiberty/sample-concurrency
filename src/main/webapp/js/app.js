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

    socket = new WebSocket("ws://localhost:9080/concurrencyEndpoint");
      
    socket.onmessage = function(event) {
        var json = JSON.parse(event.data);
        if (json.hasOwnProperty("schedule")) {
            document.getElementById("scheduleOutput").innerText = "Count: " + json.schedule;
        }
        else if (json.hasOwnProperty("contextualFlow")) {
            document.getElementById("contextualFlowOutput").innerText =  json.contextualFlow;
        }
        console.log(JSON.parse(event.data));
      };

}

async function restCall(call) {
    var response = await fetch("concurrency/api/" + call);
    console.log(response.status)
    if (response.status == 200) {
        var output = Number.parseFloat(await response.text());
        document.getElementById("virtualThreadsOutput").innerText = "100k threads run in:  " + output.toFixed(2) + "s";
    }
}

function toast(message, index) {
	var length = 3000;
	var toast = document.getElementById("toast");
	setTimeout(function(){ toast.innerText = message; toast.className = "show"; }, length*index);
	setTimeout(function(){ toast.className = toast.className.replace("show",""); }, length + length*index);
}