define([
    'backbone',
    'tmpl/game',
    'models/gametable',
    'views/playerCards',
    'models/card',
    'views/card'
], function(
    Backbone,
    tmpl,
    GameTable,
    PlayerCards,
    CardModel,
    CardView
){

    var View = Backbone.View.extend({

        events: {
            'click .js-hitbutton': 'hitButtonClick',
            'click .js-standbutton': 'standButtonClick'
        },

        template: tmpl,
        //className: 'gamefield',
        //playerCards1: new Cards(),
        //Cards: CardsArr,
        model: new GameTable,

        initialize: function ($body) {
            $body.append(this.el);
            this.$el.css("height", "100%");
            this.playerCards1 = new PlayerCards({cards: this.model.player1Cards});
            this.playerCards1.listenTo(this.model.player1Cards, 'add remove reset', this.playerCards1.render);
            this.render();
            this.playerCards1.setElement('.js-cards');
            this.hide();
            this.playerCards1.render();
        },

        render: function () {
            this.$el.html(this.template);
            return this;
        },

        hitButtonClick: function() {
            var card = new CardModel({code: "5d", player: 1});
            var cardView = new CardView({model: card})
            this.$el.append(cardView.$el);
            cardView.$el.children().eq(0).animate({ "top": "+=800px" }, "slow");
            this.model.hit();
        },

        standButtonClick: function() {
            this.model.stand();
        },

        show: function () {
            this.$el.show();
            this.trigger("show", this);
        },

        hide: function () {
            this.$el.hide();
        }

    });

    return View;
});