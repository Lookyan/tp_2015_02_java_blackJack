define([
    'backbone',
    'api/userSync'
], function(
    Backbone,
    userSync
){

    var UserModel = Backbone.Model.extend({
        sync: userSync,
        url: '/',
        //id: 1,
        isLogged: false,

        //events:
        signupCompleteEvent: 'signupCompleteEvent',
        loginCompleteEvent: 'loginCompleteEvent',
        logoutCompleteEvent: 'logoutCompleteEvent',

        //login: "",
        //chips: 0,
        //password: "",
        //email: "",

        defaults: {
            "name": "",
            "chips": 0,
            "password": "",
            "email": ""
        },

        initialize: function() {
           this.fetch();
           // window.js_test_ = this.toJSON();
           // console.log(JSON.stringify(this.toJSON()));
        },

        isNew: function() {
            return true;
        },

        loginSuccess: function(data) {
            this.trigger(this.loginCompleteEvent);
            if (data.status === 200) {
                this.set('email', data.body.email);
                this.set('chips', data.body.chips);
                this.set('password', "");
                this.isLogged = true;
            }
            if (data.status === 404) {
                if (data.body.error === 'wrong') {
                    this.set('name', "");
                    this.set('password', "");
                //    TODO ...
                }
            }
        },

        signUpSuccess: function(data) {
            this.trigger(this.signupCompleteEvent);
            if (data.status === 200) {
                this.set('email', data.body.email);
                this.set('chips', data.body.chips);
                this.set('password', "");
                this.isLogged = true;
            }
            if (data.status === 400) {
                if (data.body.error === 'exists') {
                    this.set('name', "");
                    this.set('password', "");
                    this.set('email', "");
                    //    TODO ...
                }
            }
        },

        identifySuccess: function(data) {
            if (data.status === 200) {
                this.set('name', data.body.name);
                this.set('email', data.body.email);
                this.set('chips', data.body.chips);
                this.set('password', "");
                this.isLogged = true;
            }
            if (data.status === 404) {
                if (data.body.error === 'notLogged') {
                    this.set('name', "");
                    this.set('chips', data.body.chips);
                    this.set('password', "");
                    this.set('email', "");
                }
            }
        },

        logoutSuccess: function(data) {
            this.set('name', "");
            this.set('email', "");
            this.set('chips', 0);
            this.set('password', "");
            this.isLogged = false;
        }

        //login: function(args) {
        //    var options = _.extend({'method': 'login'},
        //        {'callbacks': this.callbacks('successAuth', 'errorAuth')});
        //    this.save(args, options);
        //},
        //
        //register: function(args) {
        //    var options = _.extend({'method': 'register'},
        //         {'callbacks': this.callbacks('successReg', 'errorReg')});
        //    this.save(args, options);
        //},
        //
        //identifyUser: function(prefix) {
        //    args = _.extend({'method': 'identifyUser'},
        //        {'callbacks': this.callbacks(prefix + ':known', prefix + ':anonymous')});
        //    this.fetch(args);
        //},
        //
        //logout: function() {
        //    args = _.extend({'method': 'logout'},
        //        {'callbacks': this.callbacks('successLogout', 'errorLogout')});
        //    this.destroy(args);
        //},

        //callbacks: function(success, error) {
        //    return  { success: function(message) {
        //                    this.trigger(success, message);
        //                }.bind(this),
        //
        //            error: function(message) {
        //                    console.log(message);
        //                    this.trigger(error, message);
        //                }.bind(this)
        //            }
        //}
    });

    return new UserModel();
});