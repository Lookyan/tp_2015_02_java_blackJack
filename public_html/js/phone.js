var ws;

function init() {
    var queryDict = {};
    window.location.search.substr(1).split("&").forEach(function(item) {queryDict[item.split("=")[0]] = item.split("=")[1]})
    var token = queryDict.token;
    var body_elem = document.getElementsByTagName("body")[0];

    if (!token) {
        body_elem.innerHTML = "NO TOKEN";
    } else {
        ws = new WebSocket("ws://http://g06.javaprojects.tp-dev.ru/phone?token=" + token);
        ws.onmessage = onMessage;
        ws.onclose = onClose;
    }

    if (Modernizr.touch) {

        var hitButton = document.getElementsByClassName('js-hit')[0];
        var standButton = document.getElementsByClassName('js-stand')[0];

        var hitElem = new Hammer(hitButton);
        var standElem = new Hammer(standButton);

        hitElem.on("tap", function (event) {
            hit();
        });

        standElem.on("tap", function (event) {
            stand();
        });

        window.addEventListener('orientationchange', orientationChange);
        window.addEventListener('deviceorientation', handleOrientation);
    } else {
        body_elem.innerHTML = "NO TOUCH EVENTS";
    }



}

function handleOrientation(event) {
    var body_elem = document.getElementsByTagName("body")[0];
    var red = Math.round(255 * event.alpha / 360); // alpha = [0, 360).
    var green = Math.round(255 * (event.beta + 180) / 360); // beta = [-180,180]
    var blue = Math.round(255 * (event.gamma + 90) / 180);// gamma =  [-90,90]
    body_elem.style.backgroundColor = "rgb("+red.toString() + ',' + green.toString() + ',' + blue.toString() + ')';
}

function orientationChange() {
    var hitButton = document.getElementsByClassName('js-hit')[0];
    var standButton = document.getElementsByClassName('js-stand')[0];

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
    var body_elem = document.getElementsByTagName("body")[0];
    body_elem.innerHTML = "SOCKET CLOSED";
}