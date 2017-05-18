package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.uti.Utils.GenericTabRow;
import com.uti.Utils.SparkLineView;

/**
 * Created by kunyi on 2017/5/18.
 */

public class MovementTabRow extends GenericTabRow {
    public final SparkLineView sl4, sl5, sl6;
    public final SparkLineView sl7, sl8, sl9;
    public final AppCompatTextView gyroValue;
    public final AppCompatTextView magValue;
    private final boolean DBG = false;
    private final String TAG = "MovementTabRow";
    public Switch WOS;

    public MovementTabRow(Context con) {
        super(con);
        final int nextGuiId = this.sl3.getId();


        this.sl1.autoScale = this.sl2.autoScale = this.sl3.autoScale = true;
        this.sl1.autoScaleBounceBack = this.sl2.autoScaleBounceBack = this.sl3.autoScaleBounceBack = false;
        this.sl2.setVisibility(VISIBLE);
        this.sl3.setVisibility(VISIBLE);
        this.sl2.setEnabled(true);
        this.sl3.setEnabled(true);
        this.sl2.setColor(255, 0, 150, 125);
        this.sl3.setColor(255, 0, 0, 0);

        //One Sparkline showing Gyroscope trends
        this.sl4 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                autoScale = true;
                autoScaleBounceBack = true;
                setId(nextGuiId + 1);
            }
        };
        this.sl5 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                autoScale = true;
                autoScaleBounceBack = true;
                setColor(255, 0, 150, 125);
                setId(nextGuiId + 2);
            }
        };
        this.sl6 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                autoScale = true;
                autoScaleBounceBack = true;
                setColor(255, 0, 0, 0);
                setId(nextGuiId + 3);
            }
        };
        //Three Sparkline showing Magnetometer trends
        this.sl7 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                autoScale = true;
                autoScaleBounceBack = true;
                setId(nextGuiId + 4);
            }
        };
        this.sl8 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                setColor(255, 0, 150, 125);
                autoScale = true;
                autoScaleBounceBack = true;
                setId(nextGuiId + 5);
            }
        };
        this.sl9 = new SparkLineView(con) {
            {
                setVisibility(VISIBLE);
                setColor(255, 0, 0, 0);
                autoScale = true;
                autoScaleBounceBack = true;
                setId(nextGuiId + 6);
            }
        };
        this.gyroValue = new AppCompatTextView(con) {
            {
                setTextSize(TypedValue.COMPLEX_UNIT_PT, 8.0f);
                setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
                setId(nextGuiId + 7);
                setVisibility(VISIBLE);
            }
        };
        this.magValue = new AppCompatTextView(con) {
            {
                setTextSize(TypedValue.COMPLEX_UNIT_PT, 8.0f);
                setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
                setId(nextGuiId + 8);
                setVisibility(VISIBLE);
            }
        };

        this.WOS = new Switch(con) {
            {
                setText("Wake on shake");
                setId(nextGuiId + 9);
            }
        };

        RelativeLayout.LayoutParams tmpLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
                this.sl3.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        gyroValue.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.setMargins(0, 10, 0, 20);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
                gyroValue.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        this.sl4.setLayoutParams(tmpLayoutParams);
        this.sl5.setLayoutParams(tmpLayoutParams);
        this.sl6.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
                this.sl6.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        magValue.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.setMargins(0, 10, 0, 20);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
                magValue.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        this.sl7.setLayoutParams(tmpLayoutParams);
        this.sl8.setLayoutParams(tmpLayoutParams);
        this.sl9.setLayoutParams(tmpLayoutParams);

        RelativeLayout.LayoutParams WOSLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        WOSLayoutParams.setMargins(0, 10, 0, 20);
        WOSLayoutParams.addRule(RelativeLayout.BELOW,
                sl9.getId());
        WOSLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
        this.WOS.setLayoutParams(WOSLayoutParams);

        rowLayout.addView(gyroValue);
        rowLayout.addView(this.sl4);
        rowLayout.addView(this.sl5);
        rowLayout.addView(this.sl6);

        rowLayout.addView(magValue);
        rowLayout.addView(this.sl7);
        rowLayout.addView(this.sl8);
        rowLayout.addView(this.sl9);
        rowLayout.addView(this.WOS);
    }

    @Override
    public void onClick(View v) {
        this.config = !this.config;
        Log.d(TAG, "onClick(), Row ID" + v.getId());
        //Toast.makeText(this.context, "Found row with title : " + this.title.getText(), Toast.LENGTH_SHORT).show();
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setAnimationListener(this);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(0);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setAnimationListener(this);
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(250);
        if (this.config == true) {
            this.sl1.startAnimation(fadeOut);
            this.sl2.startAnimation(fadeOut);
            this.sl3.startAnimation(fadeOut);
            this.sl4.startAnimation(fadeOut);
            this.sl5.startAnimation(fadeOut);
            this.sl6.startAnimation(fadeOut);
            this.sl7.startAnimation(fadeOut);
            this.sl8.startAnimation(fadeOut);
            this.sl9.startAnimation(fadeOut);
            this.value.startAnimation(fadeOut);
            this.gyroValue.startAnimation(fadeOut);
            this.magValue.startAnimation(fadeOut);
            this.onOffLegend.startAnimation(fadeIn);
            this.onOff.startAnimation(fadeIn);
            this.periodLegend.startAnimation(fadeIn);
            this.periodBar.startAnimation(fadeIn);
        } else {
            this.sl1.startAnimation(fadeIn);
            this.sl1.startAnimation(fadeIn);
            this.sl2.startAnimation(fadeIn);
            this.sl3.startAnimation(fadeIn);
            this.sl4.startAnimation(fadeIn);
            this.sl5.startAnimation(fadeIn);
            this.sl6.startAnimation(fadeIn);
            this.sl7.startAnimation(fadeIn);
            this.sl8.startAnimation(fadeIn);
            this.sl9.startAnimation(fadeIn);
            this.value.startAnimation(fadeIn);
            this.gyroValue.startAnimation(fadeIn);
            this.magValue.startAnimation(fadeIn);
            this.onOffLegend.startAnimation(fadeOut);
            this.onOff.startAnimation(fadeOut);
            this.periodLegend.startAnimation(fadeOut);
            this.periodBar.startAnimation(fadeOut);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (this.config == true) {
            this.sl1.setVisibility(INVISIBLE);
            this.sl2.setVisibility(INVISIBLE);
            this.sl3.setVisibility(INVISIBLE);
            this.sl4.setVisibility(INVISIBLE);
            this.sl5.setVisibility(INVISIBLE);
            this.sl6.setVisibility(INVISIBLE);
            this.sl7.setVisibility(INVISIBLE);
            this.sl8.setVisibility(INVISIBLE);
            this.sl9.setVisibility(INVISIBLE);
            this.onOff.setVisibility(VISIBLE);
            this.onOffLegend.setVisibility(VISIBLE);
            this.periodBar.setVisibility(VISIBLE);
            this.periodLegend.setVisibility(VISIBLE);
            this.gyroValue.setVisibility(INVISIBLE);
            this.magValue.setVisibility(INVISIBLE);
            this.value.setVisibility(INVISIBLE);
        } else {
            this.sl1.setVisibility(VISIBLE);
            this.sl2.setVisibility(VISIBLE);
            this.sl3.setVisibility(VISIBLE);
            this.sl4.setVisibility(VISIBLE);
            this.sl5.setVisibility(VISIBLE);
            this.sl6.setVisibility(VISIBLE);
            this.sl7.setVisibility(VISIBLE);
            this.sl8.setVisibility(VISIBLE);
            this.sl9.setVisibility(VISIBLE);
            this.gyroValue.setVisibility(VISIBLE);
            this.magValue.setVisibility(VISIBLE);
            this.value.setVisibility(VISIBLE);
            this.onOff.setVisibility(INVISIBLE);
            this.onOffLegend.setVisibility(INVISIBLE);
            this.periodBar.setVisibility(INVISIBLE);
            this.periodLegend.setVisibility(INVISIBLE);
        }
    }
}
