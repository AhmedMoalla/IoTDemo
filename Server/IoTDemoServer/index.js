const startMQTTBroker = require('./back/mqtt/mqttBroker'),
      config = require('./config'),
      RemoteSensor = require('./back/RemoteSensor'),
      { HEARTBEAT_TOPIC } = require('./back/mqtt/topics');

startMQTTBroker((server) => {
    console.log('Mosca server is up and running on port', config.mqttBrokerPort);

}, { port: config.mqttBrokerPort });
