define([
    'backbone',
    'models/card'
], function(
    Backbone, Card
){

    var Collection = Backbone.Collection.extend({
        model: Card,
        initialize: function () {
            //console.log("collection");
        }
    });

    return Collection;
});