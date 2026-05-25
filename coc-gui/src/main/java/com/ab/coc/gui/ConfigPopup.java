/* This file is part of Clock-of-clocks project.
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
 * Created by Alexander Bootman on 8/24/2023.
 *
 */

package com.ab.coc.gui;

import com.ab.coc.gui.config.CConfig;
import com.ab.coc.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.LinkedList;
import java.util.List;

import static com.ab.coc.gui.Main.mainFrame;
import static com.ab.config.Config.ConfigParam;
import static com.ab.config.Config.Theme;

public class ConfigPopup extends JDialog {
    public static final int X_MARGIN = 10;
    public static final int COMPONENT_Y_GAP = ColorPicker.COMPONENT_Y_GAP;
    public static final Color bgColor = Color.LIGHT_GRAY;

    private static final int CUST_FLAG = 0x1;  // horizontal
    private static final int DIAL_FLAG = 0x2;  // horizontal

    public static Rectangle popupRectangle;

    private final int yGap;
    private final Dimension sectionDimension;
    private final int fontSize;
    private final int labelWidth;
    private final int lineHeight;
    protected final Color focusColor;

    transient private final List<JComponent> customThemeControls = new LinkedList<>();
    transient private final List<JComponent> dialControls = new LinkedList<>();
    JComponent themeTime;
    JCheckBox drawDials;

    JButton okButton;

    public final CConfig config;

    public ConfigPopup() {
        super(mainFrame, true);

        config = CConfig.getInstance();
        Rectangle mainRectangle = Main.mainFrame.getBounds();
        popupRectangle = new Rectangle(mainRectangle);
        popupRectangle.width = mainRectangle.width * 2 / 5;
        popupRectangle.x = mainRectangle.x + mainRectangle.width - popupRectangle.width - X_MARGIN;
        Main main = Main.getInstance();
        if (main.isFullScreen) {
            popupRectangle.height -= main.insets.top + main.insets.bottom;
        }
        focusColor = CConfig.focusColor;

        int _yGap = 10;
        if (_yGap > popupRectangle.height / 100) {
            _yGap = popupRectangle.height / 100;
        }
        yGap = _yGap;
        final int nColorPickers = 5;

        lineHeight = (popupRectangle.height
            - 2 * COMPONENT_Y_GAP * nColorPickers
            - yGap * 15)    // number of sections
            / 24;           // number of control lines and labels
        sectionDimension = new Dimension(popupRectangle.width - 2 * X_MARGIN, lineHeight);
        fontSize = lineHeight / 2;
        config.fontSize = fontSize;
        labelWidth = sectionDimension.width / 5;    // todo: calculate

        this.setBounds(popupRectangle);
        this.setLocation(popupRectangle.x, popupRectangle.y);
        this.setResizable(false);
//        this.setTitle(CConfig.PROJECT_NAME + " " + CConfig.VERSION);
        this.setUndecorated(true);
        this.setLayout(new BorderLayout(1, 4));

        // 1. list of settings
        JPanel settings = setConfigPanel();
        add(settings, BorderLayout.CENTER);
        JPanel buttons = setButtonsPanel();
        add(buttons, BorderLayout.SOUTH);

        checkControls();
        config.addPropertyChangeListener(evt -> checkControls());

        this.setVisible(true);   // blocks until dialog ends
    }

    JPanel setButtonsPanel() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());

        okButton = new JButton("OK");
        okButton.setFont(okButton.getFont().deriveFont((float) fontSize));
        okButton.addActionListener(arg0 -> onConfigEnd(true));
        setFocusable(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(cancelButton.getFont().deriveFont((float) fontSize));
        cancelButton.addActionListener(arg0 -> {
            System.out.println("cancel, ignore changes");
            onConfigEnd(false);
        });
        setFocusable(cancelButton);

        buttonPane.add(okButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(cancelButton);
        return buttonPane;
    }

    private void onConfigEnd(boolean save) {
        System.out.println("onConfigEnd, " + save);
        this.dispose();
        if (save) {
            config.serialize();
        } else {
            CConfig.refresh();
            Animator.getInstance().refresh(CConfig.getInstance());  // with new instance
        }
    }

    private void setFocusable(JComponent cb) {
        cb.setFocusable(true);
        cb.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                cb.setBackground(focusColor);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                cb.setBackground(bgColor);
            }
        });
    }

    private JPanel setConfigPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(Color.white);
        settingsPanel.setLayout(null);

        Rectangle bounds = new Rectangle(X_MARGIN, yGap, sectionDimension.width, sectionDimension.height);

        addPicker(bounds, ConfigParam.animTime, 0);
        addLabel(bounds, "Themes:");
        addThemeCheckBoxes(bounds, 0);
        bounds.y += yGap;
        themeTime = addPicker(bounds, ConfigParam.themeTime, 0);

        addLabel(bounds, "Custom Theme:");
        addColorPicker(bounds, ConfigParam.bgColor, CUST_FLAG);
        addColorPicker(bounds, ConfigParam.handsColor, CUST_FLAG);

        drawDials = addCheckBox(bounds, ConfigParam.drawDials, CUST_FLAG);
        addColorPicker(bounds, ConfigParam.dialColor, DIAL_FLAG);
        addPicker(bounds, ConfigParam.borderWidth, DIAL_FLAG);
        addColorPicker(bounds, ConfigParam.borderColor, DIAL_FLAG);

        addPicker(bounds, ConfigParam.shadowWidth, DIAL_FLAG);
        addColorPicker(bounds, ConfigParam.shadowColor, DIAL_FLAG);
        return settingsPanel;
    }

    private void add2group(JComponent widget, int flag) {
        if (flag == CUST_FLAG) {
            customThemeControls.add(widget);
        }
        if (flag == DIAL_FLAG) {
            customThemeControls.add(widget);
            dialControls.add(widget);
        }
    }

    private void addLabel(Rectangle bounds, String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor);
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        label.setFont(font);
        add(label);
        label.setBounds(bounds);
        bounds.y += yGap + bounds.height;
    }

    private JPanel addThemeCheckBoxes(Rectangle bounds, int flag) {
        JPanel section = new JPanel();
        section.setLayout(null);
        Rectangle cbBounds = new Rectangle(0, 0, 0, sectionDimension.height);
        int n = config.themes.length;
        cbBounds.width = (sectionDimension.width - (n - 1) * COMPONENT_Y_GAP) / n;
        for (final Theme theme : config.themes) {
            final JCheckBox cb = new JCheckBox(theme.name);
            cb.setSelected(theme.active);
            cb.addActionListener(e -> {
                config.enableTheme(theme, cb.isSelected());
            });
            setFocusable(cb);

            section.add(cb);
            Font font = new Font("Serif", Font.PLAIN, fontSize);
            cb.setFont(font);
            cb.setBounds(cbBounds);
            cbBounds.x += cbBounds.width + COMPONENT_Y_GAP;
        }
        add(section);
        section.setBounds(bounds);
        add2group(section, flag);
        bounds.y += yGap + bounds.height;
        return section;
    }

    private JPanel addPicker(Rectangle bounds, ConfigParam configParam, int flag) {
        JPanel section = new JPanel();
        section.setLayout(null);

        JLabel pickerLabel = new JLabel(configParam.toLabel() + ":");
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        pickerLabel.setFont(font);
        section.add(pickerLabel);
        Rectangle labelBounds = new Rectangle(0, 0, labelWidth, sectionDimension.height);
        pickerLabel.setBounds(labelBounds);

        Rectangle pickerBounds = new Rectangle(labelWidth, 0, sectionDimension.width - labelWidth, sectionDimension.height);
        Picker picker = new Picker(pickerBounds.width, pickerBounds.height, configParam);
        section.add(picker);
        picker.setBounds(pickerBounds);

        section.setFont(font);
        add(section);
        section.setBounds(bounds);
        add2group(picker, flag);
        bounds.y += yGap + bounds.height;
        return picker;
    }

    // bounds, sectionDimension for a single color picker
    private JPanel addColorPicker(Rectangle bounds, ConfigParam configParam, int flag) {
        JPanel section = new JPanel();
        section.setLayout(null);

        JLabel pickerLabel = new JLabel(configParam.toLabel() + ":");
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        pickerLabel.setFont(font);
        section.add(pickerLabel);
        Rectangle labelBounds = new Rectangle(0, sectionDimension.height + ColorPicker.COMPONENT_Y_GAP,
            labelWidth, sectionDimension.height);
        pickerLabel.setBounds(labelBounds);

        Rectangle pickerBounds = new Rectangle(labelWidth, 0,
            sectionDimension.width - labelWidth,
            3 * sectionDimension.height + 2 * ColorPicker.COMPONENT_Y_GAP);
        Dimension thisDimension = new Dimension(sectionDimension.width - labelWidth,
                sectionDimension.height);
        ColorPicker colorPicker = new ColorPicker(thisDimension, configParam);
        section.add(colorPicker);
        colorPicker.setBounds(pickerBounds);

        add(section);
        Rectangle thisBounds = new Rectangle(bounds);
        thisBounds.height = pickerBounds.height;
        section.setBounds(thisBounds);
        add2group(colorPicker, flag);
        bounds.y += yGap + thisBounds.height;
        return section;
    }

    private JCheckBox addCheckBox(Rectangle bounds, ConfigParam configParam, int flag) {
        JCheckBox section = new JCheckBox(configParam.toLabel());
        section.setSelected(config.getBooleanParam(configParam));
        section.addActionListener(e -> {
            config.setParam(configParam, section.isSelected());
            Animator.getInstance().refresh(config);
        });
        setFocusable(section);
        Font font = new Font("Serif", Font.PLAIN, fontSize);
        section.setFont(font);

        add(section);
        section.setBounds(bounds);
        add2group(section, flag);
        bounds.y += yGap + bounds.height;
        return section;
    }

    private void checkControls() {
        int activeThemes = config.countActiveThemes();
        themeTime.setEnabled(activeThemes > 1);
        okButton.setEnabled(activeThemes > 0);

        boolean customEnabled = config.isCustomThemeEnabled();
        for (JComponent control : customThemeControls) {
            control.setEnabled(customEnabled);
        }

        if (customEnabled) {
            for (JComponent control : dialControls) {
                control.setEnabled(drawDials.isSelected());
            }
        }
        Animator.getInstance().refresh(config);
    }
}