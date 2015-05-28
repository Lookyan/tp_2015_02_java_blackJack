var ws;
var body_elem = document.getElementsByName("body");
var hitButton = document.getElementsByClassName('js-hit')[0];
var standButton = document.getElementsByClassName('js-stand')[0];

function init() {
    var queryDict = {};
    window.location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]})
    var token = queryDict.token;
    //
    var hitButton = document.getElementsByClassName('js-hit')[0];
    var standButton = document.getElementsByClassName('js-stand')[0];

    var hitElem = new Hammer(hitButton);
    var standElem = new Hammer(standButton);

    hitElem.on("tap", function(event) {
        hit();
    });

    standElem.on("tap", function(event) {
        stand();
    });

    window.addEventListener('orientationchange', orientationChange);
    window.addEventListener('deviceorientation', handleOrientation);


    if (!token) {
        body_elem.innerHTML = "NO TOKEN";
    } else {
        ws = new WebSocket("ws://localhost:8080/phone?token=" + token);
        ws.onmessage = onMessage;
        ws.onclose = onClose;
    }
}

function handleOrientation(event) {
    var red = 255 * event.alpha / 360; // alpha = [0, 360).
    var green = 255 * (event.beta + 180) / 360; // beta = [-180,180]
    var blue = 255 * (event.gamma + 90) / 180;// gamma =  [-90,90]
    body_elem.style.backgroundColor = "rgb("+red.toString() + ',' + green.toString() + ',' + blue.toString() + ')';
}

function orientationChange() {
    if (window.orientation%180===0) {
        hitButton.style.float = "none";
        standButton.style.float = "none";
    } else {
        hitButton.style.float = "left";
        standButton.style.float = "left";
    }
};

function hit() {
    ws.send(JSON.stringify({type: "hit"}));
}

function stand() {
    ws.send(JSON.stringify({type: "stand"}));
}

function onMessage(event) {

}

function onClose(event) {
    body_elem.innerHTML = "SOCKET CLOSED";
}