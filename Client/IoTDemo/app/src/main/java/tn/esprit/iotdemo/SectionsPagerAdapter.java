package tn.esprit.iotdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;

import java.util.HashMap;
import java.util.Map;

import tn.esprit.iotdemo.fragments.HeartbeatSensorFragment;
import tn.esprit.iotdemo.fragments.IRBlasterFragment;
import tn.esprit.iotdemo.fragments.LocationFragment;
import tn.esprit.iotdemo.fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch(position) {
            case 0:
                return Fragment.instantiate(mContext, HeartbeatSensorFragment.class.getName(), null);
            case 1:
                return Fragment.instantiate(mContext, LocationFragment.class.getName(), null);
            default:
                Bundle args = new Bundle();
                args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, position + 1);
                return Fragment.instantiate(mContext, PlaceholderFragment.class.getName(), args);
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Heartbeat Sensor";
            case 1:
                return "Location";
        }
        return null;
    }
}