define([
	'backbone',
    'views/menu/scoreboard',
    'views/menu/login',
    'views/menu/main',
    'views/game',
    'views/menu/signup'
], function(
    Backbone,
    scoreboardView,
    loginView,
    mainView,
    gameView,
    signupView
) {


	var Manager = Backbone.View.extend({

        el: 'body',

        views: [],

        initialize: function() {
            this.scoreboard = new scoreboardView(this.$el);
            this.login = new loginView(this.$el);
            this.main = new mainView(this.$el);
            this.game = new gameView(this.$el);
            this.signup = new signupView(this.$el);
            this.addView(this.scoreboard);
            this.addView(this.login);
            this.addView(this.main);
            this.addView(this.game);
            this.addView(this.signup);
        },

		addView: function(currentView) {
			this.views.push(currentView);
			this.listenTo(currentView, "show", function() {
                this.views.forEach( function(view) {
					if (view != currentView)
						view.hide();
				});
			});
		},

        mainScreen: function() {
            this.main.show();
        },

        loginScreen: function() {
            this.login.show();
        },

        signupScreen: function() {
            this.signup.show();
        },

        scoreboardScreen: function() {
            this.scoreboard.show();
        },

        gameScreen: function() {
            this.game.show();
        }
		
	});

	return new Manager();
});