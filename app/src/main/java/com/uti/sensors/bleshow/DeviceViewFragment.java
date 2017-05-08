package com.uti.sensors.bleshow;

import android.bluetooth.BluetoothClass;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.os.Bundle;

/**
 * Created by kunyi on 2017/5/6.
 */

public class DeviceViewFragment extends Fragment {

    public DeviceViewFragment() {
        super();
    }
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_TITLE = "section_title";
    public String title;


    public static DeviceViewFragment newInstance(int sectionNumber, String title) {
        DeviceViewFragment fragment = new DeviceViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_SECTION_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.title = getArguments().getString(ARG_SECTION_TITLE);
        return view;
    }
}
