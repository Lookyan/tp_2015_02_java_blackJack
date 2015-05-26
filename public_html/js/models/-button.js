define([
    'backbone'
], function(
    Backbone
){

    var Model = Backbone.Model.extend({
        defaults: {
            isDisabled: false
        },
        initialize: function () {
            console.log("Btn was created");
        }

    });

    return Model;
});