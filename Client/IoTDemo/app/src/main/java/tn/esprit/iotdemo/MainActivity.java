package tn.esprit.iotdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

import java.util.UUID;

import tn.esprit.iotdemo.fragments.HeartbeatSensorFragment;
import tn.esprit.iotdemo.fragments.LocationFragment;
import tn.esprit.iotdemo.fragments.PlaceholderFragment;
import tn.esprit.iotdemo.listeners.AccelerometerListener;
import tn.esprit.iotdemo.mqtt.MqttClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton fab;

    private MqttClient mMqttClient;
    private static final String BROKER_URL = "tcp://192.168.1.3:1883";
    private static final String CLIENT_ID = "IoTDemo:" + UUID.randomUUID().toString();
    private static final String SUB_TOPIC = "IoTSubTopic";
    private static final String PUB_TOPIC = "IoTPubTopic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO Sensor.TYPE_LINEAR_ACCELERATION
        // TODO Context.FINGERPRINT BALALB

        mMqttClient = new MqttClient(getApplicationContext(), BROKER_URL, CLIENT_ID);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BODY_SENSORS, Manifest.permission.TRANSMIT_IR}, 1337);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMqttClient.publish(PUB_TOPIC, "Hello from android !");

                /*
                Fragment currentFragment = ((SectionsPagerAdapter) mViewPager.getAdapter()).getFragment(mViewPager.getCurrentItem());

                if (currentFragment instanceof LocationFragment) {
                    LocationFragment frag = (LocationFragment) currentFragment;
                    if (!frag.isActive()) {
                        frag.setupGoogleLocationApi();
                        fab.setImageResource(R.drawable.ic_media_pause);
                        Log.e("MainActivity", "Activating location service.");
                    } else {
                        frag.disconnect();
                        fab.setImageResource(R.drawable.ic_media_play);
                        Log.e("MainActivity", "Deactivating location service.");
                    }
                    return;
                }
                if (currentFragment instanceof HeartbeatSensorFragment) {
                    HeartbeatSensorFragment frag = (HeartbeatSensorFragment) currentFragment;
                    if (!frag.isActive()) {
                        frag.activateSensor();
                        fab.setImageResource(R.drawable.ic_media_pause);
                        Log.e("MainActivity", "Activating heartbeat service.");
                    } else {
                        frag.deactivateSensor();
                        fab.setImageResource(R.drawable.ic_media_play);
                        Log.e("MainActivity", "Deactivating heartbeat service.");
                    }
                    return;
                }
                */
            }
        });

        // on change fragment pause current service
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                deactivateAllService();
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        mMqttClient.connect();
        mMqttClient.subscribe(SUB_TOPIC, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Failed to subscribe");
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // message Arrived!
                System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
            }
        });
    }

    private void deactivateAllService() {
        SectionsPagerAdapter adapter = ((SectionsPagerAdapter) mViewPager.getAdapter());

        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment currentFragment = adapter.getFragment(i);
            if (currentFragment instanceof LocationFragment) {
                LocationFragment frag = (LocationFragment) currentFragment;
                if (frag.isActive()) {
                    frag.disconnect();
                    fab.setImageResource(R.drawable.ic_media_play);
                    Log.e("MainActivity", "Deactivating location service.");
                }
                return;
            }
            if (currentFragment instanceof HeartbeatSensorFragment) {
                HeartbeatSensorFragment frag = (HeartbeatSensorFragment) currentFragment;
                if (frag.isActive()) {
                    frag.deactivateSensor();
                    fab.setImageResource(R.drawable.ic_media_play);
                    Log.e("MainActivity", "Deactivating heartbeat service.");
                }
                return;
            }
        }

    }


}
