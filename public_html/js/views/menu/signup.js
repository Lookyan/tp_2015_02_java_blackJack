define([
    'backbone',
    'tmpl/menu/signup',
    'models/user'
], function(
    Backbone,
    tmpl,
    userModel
){

    var View = Backbone.View.extend({

        template: tmpl,
        //className: 'menu',
        user: userModel,

        throttled: _.throttle(function(signupInfo) {
                localStorage.setItem("signupInfo", JSON.stringify(signupInfo));
            }, 1000),

        events: {
            "submit": "send",
            "input": "saveFormFields"
        },

        initialize: function ($body) {
            $body.append(this.el);
            this.$el.hide();
            this.render();
        },
        
        render: function () {
            this.$el.html(this.template);

            var signupInfo = JSON.parse(localStorage.getItem('signupInfo'));
            if(signupInfo) {
                this.$("input[name=name]").val(signupInfo.name);
                this.$("input[name=password]").val(signupInfo.password);
                this.$("input[name=email]").val(signupInfo.email);
            }
            return this;
        },
        
        show: function () {
            this.$el.show();
            this.trigger("show", this);
        },
        
        hide: function () {
            this.$el.hide();
        },

        send: function (event) {
            event.preventDefault();
            var username = this.$("input[name=name]").val(),
                password = this.$("input[name=password]").val(),
                email = this.$("input[name=email]").val();

            this.user.set('name', username);
            this.user.set('password', password);
            this.user.set('email', email);

            var btnSubmit = this.$("input[name=submit]");
            btnSubmit.prop('disabled', true);

            this.listenTo(this.user, this.user.signupCompleteEvent + ' syncError', function() {
                btnSubmit.prop('disabled', false);
            });

            //btnSubmit.prop('disabled', true).delay(2000).queue(
            //    function(next) {
            //        $(this).prop('disabled', false);
            //        next();
            //    }
            //);

            this.user.save();
        },

        saveFormFields: function(event) {
            var signupInfoInd = this.$("form").serializeArray();
            var signupInfo = {};
            $.map(signupInfoInd, function(n, i) {
                signupInfo[n.name] = n.value;
            });
            this.throttled(signupInfo);
        }

    });

    return View;
});