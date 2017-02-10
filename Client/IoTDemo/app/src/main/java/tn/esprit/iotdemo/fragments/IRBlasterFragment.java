package tn.esprit.iotdemo.fragments;


import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tn.esprit.iotdemo.R;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class IRBlasterFragment extends Fragment {

    private ConsumerIrManager mIRBlasterManager;


    public IRBlasterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIRBlasterManager = (ConsumerIrManager) getActivity().getSystemService(Context.CONSUMER_IR_SERVICE);
        //mIRBlasterManager.transmit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_irblaster, container, false);
    }

}
