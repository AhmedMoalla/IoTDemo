var mosca = require('mosca');

var defaults = {
    port: 1883
};
var port = defaults.port;

module.exports = function startMQTTServer(callback, settings) {
    settings = settings || defaults;
    port = settings.port;
    server = new mosca.Server(settings);

    server.on('clientConnected', function (client) {
        console.log('Client connected ID: ', client.id);
    });

    // fired when a message is received
    /*
    server.on('published', function (packet, client) {
        console.log('Published', packet);
    });*/

    server.on('ready', setup);

    // fired when the mqtt server is ready
    function setup() {
        console.log('Mosca server is up and running on port ', port);
        callback(server);
    }
}