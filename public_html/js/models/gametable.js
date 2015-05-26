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
            this.player1Cards.add({value: 3, suit: 'p', player: 1});
        },

        stand: function() {

        }

    });

    return Model;
});