const RemoteSensor = require('./back/RemoteSensor'),
      { HEARTBEAT_TOPIC } = require('./back/mqtt/topics');

const heartbeat = new RemoteSensor(HEARTBEAT_TOPIC);
heartbeat.deactivate();