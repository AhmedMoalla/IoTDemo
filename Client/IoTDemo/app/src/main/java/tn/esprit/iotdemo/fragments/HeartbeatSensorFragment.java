package tn.esprit.iotdemo.fragments;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.vision.text.Text;

import tn.esprit.iotdemo.R;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartbeatSensorFragment extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mHeartbeat;
    private boolean mIsActive = false;

    private TextView mTxtHeartbeat;
    private TextView mTxtStatus;
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_heartbeat_sensor, container, false);

        mTxtHeartbeat = (TextView) rootView.findViewById(R.id.txt_heartbeat);
        mTxtStatus = (TextView) rootView.findViewById(R.id.txt_status_heartbeat);
        mChart = (LineChart) rootView.findViewById(R.id.chart);

        configureChart();

        return rootView;
    }

    private void configureChart() {
        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLUE);

        // add empty data
        mChart.setData(data);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.GRAY);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(90f);
        leftAxis.setAxisMinimum(55f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        for (int i = 0; i < 5; i ++) addEntry(i);
    }

    private void addEntry(float value) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(ColorTemplate.getHoloBlue());
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mHeartbeat = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deactivateSensor();
    }

    public void activateSensor() {
        mSensorManager.registerListener(this, mHeartbeat, SensorManager.SENSOR_DELAY_NORMAL);
        mIsActive = true;
    }

    public void deactivateSensor() {
        mSensorManager.unregisterListener(this);
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mTxtHeartbeat.setText(Double.toString(event.values[0]));
        addEntry(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        if (accuracy == 0) {
            Toast.makeText(getActivity(), "Put your finger near the sensor !", Toast.LENGTH_LONG).show();
            mTxtStatus.setText("Not Recording");
            mTxtStatus.setTextColor(0xffff4444); // red
        } else {
            mTxtStatus.setText("Recording");
            mTxtStatus.setTextColor(0xff99cc00); // green
        }
    }
}
