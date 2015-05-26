define([
    'backbone',
    'tmpl/menu/scoreboard',
    'collections/scores'
], function(
    Backbone,
    tmpl,
    scores
){

    var View = Backbone.View.extend({

        template: tmpl,
        model: scores,

        initialize: function ($body) {
            $body.append(this.el);
            this.$el.hide();
            this.render();
        },
        
        render: function () {
            this.$el.html(this.template(this.model.toJSON()));
            return this;
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