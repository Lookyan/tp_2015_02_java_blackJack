define([
    'backbone',
    'collections/cardsList'
], function(
    Backbone,
    CardsList
){

    var Model = Backbone.Model.extend({
        //defaults: {
        //    //value: 0
        //},

        //allCards: new CardsList(),
        //dealerCards: new CardsList(),
        //player1Cards: new CardsList(),
        //player2Cards: new CardsList(),
        //player3Cards: new CardsList(),


        initialize: function () {
            //console.log("Game table was created");
            this.allCards = new CardsList();
            this.dealerCards = new CardsList();
            this.player1Cards = new CardsList();

            this.listenTo(this.allCards, 'add', this.spreadNewCards);

            // 0 - dealer, 1 - player1, etc
            this.allCards.add([
                {value: 6, suit: 'd', player: 0},
                {value: 10, suit: 'p', player: 1},
                {value: 2, suit: 'c', player: 1}
            ]);
        },

        spreadNewCards: function(card) {
            switch(card.get('player')) {
                case 0:
                    this.dealerCards.add(card);
                    break;
                case 1:
                    this.player1Cards.add(card);
                    break;
            }
        },

        hit: function() {
            this.ws.send(JSON.stringify({type: "hit"}));
        },

        stand: function() {
            this.ws.send(JSON.stringify({type: "stand"}));
        },

        bet: function(bet) {
            this.ws.send(JSON.stringify({type: "bet", bet: bet}));
        },

        start: function() {
            this.ws = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/gameplay");
            this.ws.onopen = this.onOpen;
            this.ws.onclose = this.onClose;
            this.ws.onmessage = this.onMessage;
        },

        finish: function() {
            if (this.ws) {
                this.ws.close();
            }
        },

        onOpen: function() {
        },

        onMessage: function(event) {
            console.log(event.data);
        },

        onClose: function(event) {
            console.log("Error " + JSON.stringify(event));
        }

    });

    return Model;
});