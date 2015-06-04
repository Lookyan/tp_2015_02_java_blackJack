define([
    'backbone',
    'models/user',
    'views/manager'
], function(
    Backbone,
    userModel,
    viewManager
){

    var Router = Backbone.Router.extend({

        user: userModel,

        routes: {
            'scoreboard': 'scoreboardAction',
            'game': 'gameAction',
            'login': 'loginAction',
            'signup': 'signupAction',
            'token': 'tokenAction',
            '*default': 'defaultActions'
        },

        initialize: function () {
            this.listenTo(this.user, this.user.signupCompleteEvent + ' ' + this.user.loginCompleteEvent, function() {
                this.navigate('/', {trigger: true});
            });
        },

        defaultActions: function () {
            viewManager.mainScreen();
        },

        tokenAction: function () {
            viewManager.tokenScreen();
        },

        scoreboardAction: function () {
            viewManager.scoreboardScreen();
        },

        gameAction: function () {
            //if (this.user.isLogged) {
                viewManager.gameScreen();
            //} else {
                //this.navigate("#login", {trigger: true});
            //}
        },

        loginAction: function () {
            if (this.user.isLogged) {
                this.navigate("/", {trigger: true});
            } else {
                viewManager.loginScreen();
            }
        },

        signupAction: function () {
            if (this.user.isLogged) {
                this.navigate("/", {trigger: true});
            } else {
                viewManager.signupScreen();
            }
        }

    });

    return new Router();
});