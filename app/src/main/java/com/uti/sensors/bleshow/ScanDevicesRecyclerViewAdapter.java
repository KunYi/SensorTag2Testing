package com.uti.sensors.bleshow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.uti.sensors.bleshow.ScanDevicesFragment.OnListFragmentInteractionListener;
import com.uti.sensors.bleshow.Devices.DeviceContext.DeviceItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DeviceItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ScanDevicesRecyclerViewAdapter extends RecyclerView.Adapter<ScanDevicesRecyclerViewAdapter.ViewHolder> {

    private final List<DeviceItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ScanDevicesRecyclerViewAdapter(List<DeviceItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitle.setText(holder.mItem.name);
        holder.mMac.setText(holder.mItem.MAC);
        holder.mRssi.setText(" RSSI: " + String.valueOf(holder.mItem.nRSSI));
        holder.mConnected.setEnabled(holder.mItem.state == DeviceItem.CONNECT_STATE.DISCONNECTED);
        holder.mConnected.setChecked(holder.mItem.state == DeviceItem.CONNECT_STATE.CONNECTED);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMac;
        public final TextView mTitle;
        public final TextView mRssi;
        public final CheckBox mConnected;
        public DeviceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.title);
            mMac = (TextView) view.findViewById(R.id.mac);
            mRssi = (TextView) view.findViewById(R.id.rssi);
            mConnected = (CheckBox) view.findViewById(R.id.chkConnect);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMac.getText() + "'";
        }
    }
}
