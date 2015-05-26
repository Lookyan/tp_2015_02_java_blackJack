define([
    'backbone'
], function(
    Backbone
){

    var Model = Backbone.Model.extend({
        defaults: {
            value: 0
        },
        initialize: function () {
            console.log("Chip was created");
        }

    });

    return Model;
});