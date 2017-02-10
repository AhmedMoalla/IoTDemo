var mosca = require('mosca');

var defaults = {
    port: 1883
};

module.exports = function startMQTTServer(callback, settings) {
    settings = settings || defaults;
    server = new mosca.Server(settings);

    server.on('clientConnected', function (client) {
        console.log('Client connected ID:', client.id);
    });

    // fired when a message is received
    /*
    server.on('published', function (packet, client) {
        console.log('Published', packet);
    });*/

    server.on('ready', setup);

    // fired when the mqtt server is ready
    function setup() {
        callback(server);
    }
}