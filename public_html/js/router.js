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
        scoreboardAction: function () {
            viewManager.scoreboardScreen();
        },
        gameAction: function () {
            if (this.user.isLogged) {
                viewManager.gameScreen();
            } else {
                viewManager.gameScreen(); // TODO: comment this and uncomment following
                //this.navigate("#login", {trigger: true});
            }
        },
        loginAction: function () {
            viewManager.loginScreen();
        },
        signupAction: function () {
            viewManager.signupScreen();
        }
    });

    return new Router();
});