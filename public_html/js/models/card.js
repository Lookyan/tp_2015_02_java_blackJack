define([
    'backbone'
], function(
    Backbone
){

    var Model = Backbone.Model.extend({

        code: "",
        player: "",

        initialize: function (options) {
            this.code = options.code;
            this.player = options.player;
            //this.x = options.x;
        }

    });

    return Model;
});