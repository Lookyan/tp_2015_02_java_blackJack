define([
    'backbone',
    'tmpl/menu/token',
    'models/user'
], function(
    Backbone,
    tmpl,
    userModel
){

    var View = Backbone.View.extend({

        template: tmpl,
        user: userModel,

        initialize: function ($body) {
            $body.append(this.el);
            this.$el.hide();
            this.render();
            this.listenTo(this.user, this.user.tokenCompleteEvent, function() {
                this.url = "http://" + window.location.hostname + "/phone.html?token=" + this.user.get("token");
                this.render();
            });

        },
        
        render: function () {
            this.$el.html(this.template({url: this.url}));
            return this;
        },
        
        show: function () {
            this.$el.show();
            this.trigger("show", this);
            this.user.getToken();
        },
        
        hide: function () {
            this.$el.hide();
        }

    });

    return View;
});