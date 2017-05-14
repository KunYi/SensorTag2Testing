package com.uti.sensors.bleshow;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.util.Log;

import android.os.Bundle;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleConnection;
import static com.trello.rxlifecycle.android.FragmentEvent.DESTROY;
import static com.trello.rxlifecycle.android.FragmentEvent.PAUSE;
import static java.lang.StrictMath.pow;

import com.polidea.rxandroidble.utils.ConnectionSharingAdapter;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.uti.sensors.bleshow.Devices.DeviceContext;

import java.util.Observable;
import java.util.UUID;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kunyi on 2017/5/6.
 */

public class DeviceViewFragment extends Fragment {
    private static final String TAG = "DeviceViewFragment";
    private RxBleClient mRxBleClient;
    private rx.Observable<RxBleConnection> mRxBleConnection;
    private Subscription connection;
    private Subscription connectionState;
    private ContentLoadingProgressBar  progress;
    private SimpleKeyProfile simpleKey;
    private MovementProfile movement;
    private TempertureProfile temperture;
    private LuxometerProfile luxometer;

    public DeviceViewFragment() {
        super();
    }
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_TITLE = "section_title";
    public String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxBleClient = AppExt.getRxBleClient(getContext());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destory():" + title);
        if (connection != null)
            connection.unsubscribe();

        if (connectionState != null)
            connectionState.unsubscribe();

        if (connection != null)
            connection = null;

        if (connectionState != null)
            connectionState = null;
    }

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


        RxBleDevice dev = mRxBleClient.getBleDevice(this.title);

        if (dev.getConnectionState() != RxBleConnection.RxBleConnectionState.CONNECTED)
            connectionState =  dev.observeConnectionStateChanges()
                //.compose(bindUntilEvent(DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectionStateChange);


        if (dev.getConnectionState() != RxBleConnection.RxBleConnectionState.CONNECTED)
            connection = dev.establishConnection(false)
                //.compose(bindUntilEvent(PAUSE))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose( rxBleConnectionObservable ->
                        mRxBleConnection = new ConnectionSharingAdapter() {
                            @Override
                            public rx.Observable<RxBleConnection> call(rx.Observable<RxBleConnection> source) {
                                return super.call(source);
                            }
                        }.call(rxBleConnectionObservable))
                .subscribe( rxBleConnection -> {

                });
        return view;
    }

    private void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
        if (newState.equals(RxBleConnection.RxBleConnectionState.CONNECTED)) {
            Log.d(TAG, "Device:"+title + "  connected");
            DeviceContext.ITEM_MAP.get(title).bConnected = true;
            RxBleDevice dev = mRxBleClient.getBleDevice(this.title);

            // for SimpleKey services
            simpleKey = new SimpleKeyProfile(mRxBleConnection);

            if (simpleKey.configuration())
                simpleKey.registerNotification();

            // For  Movement sensor services
            movement = new MovementProfile(mRxBleConnection);
            if (movement.configuration())
                movement.registerNotification();

            temperture = new TempertureProfile(mRxBleConnection);
            if(temperture.configuration())
                temperture.registerNotification();

            luxometer = new LuxometerProfile(mRxBleConnection);
            if(luxometer.configuration())
                luxometer.registerNotification();
        }
        else if (newState.equals(RxBleConnection.RxBleConnectionState.CONNECTING)) {
            Log.d(TAG, "Device:"+title+ "  connecting");
        }
        else if (newState.equals(RxBleConnection.RxBleConnectionState.DISCONNECTED)) {

        }
        else if (newState.equals(RxBleConnection.RxBleConnectionState.DISCONNECTING))
        {

        }
    }
}
