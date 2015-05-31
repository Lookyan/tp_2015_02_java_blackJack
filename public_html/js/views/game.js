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
            'click .js-standbutton': 'standButtonClick',
            'click .js-bet': 'betButtonClick'
        },

        template: tmpl,
        //className: 'gamefield',
        //playerCards1: new Cards(),
        //Cards: CardsArr,
        model: new GameTable,

        initialize: function ($body) {
            this.model.on("betPhase", this.betPhase.bind(this));
            this.model.on("playPhase", this.playPhase.bind(this));
            this.model.on("betShow", this.betShow.bind(this));
            this.model.on("newCard", this.newCard.bind(this));
            this.model.on("end", this.end.bind(this));
            this.model.on("wins", this.wins.bind(this));
            $body.append(this.el);
            this.$el.css("height", "100%");

//            this.playerCards1 = new PlayerCards({cards: this.model.player1Cards});
//            this.playerCards1.listenTo(this.model.player1Cards, 'add remove reset', this.playerCards1.render);
            this.render();
//            this.playerCards1.setElement('.js-cards');
            this.hide();
//            this.playerCards1.render();
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

        betButtonClick: function(e) {
            debugger;
            this.$el.find('.dealer__cardsplace > .cardset').empty();
            this.$el.find('.leftplayer__cardsplace > .cardset').empty();
            this.$el.find('.mainplayer__cardsplace > .cards > .cardset').empty();
            this.$el.find('.rightplayer__cardsplace > .cardset').empty();
            this.$el.find('.leftplayer__cardsplace > .result').text("");
            this.$el.find('.mainplayer__cardsplace > .cards > .result').text("");
            this.$el.find('.rightplayer__cardsplace > .result').text("");
            this.$el.find('.score').css('display', 'block');
            var amount = parseInt($(e.currentTarget).children('span').eq(0).text(), 10);
            this.model.bet(amount);
        },

        show: function () {
            this.model.start();
            this.$el.show();
            this.trigger("show", this);
        },

        hide: function () {
            this.model.finish();
            this.$el.hide();
        },

        betPhase: function (player) {
            this.$el.find('.playerboard').show();
            this.$el.find('.js-buttons').hide();
        },

        playPhase: function () {
            this.$el.find('.playerboard').hide();
            this.$el.find('.js-buttons').show();
        },

        betShow: function (who, bet) {
            switch(who) {
                case 1: this.$el.find('.leftplayer__chipsplace').append(bet); break;
                case 2: this.$el.find('.mainplayer__chipsplace').append(bet); break;
                case 3: this.$el.find('.rightplayer__chipsplace').append(bet); break;
            }
        },

        newCard: function (who, x, y, score) {
            $card = $('<div class="card">');
            $card.css('background-position', x + 'px ' + y + 'px');
            debugger;
            switch(who) {
                case 0:
                {
                    this.$el.find('.dealer__cardsplace > .cardset').append($card);
                    this.$el.find('.dealer__cardsplace > .score').text(score);
                    break;
                }
                case 1:
                {
                    this.$el.find('.leftplayer__cardsplace > .cardset').append($card);
                    this.$el.find('.leftplayer__cardsplace > .score').text(score);
                    break;
                }
                case 2:
                {
                    this.$el.find('.mainplayer__cardsplace > .cards > .cardset').append($card);
                    this.$el.find('.mainplayer__cardsplace > .cards > .score').text(score);
                    break;
                }
                case 3:
                {
                    this.$el.find('.rightplayer__cardsplace > .cardset').append($card);
                    this.$el.find('.rightplayer__cardsplace > .score').text(score);
                    break;
                }
            }
        },

        end: function () {
            this.$el.find('.js-buttons').hide();
        },

        wins: function (who, num) {
            var str = "";
            num = Number(num, 10);
            if(num > 0) {
                str = "Win +" + num;
            } else if(num == 0) {
                str = "Push";
            } else {
                str = "Lost " + num;
            }
            switch(who) {
                case 1: this.$el.find('.leftplayer__cardsplace > .result').text(str); break;
                case 2: this.$el.find('.mainplayer__cardsplace > .cards > .result').text(str); break;
                case 3: this.$el.find('.rightplayer__cardsplace > .result').text(str); break;
            }
        }

    });

    return View;
});