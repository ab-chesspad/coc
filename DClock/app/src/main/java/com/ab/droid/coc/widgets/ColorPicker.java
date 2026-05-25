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

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.ab.droid.coc.MainActivity;
import com.ab.droid.coc.config.DConfig;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends LinearLayout {
    public static final int COMPONENT_Y_GAP = 2;
    public enum ComponentName {
        red(0), green(1), blue(2);

        private final int value;

        ComponentName(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        int color() {
            switch (value) {
                case 0:
                    return Color.RED;
                case 1:
                    return Color.GREEN;
                case 2:
                    return Color.BLUE;
            }
            return 0;    // should not be here
        }
    }

    private final MainActivity context;
    private final DConfig config;
    private final ConfigParam configParam;
    private final ComponentPicker[] componentPickers = new ComponentPicker[ComponentName.values().length];

    public ColorPicker(MainActivity context, ConfigParam configParam) {
        super(context);
        this.context = context;
        this.config = context.config();
        this.configParam = configParam;

        LinearLayout lineLayout = this;
        lineLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lineLayout.setOrientation(LinearLayout.VERTICAL);

        int prevId = 0;
        for (int i = 0; i < componentPickers.length; ++i) {
            componentPickers[i] = new ComponentPicker(ComponentName.values()[i]);
            componentPickers[i].setId(++prevId);
            lineLayout.addView(componentPickers[i]);
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        for (ComponentPicker componentPicker : componentPickers) {
            componentPicker.setFocusable(focusable);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (ComponentPicker componentPicker : componentPickers) {
            componentPicker.setEnabled(enabled);
        }
    }

    void setComponentColor(ComponentName componentName, int position) {
        if (componentName == null) {
            return;
        }
        int color = config.getColorParam(configParam);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        switch (componentName) {
            case red:
                red = position;
                break;
            case green:
                green = position;
                break;
            case blue:
                blue = position;
                break;
        }
        color = 0xff << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
        config.setColorParam(configParam, color);
    }

    int getComponentColor(ComponentName componentName) {
        if (componentName == null) {
            return 0;
        }
        int color = config.getColorParam(configParam);
        int position = 0;
        switch (componentName) {
            case red:
                position = Color.red(color);
                break;
            case green:
                position = Color.green(color);
                break;
            case blue:
                position = Color.blue(color);
                break;
        }
        return position;
    }

    public List<View> getFocusViews() {
        List<View> views = new ArrayList<>(componentPickers.length);
        for (ComponentPicker cp : componentPickers) {
            views.add(cp.getFocusView());
        }
        return views;
    }

    private class ComponentPicker extends Picker {
        final ComponentName componentName;

        private ComponentPicker(ComponentName componentName) {
            super(context, null, componentName.color());
            this.componentName = componentName;
            setPosition(getPosition());
            positionLabel.setText(String.format("%02x", position));
        }

        @Override
        protected boolean onKey(View v, int keyCode, KeyEvent event) {
            boolean res = super.onKey(v, keyCode, event);
            return res;
        }

        @Override
        protected int getPosition() {
            this.position = getComponentColor(componentName);
            return this.position;
        }

        @Override
        protected int getMinPosition() {
            return 0;
        }
        @Override
        protected int getMaxPosition() {
            return 255;
        }

        @Override
        protected void setParam() {
            setComponentColor(componentName, this.position);
            if (positionLabel != null) {
                positionLabel.setText(String.format("%02x", position));
            }
        }
    }
}