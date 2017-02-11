import mqtt from 'mqtt';
import config from '../../config';

const MESSAGE_ON = 'ON',
    MESSAGE_OFF = 'OFF',
    DATA_SUFFIX = 'Data',
    CONTROL_SUFFIX = 'Control',
    ERROR_SUFFIX = 'Error';

class RemoteSensor {

    // Connects to topic
    constructor(sensorTopic) {
        this.sensorTopic = sensorTopic;
        this.client = mqtt.connect(config.mqttBrokerUrl);
        this.client.on('connect', () => {
            // Subscribe to data and error topic
            this.subscribe(DATA_SUFFIX);
            this.subscribe(ERROR_SUFFIX);
            console.log('Connected to remote sensor @' + sensorTopic);
        })
        this.client.on('error', (err) => {
            console.log('[RemoteSensor:Error]', err);
        })
    }

    // Publish on the control topic
    activate() {
        this.publish(CONTROL_SUFFIX, MESSAGE_ON);
    }

    // Publish on the control topic
    deactivate() {
        this.publish(CONTROL_SUFFIX, MESSAGE_OFF);
    }

    // Publish on a subtopic of the main one ('topic:suffix')
    publish(suffix, message) {
        this.client.publish(`${this.sensorTopic}:${suffix}`, message);
    }

    // Subscribe to a subtopic of the main one ('topic:suffix')
    subscribe(suffix) {
        console.log(`[RemoteSensor] Subscribe to '${this.sensorTopic}:${suffix}'`)
        this.client.subscribe(`${this.sensorTopic}:${suffix}`);
    }

    // Handle message reception from sensor
    onDataReceived(callback) {
        this.client.on('message', (topic, message) => {
            if (topic === `${this.sensorTopic}:${DATA_SUFFIX}`)
                callback(message);
            else console.log('not data', message.toString())
        })
    }

    // Handle error reception from sensor
    onErrorReceived(callback) {
        this.client.on('message', (topic, message) => {
            if (topic === `${this.sensorTopic}:${ERROR_SUFFIX}`)
                callback(message);
        })
    }

}

export default RemoteSensor;