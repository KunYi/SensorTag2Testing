package com.uti.sensors.bleshow;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.polidea.rxandroidble.RxBleClient;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.exceptions.BleScanException;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import com.uti.sensors.bleshow.Devices.DeviceContext;
import com.uti.sensors.bleshow.Devices.DeviceContext.DeviceItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RxAppCompatActivity
        implements ScanDevicesFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ScanDevicesFragment mScanDevices;
    private List<DeviceViewFragment>  mDevices;

    private RxBleClient mRxBleClient;
    private Subscription mScanSubscroption;
    private final static String FilterDeviceName = "CC2650 SensorTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() entry");

        setContentView(R.layout.activity_main);
        checkPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mScanDevices = ScanDevicesFragment.newInstance(1);
        mDevices = new ArrayList<DeviceViewFragment>();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mSectionsPagerAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mViewPager.setCurrentItem(mDevices.size());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    mScanDevices.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mRxBleClient = AppExt.getRxBleClient(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        mDevices.clear();
        for (DeviceItem it : DeviceContext.ITEMS) {
            if (it.fragment != null)
                mDevices.add(it.fragment);
        }
    }

    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            boolean isCoarse = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED);
            boolean isFine = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED);

            if (!isCoarse || !isFine) {
                ActivityCompat.requestPermissions(this, new String [] {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION },
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return mScanDevices;

            // Return DeviceViewFragment
            position -= 1;
            return mDevices.get(position);
        }

        @Override
        public int getCount() {
            return mDevices.size()+1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Sensors";

            position -= 1;
            return mDevices.get(position).title;
        }
    }

    private boolean isScanning() {
        return (mScanSubscroption != null);
    }

    private void scanBleDevices() {
        mScanSubscroption = mRxBleClient
                .scanBleDevices()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(rxBleScanResult -> {
                    return FilterDeviceName.equals(rxBleScanResult.getBleDevice().getName());
                })
                .subscribe( rxBleScanResult -> {
                    int position = DeviceContext.AddorUpdateDevice(rxBleScanResult.getBleDevice()
                            .getMacAddress(),
                            rxBleScanResult.getRssi());
                    if (position >= 0)
                        mScanDevices.getAdapter().notifyItemChanged(position);
                    else
                        mScanDevices.getAdapter().notifyDataSetChanged();
                }, this::onScanFailure);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isScanning()) {
            scanBleDevices();
        }
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isScanning()) {
            if (!mScanSubscroption.isUnsubscribed())
                mScanSubscroption.unsubscribe();
        }
    }

    @Override
    public void onListFragmentInteraction(DeviceItem item) {
        boolean wantConnect = !item.bConnected;
        mScanDevices.getAdapter().notifyItemChanged(item.position);

        if (wantConnect) {
            DeviceViewFragment fragment = DeviceViewFragment.newInstance(mDevices.size(), item.MAC);
            fragment.title = item.MAC;
            item.fragment = fragment;
            mDevices.add(fragment);
            mSectionsPagerAdapter.notifyDataSetChanged();
            mScanSubscroption.unsubscribe();
        }
    }

    private void onScanFailure(Throwable throwable) {

        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }

    private void handleBleScanException(BleScanException bleScanException) {

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                Toast.makeText(MainActivity.this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                Toast.makeText(MainActivity.this, "Enable bluetooth and try again", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                Toast.makeText(MainActivity.this,
                        "On Android 6.0 location permission is required. Implement Runtime Permissions", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                Toast.makeText(MainActivity.this, "Location services needs to be enabled on Android 6.0", Toast.LENGTH_SHORT).show();
                break;
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                Toast.makeText(MainActivity.this, "Unable to start scanning", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
