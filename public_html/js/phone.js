var ws;

function init() {
    var queryDict = {}
    window.location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]})
    var token = queryDict.token;

    if (!token) {
        document.getElementsByName("body").innerHTML = "NO TOKEN";
    } else {
        ws = new WebSocket("ws://localhost:8080/phone?token=" + token);
        ws.onmessage = onMessage;
        ws.onclose = onClose;
    }
}

function hit() {
    ws.send(JSON.stringify({type: "hit"}));
}

function stand() {
    ws.send(JSON.stringify({type: "stand"}));
}

function onMessage(event) {

}

function onClose(event) {
    document.getElementsByName("body").innerHTML = "SOCKET CLOSED";
}