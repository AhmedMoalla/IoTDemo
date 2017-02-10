package tn.esprit.iotdemo.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import tn.esprit.iotdemo.MainActivity;
import tn.esprit.iotdemo.R;
import tn.esprit.iotdemo.mqtt.MqttClient;

public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private static final String TAG = LocationFragment.class.getName();

    private static final String PUB_TOPIC = "IoTDemo/Location:Data";
    private static final String SUB_TOPIC = "IoTDemo/Location:Control";
    private static final String ERROR_TOPIC = "IoTDemo/Location:Error";

    private static final String MESSAGE_ON = "ON";
    private static final String MESSAGE_OFF = "OFF";

    private MqttClient mMqttClient;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private boolean mIsActive = false;

    // View components
    TextView mTxtLatitude;
    TextView mTxtLongitude;
    TextView mTxtStatus;
    MapView mMapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMqttClient = ((MainActivity) getActivity()).getMqttClient();

        mMqttClient.subscribe(SUB_TOPIC, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Subscribed successfully to: " + SUB_TOPIC);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                Log.d(TAG, "Error occured while trying to subscribe to: " + SUB_TOPIC);
                e.printStackTrace();
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Log.d(TAG, "Message : '" + msg + "' arrived on topic : '" + topic + "'");
                if (msg.equals(MESSAGE_ON)) {
                    if (!isActive()) {
                        setupGoogleLocationApi();
                        ((MainActivity) getActivity()).fab.setImageResource(R.drawable.ic_media_pause);
                        Log.e(TAG, "[Server] Activating Location service.");
                    } else {
                        Log.d(TAG, "[Server] Location service already active !");
                    }
                } else if (msg.equals(MESSAGE_OFF)) {
                    if (isActive()) {
                        disconnect();
                        ((MainActivity) getActivity()).fab.setImageResource(R.drawable.ic_media_play);
                        Log.e(TAG, "[Server] Deactivating Location service.");
                    } else {
                        Log.d(TAG, "[Server] Location service already inactive !");
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        mTxtLatitude = (TextView) rootView.findViewById(R.id.txt_latitude_location);
        mTxtLongitude = (TextView) rootView.findViewById(R.id.txt_longitude_location);
        mTxtStatus = (TextView) rootView.findViewById(R.id.txt_status_location);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.getUiSettings().setMyLocationButtonEnabled(false);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

                MapsInitializer.initialize(getActivity());
                LatLng tunisiaLatLng = new LatLng(36.8017133d, 10.3447479d);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(tunisiaLatLng, 8);
                map.animateCamera(cameraUpdate);
                LocationFragment.this.map = map;
            }
        });

        return rootView;
    }

    public void setupGoogleLocationApi() {
        // Start Google Client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mTxtStatus.setTextColor(0xff99cc00); // green
        mTxtStatus.setText("Activated");
        mIsActive = true;
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
        mTxtStatus.setTextColor(0xffff4444); // red
        mTxtStatus.setText("Not Activated");
        mIsActive = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = createLocationRequest();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("MainActivity", "Connection to Google API suspended");
        mMqttClient.publish(ERROR_TOPIC, "Connection to Google API suspended");
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location Update", "Latitude: " + location.getLatitude() +
                " Longitude: " + location.getLongitude());
        mTxtLatitude.setText(Double.toString(location.getLatitude()));
        mTxtLongitude.setText(Double.toString(location.getLongitude()));
        // Update map
        LatLng newPos = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newPos, 16);
        map.animateCamera(cameraUpdate);

        // Notify server here!
        mMqttClient.publish(PUB_TOPIC, Double.toString(location.getLatitude()) + "|" + Double.toString(location.getLongitude()));
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public boolean isActive() {
        return mIsActive;
    }
}
