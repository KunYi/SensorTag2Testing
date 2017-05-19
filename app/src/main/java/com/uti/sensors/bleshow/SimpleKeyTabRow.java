package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.uti.Utils.GenericTabRow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kunyi on 2017/5/17.
 */

public class SimpleKeyTabRow extends GenericTabRow {
    protected int lastKeys;
    protected AppCompatImageView leftKeyPressState;
    protected AppCompatImageView rightKeyPressState;
    protected AppCompatImageView reedState;
    protected updateSparkLinesTimerTask sparkLineUpdateTask;
    protected Timer sparkLineUpdateTimer;

    public SimpleKeyTabRow(Context con) {
        super(con);
        this.periodBar.setEnabled(false);
        this.periodLegend.setText("Sensor period (\"Notification\")");
        this.sl1.maxVal = 1.0f;
        this.sl1.setColor(255, 255, 0, 0);
        this.sl2.maxVal = 1.0f;
        this.sl2.setColor(255, 17, 136, 153);
        this.sl2.setVisibility(VISIBLE);
        this.sl3.maxVal = 1.0f;
        this.sl3.setColor(255, 0, 0, 0);
        this.sl3.setVisibility(VISIBLE);
        this.sl2.setEnabled(true);
        this.sl3.setEnabled(true);
        this.value.setVisibility(INVISIBLE);
        final int nextGuiId = this.sl3.getId();
        this.leftKeyPressState = new AppCompatImageView(con) {
            {
                setId(nextGuiId + 1);
            }
        };
        this.leftKeyPressState.setImageResource(R.drawable.leftkeyoff_300);
        this.rightKeyPressState = new AppCompatImageView(con) {
            {
                setId(nextGuiId + 2);
            }
        };
        this.rightKeyPressState.setImageResource(R.drawable.rightkeyoff_300);
        this.reedState = new AppCompatImageView(con) {
            {
                setId(nextGuiId + 3);
            }
        };
        this.reedState.setImageResource(R.drawable.reedrelayoff_300);


        //Setup layout for all cell elements
        RelativeLayout.LayoutParams iconItemParams = new RelativeLayout.LayoutParams(
                210,
                180) {
            {
                addRule(RelativeLayout.RIGHT_OF,
                        icon.getId());
                addRule(RelativeLayout.BELOW, title.getId());
            }

        };
        leftKeyPressState.setLayoutParams(iconItemParams);
        leftKeyPressState.setPadding(20, 20, 20, 20);

        iconItemParams = new RelativeLayout.LayoutParams(
                160,
                160) {
            {
                addRule(RelativeLayout.RIGHT_OF,
                        leftKeyPressState.getId());
                addRule(RelativeLayout.BELOW, title.getId());
            }

        };
        rightKeyPressState.setPadding(10, 10, 10, 10);
        rightKeyPressState.setLayoutParams(iconItemParams);
        iconItemParams = new RelativeLayout.LayoutParams(
                160,
                160) {
            {
                addRule(RelativeLayout.RIGHT_OF,
                        rightKeyPressState.getId());
                addRule(RelativeLayout.BELOW, title.getId());
            }

        };
        reedState.setLayoutParams(iconItemParams);
        reedState.setPadding(10, 10, 10, 10);


        //Move sparkLines below the state images

        iconItemParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT) {
            {
                addRule(RelativeLayout.RIGHT_OF,
                        icon.getId());
                addRule(RelativeLayout.BELOW, reedState.getId());
            }

        };

        this.sl1.setLayoutParams(iconItemParams);
        this.sl2.setLayoutParams(iconItemParams);
        this.sl3.setLayoutParams(iconItemParams);

        this.rowLayout.addView(leftKeyPressState);
        this.rowLayout.addView(rightKeyPressState);
        this.rowLayout.addView(reedState);
        this.sparkLineUpdateTimer = new Timer();
        this.sparkLineUpdateTask = new updateSparkLinesTimerTask(this);
        this.sparkLineUpdateTimer.scheduleAtFixedRate(this.sparkLineUpdateTask, 1000, 100);
    }

    @Override
    public void onClick(View v) {
        if (disabledClick)
            return;

        super.onClick(v);
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setAnimationListener(this);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(0);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setAnimationListener(this);
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(250);
        if (this.config == true) {
            this.leftKeyPressState.startAnimation(fadeOut);
            this.rightKeyPressState.startAnimation(fadeOut);
            this.reedState.startAnimation(fadeOut);
        } else {
            this.leftKeyPressState.startAnimation(fadeIn);
            this.rightKeyPressState.startAnimation(fadeIn);
            this.reedState.startAnimation(fadeIn);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        super.onAnimationEnd(animation);
        if (this.config == true) {
            this.leftKeyPressState.setVisibility(View.INVISIBLE);
            this.rightKeyPressState.setVisibility(View.INVISIBLE);
            this.reedState.setVisibility(View.INVISIBLE);

        } else {
            this.leftKeyPressState.setVisibility(View.VISIBLE);
            this.rightKeyPressState.setVisibility(View.VISIBLE);
            this.reedState.setVisibility(View.VISIBLE);
        }
    }

    class updateSparkLinesTimerTask extends TimerTask {
        SimpleKeyTabRow param;

        public updateSparkLinesTimerTask(SimpleKeyTabRow param) {
            this.param = param;
        }

        @Override
        public void run() {
            this.param.post(new Runnable() {
                @Override
                public void run() {

                    if ((param.lastKeys & 0x1) == 0x1) {
                        param.sl1.addValue(1);
                    } else param.sl1.addValue(0);
                    if ((param.lastKeys & 0x2) == 0x2) {
                        param.sl2.addValue(1);
                    } else param.sl2.addValue(0);
                    if ((param.lastKeys & 0x4) == 0x4) {
                        param.sl3.addValue(1);
                    } else param.sl3.addValue(0);
                }
            });
        }
    }

    @Override
    public void grayedOut(boolean gray) {
        super.grayedOut(gray);
        if (gray) {
            this.leftKeyPressState.setAlpha(0.4f);
            this.rightKeyPressState.setAlpha(0.4f);
            this.reedState.setAlpha(0.4f);
            this.sl3.setAlpha(0.2f);
        } else {
            this.leftKeyPressState.setAlpha(1.0f);
            this.rightKeyPressState.setAlpha(1.0f);
            this.reedState.setAlpha(1.0f);
            this.sl3.setAlpha(1.0f);
        }
    }

}
