const RemoteSensor = require('./back/RemoteSensor'),
	{
		HEARTBEAT_TOPIC
	} = require('./back/mqtt/topics');

const heartbeat = new RemoteSensor(HEARTBEAT_TOPIC);

heartbeat.onDataReceived((data) => {
	console.log('[data]', data.toString());
})

heartbeat.onErrorReceived((err) => {
	console.log('[error]', err.toString());
})

const method = process.argv[2];
if (method === "on") {
	console.log('Activating ...')
	heartbeat.activate();
} else if (method === "off") {
	console.log('Deactivating ...');
	heartbeat.deactivate();
}