define([
    'backbone'
], function(
    Backbone
){

    var Model = Backbone.Model.extend({

        cards: [],
        score: 0,
        name: "",
        bet: 0,

        initialize: function (options) {
            this.cards = options.cards;
            this.score = options.score;
            this.name = options.name;
            this.bet = options.bet;
        }
    });

    return Model;
});