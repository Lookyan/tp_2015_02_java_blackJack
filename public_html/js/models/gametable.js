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
            var self = this;
//            debugger;
            var response = JSON.parse(event.data);
            switch(response.body.type) {
                case "state":
                {
                    _.each(response.body.players, function (player) {
                        if(player.name != self.get("me") && player.name != "#dealer") {
                            if(self.get("player1") == "") {
                                self.set({"player1": player.name});
                            } else {
                                self.set({"player3": player.name});
                            }
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
                            this.trigger('betPhase', 2);
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
                    debugger;
                }
            }
//            console.log(event.data);
        },

        onClose: function(event) {
            console.log("Error " + JSON.stringify(event));
        }

    });

    return Model;
});