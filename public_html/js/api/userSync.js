define([
    'backbone'
], function(
    Backbone
) {
    //var self = this;

    var methodMap = {
        'create': 'POST',
        'update': 'POST',
        'delete': 'GET',
        'read': 'GET',
        'token': 'GET'
    };

    var urlMap  = {
        'login': '/api/auth/signin/',
        'signup': '/api/auth/signup',
        'identify': '/api/auth/identify',
        'logout': '/api/auth/logout',
        'token': 'api/auth/token'
    };

	return function(method, model, options) {

        var params = {type: methodMap[method]};
        var modelData = model.toJSON();
        var success;

        if (method === 'create') {
            params.dataType = 'json';
            params.contentType = 'application/json';
            params.processData = false;
            delete modelData.chips;

            if (model.get('email') === "") {
                params.url = urlMap['login'];
                delete modelData.email;
                params.data = JSON.stringify(modelData);
                success = model.loginSuccess;
            } else {
                params.url = urlMap['signup'];
                params.data = JSON.stringify(modelData);
                success = model.signUpSuccess;
            }
        }

        if (method === 'read') {
            params.url = urlMap['identify'];
            success = model.identifySuccess;
        }

        if (method === 'delete') {
            params.url = urlMap['logout'];
            success = model.logoutSuccess;
        }
        //debugger;

        if (method === 'token') {
            params.url = urlMap['token'];
            success = model.tokenSuccess;
        }

        //if (!options.success) {
        //    params.success = options.success;
        //} else {
            params.success = success;
        //}

        params.error = function() {
            model.trigger('syncError');
            model.set('name', '');
            model.set('password', '');
            model.set('email', '');
            model.set('chips', 0);
            model.isLogged = false;
        };

        params.complete = function(xhr, txt) {
            console.log(JSON.stringify(xhr));
            console.log(txt);
        };

        params.context = model;

        var xhr = Backbone.ajax(params);




        //var urlSuffix = '', callSuccess = options.callbacks.success,
        //    callError = options.callbacks.error, data = {};
        //
        //if (method == 'read') {
        //    data = options.data;
        //}
        //else {
        //    data = (model instanceof Backbone.Model)?model.toJSON():{};
        //}



        //var xhr = $.ajax({
        //    type: methodMap[method],
        //    url: model.url + urlMap[options.method],
        //    data: JSON.stringify(data),
        //    dataType: 'json'
        //}).done(function(data) {
        //    if (data.status == 1) {
        //        options.success();
        //        callSuccess(data);
        //    } else {
        //        options.error();
        //        callError(data.message);
        //    }
        //}).fail(function(data) {
        //    callError("Connection error");
        //});
        return xhr;
    
    };

});