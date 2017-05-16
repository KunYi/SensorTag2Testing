package com.uti.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by kunyi on 2017/5/9.
 */

@SuppressLint("DrawAllocation")
public class SparkLineView extends View {
    private final int numberOfPoints = 15;
    private final Paint pointStrokePaint;
    private final Paint pointFillPaint;
    private final Paint sparkLinePaint;
    public float displayWidth;
    public float maxVal;
    public float minVal;
    public boolean autoScale = false;
    public boolean autoScaleBounceBack = false;
    private ArrayList<Float> dataPoints;

    public SparkLineView(Context context) {
        super(context);
        this.sparkLinePaint = new Paint() {
            {
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeWidth(5.0f);
                setAntiAlias(true);
                setARGB(255, 255, 0, 0);
            }
        };

        this.pointStrokePaint = new Paint() {
            {
                setARGB(255, 255, 255, 255);
                setStyle(Style.FILL_AND_STROKE);
                setAntiAlias(true);
            }
        };

        this.pointFillPaint = new Paint() {
            {
                setARGB(255, 255, 0, 0);
                setStyle(Style.FILL);
                setAntiAlias(true);
            }
        };

        this.dataPoints = new ArrayList<Float>();
        this.maxVal = 1.0f;
        this.minVal = 0.0f;
        int ii = 0;

        for (ii = 0; ii < numberOfPoints; ii++) {
            this.dataPoints.add(Float.valueOf(0.0f));
        }

        this.setWillNotDraw(false);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.displayWidth = 200;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int ii;
        int iterations = numberOfPoints;
        float border = 15;
        float w = this.getWidth();
        float h = this.getHeight();
        float max = 0;
        float min = 0;

        super.onDraw(canvas);
        Path path = new Path();

        if ((this.dataPoints.size() - 1 - numberOfPoints) < 0)
            iterations = this.dataPoints.size() - 1;

        ArrayList<Float> subList = new ArrayList<Float>(this.dataPoints.subList((this.dataPoints.size() - 1) - iterations, this.dataPoints.size() - 1));

        for (ii = 0; ii < iterations; ii++) {
            Float v = subList.get(ii);

            if (v > max) max = v;
            if (v < min) min = v;

            if (this.autoScale) {
                if (v > this.maxVal) {
                    this.maxVal = v + 0.01f;
                    if (this.minVal < -0.001) this.minVal = -v - 0.01f;
                    else this.minVal = 0.0f;
                } else if (v < this.minVal) {
                    this.minVal = v - 0.01f;
                    this.maxVal = -v + 0.01f;
                }
            }
        }

        if (this.autoScaleBounceBack) {

            max = Math.max(max, Math.abs(min));
            this.maxVal = max;

            if (this.minVal < -0.1f)
                this.minVal = -max;
            else
                this.minVal = 0.0f;
        }

        for (ii = 0; ii < iterations; ii++) {
            if (ii == 0) {
                Float v = subList.get(ii);
                path.moveTo(0, h - ((h / (this.maxVal - this.minVal)) * (v - this.minVal)));
                continue;
            } else {
                //Last value
                Float v0 = subList.get(ii - 1);
                //This value
                Float v1 = subList.get(ii);
                //Last Point coordinate
                PointF p0 = new PointF();
                //This Point coordinate
                PointF p1 = new PointF();
                //Midpoint between p0 and p1
                PointF midPoint;
                //Control point
                PointF c1;
                PointF c2;

                p0.x = ((w - (2 * border)) / iterations) * (ii - 1) + border;
                p0.y = h - border - (((h - (2 * border)) / (this.maxVal - this.minVal)) * (v0 - this.minVal));
                p1.x = ((w - (2 * border)) / iterations) * (ii) + border;
                p1.y = h - border - (((h - (2 * border)) / (this.maxVal - this.minVal)) * (v1 - this.minVal));
                midPoint = this.midPointForPoints(p0, p1);
                c1 = this.controlPointForPoints(midPoint, p0);
                path.quadTo(c1.x, c1.y, midPoint.x, midPoint.y);
                c2 = this.controlPointForPoints(midPoint, p1);
                path.quadTo(c2.x, c2.y, p1.x, p1.y);
            }
        }

        canvas.drawPath(path, this.sparkLinePaint);
        for (ii = 0; ii < iterations; ii++) {
            Float v = subList.get(ii);
            Float x, y;

            x = ((w - (2 * border)) / iterations) * ii + border;
            y = h - border - (((h - (2 * border)) / (this.maxVal - this.minVal)) * (v - this.minVal));
            canvas.drawCircle(x, y, 10, this.pointStrokePaint);
            canvas.drawCircle(x, y, 7, this.pointFillPaint);
        }
    }

    PointF controlPointForPoints(PointF p1, PointF p2) {
        PointF controlPoint = midPointForPoints(p1, p2);
        Float diffY = (float) Math.abs(p2.y - controlPoint.y);

        if (p1.y < p2.y)
            controlPoint.y += diffY;
        else if (p1.y > p2.y)
            controlPoint.y -= diffY;

        return controlPoint;
    }

    PointF midPointForPoints(PointF p1, PointF p2) {
        PointF tmp = new PointF();
        tmp.x = (p1.x + p2.x) / 2;
        tmp.y = (p1.y + p2.y) / 2;
        return tmp;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padding = 40;
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec) - padding;
        if (h < 200) h = 200;
        if (w < this.displayWidth) w = (int) this.displayWidth;
        setMeasuredDimension(w, h);
    }

    public void addValue(float value) {
        Float val = Float.valueOf(value);
        this.dataPoints.add(val);
        this.invalidate();
    }

    public void setColor(int a, int r, int g, int b) {
        this.pointFillPaint.setARGB(a, r, g, b);
        this.sparkLinePaint.setARGB(a, r, g, b);
    }
}
