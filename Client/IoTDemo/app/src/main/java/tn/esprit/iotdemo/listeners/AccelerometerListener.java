package tn.esprit.iotdemo.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by AHMED on 2/8/2017.
 */

public class AccelerometerListener implements SensorEventListener {

    public static final String TAG = AccelerometerListener.class.getSimpleName();
    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        //Log.i(TAG + "@onSensorChanged", "(" + x + ", " + y + ", " + z + ")");


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(this.getClass().getSimpleName() + "@onSensorChanged", "Sensor : " + sensor + ", Accuracy : " + accuracy);
    }
}
