define([
    'backbone'
], function(
    Backbone
) {
    //var self = this;

    var scoreMethod = 'GET';

    var scoreUrl = '/api/top';

	return function(method, model, options) {

        var params = {type: scoreMethod};
        var modelData = model.toJSON();
        var success;

        params.url = scoreUrl;
        success = model.scoreSuccess;


        params.success = success;

        params.error = function() {
            model.reset();
        };

        params.complete = function(xhr, txt) {
        };

        params.context = model;

        var xhr = Backbone.ajax(params);
        return xhr;

    };

});