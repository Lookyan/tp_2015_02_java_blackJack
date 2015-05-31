define([
    'backbone',
    'collections/cardsList',
    'models/user'
], function(
    Backbone,
    CardsList,
    UserModel
){

    var Model = Backbone.Model.extend({
        defaults: {
            phase: "start",
            player1: "",
            me: "",
            player3: ""
        },

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
            this.meCards = new CardsList();
            this.player3Cards = new CardsList();

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
            this.set({"me": UserModel.get("name")});
            this.ws = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/gameplay");
            this.ws.onopen = this.onOpen;
            this.ws.onclose = this.onClose;
            this.ws.onmessage = this.onMessage.bind(this);
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
            var self = this;
            var response = JSON.parse(event.data);
            switch(response.body.type) {
                case "state":
                {
                    _.each(response.body.players, function (player) {
                        var who = 0;
                        if(player.name != self.get("me") && player.name != "#dealer") {
                            if(self.get("player1") == "") {
                                who = 1;
                                self.set({"player1": player.name});
                            } else {
                                who = 3;
                                self.set({"player3": player.name});
                            }
                            _.each(player.cards, function (card) {
                                self.cardProcess(who, card, player.score);
                            });
                        }
                        //player. name bet cards... Show!
                    });
                    break;
                }
                case "phase":
                {
                    switch(response.body.phase) {
                        case "BET":
                        {
                            this.trigger('betPhase');
                            break;
                        }
                        case "PLAY":
                        {
                            this.trigger('playPhase');
                            break;
                        }
                    }
                    break;
                }
                case "bet":
                {
                    var who = 0;
                    switch(response.body.owner) {
                        case this.get("player1"): who = 1; break;
                        case this.get("me"): who = 2; break;
                        case this.get("player2"): who = 3; break;
                    }
                    this.trigger('betShow', who, response.body.bet);
                    break;
                }
                case "card":
                {
                    var who = 0;
                    switch(response.body.owner) {
                        case this.get("player1"): who = 1; break;
                        case this.get("me"): who = 2; break;
                        case this.get("player2"): who = 3; break;
                        case "#dealer": who = 0; break;
                    }
                    var card = response.body.card;
                    this.cardProcess(who, card, response.body.score);
                    break;
                }
                case "END":
                {
                    this.trigger('end');
                    break;
                }
                case "wins":
                {
                    _.each(response.body.wins, function (num, player) {
                        var who = 0;
                        switch(player) {
                            case self.get("player1"): who = 1; break;
                            case self.get("me"): who = 2; break;
                            case self.get("player2"): who = 3; break;
                        }
                        self.trigger('wins', who, num);
                    });
                    break;
                }
                case "exit":
                {
                    //body.player exits
                    break;
                }
            }
        },

        onClose: function(event) {
            console.log("Error " + JSON.stringify(event));
        },

        cardProcess: function(who, card, score) {
            var value = card[0];
            var suit = card[1];
            switch(value) {
                case 'T': value = 10; break;
                case 'J': value = 11; break;
                case 'Q': value = 12; break;
                case 'K': value = 13; break;
                case 'A': value = 14; break;
            }
            value = value - 2;
            var x = value * (-81); // смещение для спрайта
            var y = 0;
            var yDelta = -118;
            switch(suit) {
                case 'h': y = 0; break;
                case 'd': y = yDelta; break;
                case 'c': y = yDelta * 2; break;
                case 's': y = yDelta * 3; break;
            }
            this.trigger('newCard', who, x, y, score);
        }

    });

    return Model;
});