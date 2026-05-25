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
 * Created by Alexander Bootman on 7/27/2023.
 */
package com.ab.coc.gui;

import com.ab.coc.gui.config.CConfig;
import static com.ab.config.Config.ConfigParam;

import javax.swing.*;
import java.awt.*;

public class ColorPicker extends JPanel {
    public static final int COMPONENT_Y_GAP = 5;

    public enum ComponentName {
        red(0), green(1), blue(2);

        private final int value;

        ComponentName(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        Color color() {
            switch (value) {
                case 0:
                    return Color.red;
                case 1:
                    return Color.green;
                case 2:
                    return Color.blue;
            }
            return null;    // should not be here
        }
    }

    private final ConfigParam configParam;
    private final ComponentPicker[] componentPickers = new ComponentPicker[ComponentName.values().length];


    // dimensions for a single componentPicker
    public ColorPicker(Dimension dimension, ConfigParam configParam) {
        this(dimension.width, dimension.height, configParam);
    }

    public ColorPicker(int width, int height, ConfigParam configParam) {
        super();

        this.configParam = configParam;
        this.setLayout(null);

        for (int i = 0; i < componentPickers.length; ++i) {
            componentPickers[i] = new ComponentPicker(width, height, ComponentName.values()[i]);
            this.add(componentPickers[i]);
        }
    }

    int getComponentPosition(ComponentName componentName) {
        if (componentName == null) {
            return 0;
        }
        Color color = CConfig.getInstance().getColorParam(configParam);

        switch (componentName) {
            case red:
                return color.getRed();
            case green:
                return color.getGreen();
            case blue:
                return color.getBlue();
        }
        return -1;
    }

    void setComponentColor(ComponentName componentName, int position) {
        if (componentName == null) {
            return;
        }
        Color color = CConfig.getInstance().getColorParam(configParam);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

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
        color = new Color(red, green, blue, 255);
        CConfig.getInstance().setColorParam(configParam, color);
    }

    @Override
    public void setEnabled(boolean enable) {
        for (Component component : this.getComponents()) {
            component.setEnabled(enable);
        }
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
        Rectangle b = new Rectangle(bounds);
        b.height = (b.height - 2 * COMPONENT_Y_GAP) / componentPickers.length;
        b.x = b.y = 0;
        for (ComponentPicker componentPicker : componentPickers) {
            componentPicker.setBounds(b);
            b.y += b.height + COMPONENT_Y_GAP;
        }
    }

    private class ComponentPicker extends Picker {
        final ComponentName componentName;

        public ComponentPicker(int width, int height, ComponentName componentName) {
            super(width, height, null, componentName.color());
            this.positionLabel.setForeground(Color.white);
            this.componentName = componentName;
            setLayout(null);

            positionLabel.setBackground(componentName.color());
            setPosition(getPosition());
            positionLabel.setText(String.format("%02x ", position));
//            positionLabel.setText(String.format("%3d ", position));
        }

        @Override
        protected void setPosition(int position) {
            super.setPosition(position);
            positionLabel.setText(String.format("%02x ", position));
        }

        @Override
        protected int[] getTriInt(ConfigParam param) {
            int[] res = {0, 0, 255};
            String color = config.getParam(ColorPicker.this.configParam);
            if (color != null) {
                // check for leading '#'?
                if (trackColor.equals(Color.red)) {
                    res[0] = Integer.parseInt(color.substring(1, 3), 16);
                } else if (trackColor.equals(Color.green)) {
                    res[0] = Integer.parseInt(color.substring(3, 5), 16);
                } else if (trackColor.equals(Color.blue)) {
                    res[0] = Integer.parseInt(color.substring(5, 7), 16);
                }
            }
            return res;
        }

        protected int getPosition() {
            this.position = getComponentPosition(componentName);
            return this.position;
        }

        @Override
        protected void setParam() {
            setComponentColor(componentName, this.position);
            if (positionLabel != null) {
                positionLabel.setText(String.format("%02x ", position));
            }
        }
    }
}