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
        isNew_: true,

        //events:
        signupCompleteEvent: 'signupCompleteEvent',
        loginCompleteEvent: 'loginCompleteEvent',
        logoutCompleteEvent: 'logoutCompleteEvent',
        identifyCompleteEvent: 'identifyCompleteEvent',
        tokenCompleteEvent: 'tokenCompleteEvent',

        //login: "",
        //chips: 0,
        //password: "",
        //email: "",

        defaults: {
            "name": "",
            "chips": 0,
            "password": "",
            "email": "",
            "token": ""
        },

        initialize: function() {
           this.fetch();
           // window.js_test_ = this.toJSON();
           // console.log(JSON.stringify(this.toJSON()));
        },

        isNew: function() {
            return this.isNew_;
        },

        getToken: function() {
            this.sync('token', this, {});
        },

        loginSuccess: function(data) {
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
                }
            }
            this.trigger(this.loginCompleteEvent);
        },

        signUpSuccess: function(data) {
            if (data.status === 200) {
                this.set('chips', data.body.chips);
                this.set('password', "");
                this.isLogged = true;
                this.isNew_ = false;
            }
            if (data.status === 400) {
                if (data.body.error === 'exists') {
                    this.set('name', "");
                    this.set('password', "");
                    this.set('email', "");
                    //    TODO ...
                }
            }
            this.trigger(this.signupCompleteEvent);
        },

        identifySuccess: function(data) {
            data = JSON.parse(data);
            if (data.status === 200) {
                this.set('name', data.body.name);
                this.set('email', data.body.email);
                this.set('chips', data.body.chips);
                this.set('password', "");
                this.isLogged = true;
                this.isNew_ = false;
            }
            if (data.status === 404) {
                if (data.body.error === 'notLogged') {
                    this.set('name', "");
                    this.set('chips', 0);
                    this.set('password', "");
                    this.set('email', "");
                }
            }
            this.trigger(this.identifyCompleteEvent);
        },

        logoutSuccess: function(data) {
            this.set('name', "");
            this.set('email', "");
            this.set('chips', 0);
            this.set('password', "");
            this.isLogged = false;
            this.isNew_ = true;
            this.trigger(this.logoutCompleteEvent);
        },

        tokenSuccess: function(data) {
            data = JSON.parse(data);
            this.set('token', data.body.token);
            this.trigger(this.tokenCompleteEvent);
            //debugger;
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