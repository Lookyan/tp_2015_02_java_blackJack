define([
    'backbone',
    'tmpl/menu/login',
    'models/user'
], function(
    Backbone,
    tmpl,
    userModel
){

    var View = Backbone.View.extend({

        template: tmpl,
        user: userModel,

        throttled: _.throttle(function(loginInfo) {
                localStorage.setItem("loginInfo", JSON.stringify(loginInfo));
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

            var loginInfo = JSON.parse(localStorage.getItem('loginInfo'));
            if(loginInfo) {
                this.$("input[name=name]").val(loginInfo.name);
                this.$("input[name=password]").val(loginInfo.password);
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
                password = this.$("input[name=password]").val();

            this.user.set('name', username);
            this.user.set('password', password);

            var btnSubmit = this.$("input[name=submit]");
            btnSubmit.prop('disabled', true);

            this.listenTo(this.user, this.user.loginCompleteEvent + ' syncError', function() {
                btnSubmit.prop('disabled', false);
            });

            this.user.save();



            //event.preventDefault();
            //var username = this.$("input[name=name]").val(),
            //    password = this.$("input[name=password]").val();
            //
            //var btnSubmit = this.$("input[name=submit]");
            //
            //btnSubmit.prop('disabled', true).delay(2000).queue(
            //    function(next) {
            //        $(this).prop('disabled', false);
            //        next();
            //    }
            //);
            //
            //this.user.login({
            //    login: username,
            //    passw: password
            //});
            //


            // event.preventDefault();
            // $('#login__submit').prop('disabled', true);

            // var data = $(event.target).serializeArray();
            // $.post({
            //     url: '/api/v1/signin',
            //     data: JSON.stringify(data),
            //     contentType: 'application/json',
            //     success: function () {
            //         console.log('test');
            //     }
            // })
            //     .fail(function () {
            //         console.log('sending troubles!!');
            //         $('#login__submit').prop('disabled', false);
            // });



            //console.log(data);
        },

        saveFormFields: function(event) {
            var loginInfoInd = this.$("form").serializeArray();
            var loginInfo = {};
            $.map(loginInfoInd, function(n, i) {
                loginInfo[n.name] = n.value;
            });
            this.throttled(loginInfo);
            
        }

    });

    return View;
});