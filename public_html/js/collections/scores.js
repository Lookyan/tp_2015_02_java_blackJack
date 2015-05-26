define([
    'backbone',
    'models/score'
], function(
    Backbone, Score
){

    var Collection = Backbone.Collection.extend({
    	model: Score,
    	initialize: function () {
    		//console.log("collection");
    	},
    	comparator: function (score) {
    		return (-1) * score.get("score");
    	}
    });

    var Scores = new Collection([
    	{name: 'Player1', score: 235436},
    	{name: 'Rtyx', score: 23},
    	{name: 'Alex', score: 2336},
    	{name: 'Andrey', score: 23543},
    	{name: 'Test', score: 26},
    	{name: 'AnotherOne', score: 23536},
    	{name: 'Player2', score: 436},
    	{name: 'Tydewy', score: 16},
    	{name: 'WWW', score: 3},
    	{name: 'DOWN', score: 36}
    ]);

    return Scores;
});