package tn.esprit.iotdemo.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

/**
 * Created by AHMED on 2/10/2017.
 */

public class MqttClient {

    public static final String TAG = MqttClient.class.getName();

    private String mServerUrl;
    private String mClientId;
    private Context mContext;
    private MqttAndroidClient mMqttAndroidClient;

    private ArrayList<MqttSubscription> mSubscriptions;

    public MqttClient(Context context, String serverUrl, String clientId) {
        mServerUrl = serverUrl;
        mClientId = clientId;
        mContext = context;
        mMqttAndroidClient = new MqttAndroidClient(mContext, mServerUrl, mClientId);
        mSubscriptions = new ArrayList<>();
    }

    public void connect() {

        Log.d(TAG, "Trying to connect to MQTT Broker... (ClientId : " + mClientId + ")");

        mMqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    commitSubscriptions();
                } else {
                    Log.d(TAG, "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            Log.d(TAG, "Connecting to " + mServerUrl);
            mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mMqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    commitSubscriptions();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to: " + mServerUrl);
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void publish(String topic, String msg){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mMqttAndroidClient.publish(topic, message);
            Log.d(TAG, "Message Published");
            if(!mMqttAndroidClient.isConnected()){
                Log.d(TAG, mMqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, IMqttActionListener connectionListener, IMqttMessageListener messageListener) {
        mSubscriptions.add(new MqttSubscription(topic, connectionListener, messageListener));
    }

    private void commitSubscriptions() {
        for (MqttSubscription sub : mSubscriptions) {
            sub.subscribe();
        }
    }

    private class MqttSubscription {
        private String mTopic;
        private IMqttActionListener mConnectionListener;
        private IMqttMessageListener mMessageListener;

        public MqttSubscription(String mTopic, IMqttActionListener mConnectionListener, IMqttMessageListener mMessageListener) {
            this.mTopic = mTopic;
            this.mConnectionListener = mConnectionListener;
            this.mMessageListener = mMessageListener;
        }

        public void subscribe() {
            try {
                mMqttAndroidClient.subscribe(mTopic, 0, null, mConnectionListener);

                // THIS DOES NOT WORK!
                mMqttAndroidClient.subscribe(mTopic, 0, mMessageListener);

            } catch (MqttException ex) {
                System.err.println("Exception whilst subscribing");
                ex.printStackTrace();
            }
        }

        public String getTopic() {
            return mTopic;
        }

        public void setTopic(String topic) {
            this.mTopic = topic;
        }

        public IMqttActionListener getConnectionListener() {
            return mConnectionListener;
        }

        public void setConnectionListener(IMqttActionListener connectionListener) {
            this.mConnectionListener = connectionListener;
        }

        public IMqttMessageListener getMessageListener() {
            return mMessageListener;
        }

        public void setMessageListener(IMqttMessageListener messageListener) {
            this.mMessageListener = messageListener;
        }
    }
}
