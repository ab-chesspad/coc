/*  This file is part of Clock-of-clocks project.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 *
 * Copyright (C) 2023-2026 Alexander Bootman <ab.clock26@gmail.com>
 *
 * Created by Alexander Bootman on 8/27/2023.
 */
package com.ab.droid.coc.widgets;

import static com.ab.config.Config.ConfigParam;
import com.ab.droid.coc.MainActivity;
import com.ab.droid.coc.config.DConfig;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;

public class Picker extends LinearLayout {
    public static final String DEBUG_TAG = MainActivity.DEBUG_TAG;
    private final static int MAX_STROKES = 10; // how many times user has to click > or < from min to max
    private final static int xMargin = 5;
    private final int DEFAULT_TRACK_COLOR = Color.parseColor("#000000");

    private final int trackColor;

    protected final DConfig config;
    protected final ConfigParam configParam;

    private int pickerLineHeight;

    protected TextView positionLabel;
    protected View pickerView;
    protected int position;
    protected int advanceValue;

    public Picker(MainActivity context, ConfigParam configParam) {
        super(context);
        this.config = context.config();
        this.configParam = configParam;
        this.trackColor = DEFAULT_TRACK_COLOR;
        init(context);
    }

    public Picker(MainActivity context, ConfigParam configParam, int trackColor) {
        super(context);
        this.config = context.config();
        this.configParam = configParam;
        this.trackColor = trackColor;
        init(context);
    }

    private void init(MainActivity context) {
        pickerLineHeight = config.popupLineHeight - 2;

        advanceValue = (getMaxPosition() - getMinPosition()) / MAX_STROKES;
        this.clearFocus();

        positionLabel = new TextView(context);
        pickerView = new PickerView(context);
        drawControls();
        setPosition(getPosition());

    }

    protected int getBgColor() {
        return config.bgColor;
    }

    private void drawControls() {
        this.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);

        positionLabel.setSingleLine();
        positionLabel.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        positionLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.fontSize);
        positionLabel.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        positionLabel.setGravity(Gravity.CENTER_VERTICAL);
        positionLabel.setBackgroundColor(getBgColor());
        LinearLayout.LayoutParams positionLlp = new LinearLayout.LayoutParams(
                0, pickerLineHeight, 1f);
        this.addView(positionLabel, positionLlp);

        positionLlp = new LinearLayout.LayoutParams(0, pickerLineHeight, 4f);
        this.addView(pickerView, positionLlp);
    }

    void setPosition(int position) {
        int min = getMinPosition();
        int max = getMaxPosition();
        if (position < min) {
            position = min;
        }
        if (position > max) {
            position = max;
        }
        if (this.position == position) {
            return;
        }
        this.position = position;
        setParam();
        invalidate();
    }

    protected int getPosition() {
        int[] values = config.getTriInt(configParam);
        return values[0];
    }

    protected int getMinPosition() {
        int[] values = config.getTriInt(configParam);
        return values[1];
    }

    protected int getMaxPosition() {
        int[] values = config.getTriInt(configParam);
        return values[2];
    }

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        for (int i = 0; i < this.getChildCount(); ++i) {
            View child = this.getChildAt(i);
            child.setEnabled(enable);
        }
    }

    protected void setParam() {
        pickerView.requestFocus();
        int min = getMinPosition();
        int max = getMaxPosition();
        // set label
        positionLabel.setText("" + position + " ");
        config.setParam(configParam, position, min, max);
    }

    public void increment() {
        setPosition(position + advanceValue);
    }

    public void decrement() {
        setPosition(position - advanceValue);
    }

    public void invalidate() {
        super.invalidate();
        if (pickerView != null) pickerView.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(DEBUG_TAG, Picker.this + ", onDraw");
        if (pickerView != null) pickerView.invalidate();
    }

    public View getFocusView() {
        return pickerView;
    }

    protected boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d(DEBUG_TAG, Picker.this + ", " + event.toString());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KEYCODE_DPAD_LEFT:
                    decrement();
                    return true;
                case KEYCODE_DPAD_RIGHT:
                    increment();
                    return true;
            }
        }
        return false;
    }

    public class PickerView extends View {
        final Paint paint = new Paint();

        @SuppressLint("ClickableViewAccessibility")
        public PickerView(final MainActivity context) {
            super(context);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            this.clearFocus();
            this.setBackgroundColor(getBgColor());
            this.setOnTouchListener((v, event) -> {
                Log.d(DEBUG_TAG, Picker.this + ", " + event.toString());
                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        PickerView.this.requestFocus();
                    }
                    int min = getMinPosition();
                    int max = getMaxPosition();
                    float x = event.getX();
                    int w = pickerView.getWidth();
                    float newPosition = min + (x - xMargin) * (max - min) / (w - 2 * xMargin);
                    setPosition((int)newPosition);
                    return true;
                }
                return false;
            });
            this.setOnKeyListener(Picker.this::onKey);
            this.setOnFocusChangeListener((v, hasFocus) -> {
                context.onFocusColor(v, hasFocus);
            });
        }

        @Override
        public void onDraw(Canvas canvas) {
            int min = getMinPosition();
            int max = getMaxPosition();
            int position = getPosition();

            float width = this.getWidth();
            float height = this.getHeight();
            float barWidth = height / 2;
            if (barWidth < 1) {
                barWidth = 1;
            }

            paint.setColor(trackColor);
            float x0 = xMargin;
            float x1 = x0 + (width - 2 * xMargin) * (position - min) / (max - min);
            if (x0 == x1 && min > 0) {
                x1 = x0 + 1;
            }
            float y0 = height / 2;
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(barWidth);
            canvas.drawLine(x0, y0, x1, y0, paint);
        }
    }

    @Override
    public String toString() {
        String s;
        if (configParam != null) {
            s = "Picker-" + configParam.name();
        } else {
            String name;
            int trackColor = this.trackColor | 0xff000000;
            switch (trackColor) {
                case Color.RED:
                    name = "red";
                    break;
                case Color.GREEN:
                    name = "green";
                    break;
                case Color.BLUE:
                    name = "blue";
                    break;
                default:
                    name = String.format("%06x", trackColor & 0x0ffffff);
                    break;
            }
            s = "Picker-" + name;
        }
        return s;
    }
}