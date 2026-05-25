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
package com.ab.droid.coc;

import com.ab.coc.Animator;
import com.ab.coc.Dial;
import com.ab.droid.coc.config.DConfig;

import static com.ab.config.Config.ConfigParam;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ClockView extends View implements Animator.Observer {
    private final MainActivity context;
    private final int screenWidth, screenHeight;
    private Dial[] dials;

    DConfig config() {
        return context.config();
    }

    @SuppressLint("ClickableViewAccessibility")
    public ClockView(MainActivity context) {
        super(context);
        this.context = context;
        screenWidth = context.config().mainSize.first;
        screenHeight = context.config().mainSize.second;
        this.setOnKeyListener((v, keyCode, event) -> {
            Log.d(MainActivity.DEBUG_TAG, "clock view, " + event);
            return false;
        });

        this.setOnTouchListener((v, event) -> {
            Log.d(MainActivity.DEBUG_TAG, "clock view, " + event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (context.configDialog == null) {
                    new ConfigDialog(context);
                }
                return true;
            }
            return false;
        });

    }

    @Override
    public void update(Dial[] dials) {
        this.dials = dials;
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        // background
        canvas.drawColor(config().getCurColorParam(ConfigParam.bgColor));
        if (dials == null) {
            return;
        }

        for (Dial dial : dials) {
            paintDial(canvas, dial);
        }
    }

    private void paintDial(Canvas canvas, Dial dial) {
        int xSize = Animator.xSize;
        int ySize = Animator.ySize;
        int width = screenWidth;
        int height = screenHeight;
        boolean vertical = false;
        if (screenHeight > screenWidth) {
            vertical = true;
            xSize = Animator.ySize;
            ySize = Animator.xSize;
        }
        int dialRadius, xMargin, yMargin;
        int rX = width / xSize / 2;
        int rY = height / ySize / 2;
        dialRadius = Math.min(rX, rY);

        int wPanel = dialRadius * 2 * xSize;
        xMargin = (width - wPanel) / 2;
        int hPanel = dialRadius * 2 * ySize;
        yMargin = (height - hPanel) / 2;

        int col = dial.getCol(), row = dial.getRow();
        if (vertical) {
            row = dial.getCol();
            col = dial.getRow();
        }

        int xOff, yOff;
        xOff = xMargin + dialRadius + col * dialRadius * 2;
        yOff = yMargin + dialRadius + row * dialRadius * 2;

        float innerRad = dialRadius;
        double dx, dy;

        // border
        Paint paint = new Paint();
        if (config().getBooleanParam(ConfigParam.drawDials)) {
            // outer circle, border
            paint.setColor(getColorParam(ConfigParam.borderColor, dial.getDialAlpha()));
            canvas.drawCircle(xOff, yOff, dialRadius, paint);

            // shadow
            int border = config().getIntParam(ConfigParam.borderWidth);
            paint.setColor(getColorParam(ConfigParam.shadowColor, dial.getDialAlpha()));
            canvas.drawCircle(xOff, yOff, dialRadius - 2 * border, paint);

            // inner circle
            int shadow = config().getIntParam(ConfigParam.shadowWidth);
            innerRad = dialRadius - 2 * border - 2 * shadow;
            if (innerRad < 1) {
                innerRad = 1;
            }
            double angle = Math.toRadians(45);
            if (row != 0 || col != 0) {
                angle = Math.atan2(row, col);
            }

            // inner circle must be tangential to shadow circle
            double deltaRad = 2 * shadow;
            dx = deltaRad * Math.cos(angle);
            dy = deltaRad * Math.sin(angle);
            xOff += (int)dx;
            yOff += (int)dy;
            paint.setColor(getColorParam(ConfigParam.dialColor, dial.getDialAlpha()));
            canvas.drawCircle(xOff, yOff, innerRad, paint);
        }

        // hands
        paint.setColor(getColorParam(ConfigParam.handsColor, dial.getHandsAlpha()));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth((float)dialRadius / 6);

        double hFactor = 0.8;
        double mFactor = 1.0;

        int h = dial.getHours();
        double rad = Math.toRadians(h);
        dx = innerRad * hFactor * Math.cos(rad);
        dy = innerRad * hFactor * Math.sin(rad);
        canvas.drawLine(xOff, yOff, (float) (xOff + dx), (float) (yOff + dy), paint);

        int m = dial.getMinutes();
        rad = Math.toRadians(m);
        dx = innerRad * mFactor * Math.cos(rad);
        dy = innerRad * mFactor * Math.sin(rad);
        canvas.drawLine(xOff, yOff, (float) (xOff + dx), (float) (yOff + dy), paint);
    }

    private int getColorParam(ConfigParam configParam, int alpha) {
        int color = config().getCurColorParam(configParam);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if (Color.alpha(color) == 0) {
            alpha = 0;
        }
        color = Color.argb(alpha, red, green, blue);
        return  color;
    }

}
