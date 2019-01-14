

var exec = require('cordova/exec');
var platform = require('cordova/platform');

module.exports = {

    methods: {},

    //
    executeMethod: function resultForCallback(callbackId, resultArray) {
        try {
            var callback = navigator.metaio.methods[callbackId];
            if (!callback) return;
            callback.apply(null, resultArray);
        } catch (e) {
            alert(e)
        }
    },

    metaioCloud: function (channel, methods) {
        var args = [];

        for (var i = 0; i < methods.length; i++) {
            var hasCallback = methods[i] && typeof  methods[i]["fn"] == "function",
                callbackId = methods[i]["name"];

            if (hasCallback) {
                navigator.metaio.methods[callbackId] = methods[i]["fn"];
                args.push(methods[i]["name"]);
            }
        }

        args.unshift(channel);

        // exec(<successFunction>, <failFunction>, <service>, <action>, [<args>]);
        cordova.exec(
            function (a) {
                console.log(a)
            },
            function (err) {
                callback('Non funziona il plugin! ' + err);
            },
            "MetaioCloud",
            "metaiocloud",
            args
        );
    }
}