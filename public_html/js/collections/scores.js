define([
    'backbone',
    'models/score',
    'api/scoreSync'
], function(
    Backbone, Score, ScoreSync
){

    var Collection = Backbone.Collection.extend({
    	sync: ScoreSync,
    	url: '/',
    	model: Score,
    	initialize: function () {
    		this.fetch();
    		//console.log("collection");
    	},

    	comparator: function (score) {
    		return (-1) * score.get("score");
    	},

    	scoreSuccess: function(data) {
    		this.reset();
    		var self = this;
    		var models = JSON.parse(data);
    		_.each(models.body, function(score) {
    			debugger;
				var s = new Score(score);
//				s.save();
				self.add(s);
    		});
    	}
    });

    var Scores = new Collection();

    return Scores;
});