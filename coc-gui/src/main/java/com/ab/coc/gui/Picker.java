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
package com.ab.coc.gui;

import com.ab.coc.gui.config.CConfig;
import static com.ab.config.Config.ConfigParam;
import static com.ab.coc.gui.config.CConfig.focusColor;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class Picker extends JPanel {
    private static final BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private static final Graphics2D g2d = dummyImage.createGraphics();
    protected final ConfigParam configParam;
    protected final Color bgColor = Color.lightGray;
    protected final int positionLabelWidth;
    protected final int pickerWidth;

    protected final CConfig config = CConfig.getInstance();

    protected final int[] values;

    protected final JSlider slider;
    protected final JLabel positionLabel;

    protected int position;
    protected final Color trackColor;

    // pane to hold positionLabel and slider
    public Picker(Dimension dimension, ConfigParam configParam) {
        this(dimension, configParam, Color.decode("#aaaaaa"));
    }

    public Picker(int width, int height, ConfigParam configParam) {
        this(width, height, configParam, Color.decode("#aaaaaa"));
    }

    public Picker(Dimension dimension, ConfigParam configParam, Color trackColor) {
        this(dimension.width, dimension.height, configParam, trackColor);
    }

    public Picker(int width, int height, ConfigParam configParam, Color trackColor) {
        super();
        this.configParam = configParam;
        this.trackColor = trackColor;
        this.setLayout(null);

        int fontSize = config.fontSize;
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        this.setFont(font);
        g2d.setFont(font);
        positionLabelWidth = g2d.getFontMetrics().stringWidth("0000");
        pickerWidth = width - positionLabelWidth;
        values = getTriInt(configParam);
        position = values[0];

        positionLabel = new JLabel(String.format("%3d ", position));
        positionLabel.setFont(font);
        this.add(positionLabel);
        positionLabel.setOpaque(true);
        positionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        slider = new JSlider(JSlider.HORIZONTAL, values[1], values[2], position);
        this.add(slider);
        slider.setUI(new CustomSliderUI(slider, trackColor));

        slider.addChangeListener(e -> {
            if (slider.getValueIsAdjusting()) {
                return;
            }
            position = slider.getValue();
            setPosition(position);
        });

        MouseListener[] listeners = slider.getMouseListeners();
        for (MouseListener l : listeners) {
            slider.removeMouseListener(l); // remove UI-installed TrackListener
        }
        final BasicSliderUI ui = (BasicSliderUI) slider.getUI();
        BasicSliderUI.TrackListener tl = ui.new TrackListener() {
            // this is where we jump to absolute value of the click
            @Override
            public void mouseClicked(MouseEvent e) {
                if (slider.isEnabled()) {
                    if (slider.getValueIsAdjusting()) {
                        return;
                    }
                    Point p = e.getPoint();
                    int value = ui.valueForXPosition(p.x);
                    if (Main.DEBUG) {
                        System.out.println(String.format("mouseClicked %d", value));
                    }
                    slider.setValue(value);
                }
            }
            // disable check that will invoke scrollDueToClickInTrack
            @Override
            public boolean shouldScroll(int dir) {
                return false;
            }
        };
        slider.addMouseListener(tl);
        slider.setFocusable(true);
        slider.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                slider.setBackground(focusColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                slider.setBackground(bgColor);
            }
        });
        slider.setMinorTickSpacing(1);

        Rectangle bounds = new Rectangle(0, 0, positionLabelWidth, height);
        bounds.width = positionLabelWidth;
        positionLabel.setBounds(bounds);
        bounds.x += bounds.width;
        bounds.width = pickerWidth;
        slider.setBounds(bounds);
    }

    protected void setPosition(int position) {
        int min = values[1];
        int max = values[2];
        if (position < min) {
            position = min;
        }
        if (position > max) {
            position = max;
        }
        this.position = position;
        setParam();
        if (Main.DEBUG) {
            System.out.printf("%s %d\n", configParam, position);
        }
        positionLabel.setText(String.format("%3d ", position));
    }

    @Override
    public void setEnabled(boolean enable) {
        for (Component component : this.getComponents()) {
            component.setEnabled(enable);
        }
    }

    protected int[] getTriInt(ConfigParam param) {
        return config.getTriInt(configParam);
    }

    protected void setParam() {
        // set label
        positionLabel.setText("" + position);
        values[0] = position;
        config.setParam(configParam, position, values[1], values[2]);
    }
}