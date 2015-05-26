define([
    'backbone',
    'tmpl/card'
], function(
    Backbone,
    tmpl
){

    var View = Backbone.View.extend({

        template: tmpl,
        //className: 'wrap',
        initialize: function (options) {
            this.model = options.model;
            this.render();
        },

        render: function () {
            this.$el.html(this.template(this.model.toJSON()));
            //console.log('cards view render');

            return this;
        }

    });

    return View;
});