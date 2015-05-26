define([
    'backbone',
    'tmpl/menu/main',
    'models/user'
], function(
    Backbone,
    tmpl,
    userModel
){

    var View = Backbone.View.extend({

        template: tmpl,
        user:userModel,

        initialize: function ($body) {
            $body.append(this.el);
            this.$el.hide();
            this.render();
        },
        
        render: function () {
            this.$el.html(this.template({name: this.user.get('name')}));
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