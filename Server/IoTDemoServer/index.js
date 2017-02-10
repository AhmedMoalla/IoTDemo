const startMQTTServer = require('./mqttServer')

startMQTTServer((server) => {

    var mqtt = require('mqtt')
    var client = mqtt.connect('mqtt://localhost:1883')

    client.on('connect', function () {
        client.subscribe('IoTPubTopic')
        
    })

    client.on('message', function (topic, message) {
        // message is Buffer 
        console.log(topic, message.toString())
        client.publish('IoTSubTopic', 'Hello from IoTDemoServer')
    })

});