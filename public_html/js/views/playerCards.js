define([
    'backbone',
    'tmpl/cards',
    'collections/cardsList'
], function(
    Backbone,
    tmpl,
    CardsList
){

    var View = Backbone.View.extend({

        template: tmpl,
        //className: 'wrap',
        initialize: function (options) {
            this.cards = options.cards;
        },

        render: function () {
            this.$el.html(this.template(this.cards.toJSON()));
            //console.log('cards view render');

            return this;
        }

    });

    return View;
});