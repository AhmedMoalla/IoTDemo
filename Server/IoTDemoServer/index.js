const startMQTTBroker = require('./back/mqtt/mqttBroker'),
    config = require('./config'),
    RemoteSensor = require('./back/RemoteSensor'),
    {
        HEARTBEAT_TOPIC
    } = require('./back/mqtt/topics');

startMQTTBroker((server) => {
    console.log('Mosca server is up and running on port', config.mqttBrokerPort);
}, {
    port: config.mqttBrokerPort,
    http: { // configuration to run over websockets for communication with react
        port: 3001,
        bundle: true,
        static: './'
    }
});