/*  This file is part of DClock project.
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
 * Copyright (C) 2026 Alexander Bootman <ab.jpref@gmail.com>
 *
 * Created: 5/13/26
 *
 */
package com.ab.droid.coc;

import com.ab.coc.Animator;
import com.ab.droid.coc.config.DConfig;
import com.ab.droid.coc.widgets.ColorPicker;
import com.ab.droid.coc.widgets.Picker;

import static com.ab.config.Config.ConfigParam;
import static com.ab.config.Config.Theme;
import com.ab.util.Pair;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import static android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ConfigDialog extends Dialog {
    private static final int V_GAP = 10;
    private static final float LABEL_WEIGHT = 1f;
    private static final float CONTROL_WEIGHT = 3f;

    transient private final MainActivity context;
    transient private final DConfig config;

    final transient private View configView;

    transient private final  int dlgWidth, lineHeight, buttonHeight, fontSize;
    transient private int prevId = 0;

    transient private View themeTime;

    transient private Button okButton;

    private static final int CUST_FLAG = 0x1;
    private static final int DIAL_FLAG = 0x2;

    transient private final List<Pair<View, Integer>> groupedControls = new LinkedList<>();
    transient private final List<List<View>> focusRows = new ArrayList<>();
    transient private final Set<View> pickerFocusViews = new HashSet<>();

    public ConfigDialog(@NonNull MainActivity context) {
        super(context);
        context.configDialog = this;
        this.context = context;
        config = context.config();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);

        int dlgX = 0;
//        int dlgY = 20;
        dlgWidth = context.mainSize.first / 2 - dlgX;
//        dlgHeight = context.mainSize.second - dlgY;
        float ydpi = context.getResources().getDisplayMetrics().ydpi;
        lineHeight = (int)(ydpi / 8);
        config.popupLineHeight = lineHeight;
        buttonHeight = lineHeight * 3 / 2;
        fontSize = lineHeight * 2 / 3;
        config.fontSize = fontSize;

        LinearLayout popupLayout = new LinearLayout(context);
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        popupLayout.setBackgroundColor(Color.WHITE);
        this.setContentView(popupLayout);

        configView = createConfigView();

        ScrollView scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(configView);
        popupLayout.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        View buttonView = createButtonView();
        popupLayout.addView(buttonView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        config.addPropertyChangeListener(evt -> checkControls());

        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(dlgWidth, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(layoutParams);

        setupKeyListeners();
        show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!focusRows.isEmpty() && !focusRows.get(0).isEmpty()) {
            View first = focusRows.get(0).get(0);
            first.post(first::requestFocus);
        }
    }

    private void setupKeyListeners() {
        for (int r = 0; r < focusRows.size(); r++) {
            List<View> rowViews = focusRows.get(r);
            for (int c = 0; c < rowViews.size(); c++) {
                final int row = r, col = c;
                View view = rowViews.get(c);
                final boolean isPicker = pickerFocusViews.contains(view);
                view.setOnKeyListener((v, keyCode, event) -> {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            navigateFocus(row, col, false, false);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            navigateFocus(row, col, true, false);
                            return true;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if (isPicker) {
                                ((Picker) v.getParent()).decrement();

                            } else {
                                navigateFocus(row, col, false, true);
                            }
                            return true;

                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if (isPicker) {
                                ((Picker) v.getParent()).increment();
                            } else {
                                navigateFocus(row, col, true, true);
                            }
                            return true;
                    }
                    return false;
                });
            }
        }
    }

    private void navigateFocus(int row, int col, boolean forward, boolean horizontal) {
        if (horizontal) {
            List<View> rowViews = focusRows.get(row);
            int nextCol = (col + (forward ? 1 : -1) + rowViews.size()) % rowViews.size();
            rowViews.get(nextCol).requestFocus();
        } else {
            int nextRow = forward ? row + 1 : row - 1;
            while (nextRow >= 0 && nextRow < focusRows.size()) {
                List<View> nextRowViews = focusRows.get(nextRow);
                View target = nextRowViews.get(Math.min(col, nextRowViews.size() - 1));
                if (target.isEnabled()) {
                    target.requestFocus();
                    return;
                }
                nextRow += forward ? 1 : -1;
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        context.configDialog = null;
        config.clearPropertyChangeListeners();
    }

    private void add2groups(View widget, int flag) {
        if ((flag & DIAL_FLAG) != 0) {
            flag |= CUST_FLAG;
        }
        groupedControls.add(new Pair<>(widget, flag));
    }

    private LinearLayout getLineLinearLayout() {
        LinearLayout lineLayout = new LinearLayout(context);
        lineLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lineLayout.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams rlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, prevId);
        rlp.setMargins(0, V_GAP, 0, 0);
        lineLayout.setLayoutParams(rlp);
        lineLayout.setNextFocusUpId(prevId);
        lineLayout.setId(++prevId);
        lineLayout.setNextFocusDownId(prevId + 1);
        return lineLayout;
    }

    View createButtonView() {
        LinearLayout buttonLayout = getLineLinearLayout();

        int lineHeight = this.lineHeight * 2;
        LinearLayout.LayoutParams llp;

        llp = new LinearLayout.LayoutParams(0, lineHeight, 1f);
        View dummy = new View(context);
        buttonLayout.addView(dummy, llp);


        llp = new LinearLayout.LayoutParams(0, lineHeight, 1f);
        okButton = createButton(R.string.ok);
        okButton.setOnClickListener(v -> {
            Log.d(MainActivity.DEBUG_TAG, "ConfigDialog, ok");
            config.serialize();
            ConfigDialog.this.dismiss();
        });
        buttonLayout.addView(okButton, llp);

        Button cancelButton = createButton(R.string.cancel);
        cancelButton.setOnClickListener(v -> {
            Log.d(MainActivity.DEBUG_TAG, "ConfigDialog, cancel");
            DConfig.refresh();
            Animator.getInstance().refresh(DConfig.getInstance());
            ConfigDialog.this.dismiss();
        });
        llp = new LinearLayout.LayoutParams(0, lineHeight, 1f);
        buttonLayout.addView(cancelButton, llp);

        List<View> buttonRow = new ArrayList<>();
        buttonRow.add(okButton);
        buttonRow.add(cancelButton);
        focusRows.add(buttonRow);

        return buttonLayout;
    }

    private Button createButton(int text) {
        Button button = new Button(new ContextThemeWrapper(context, android.R.style.Theme_Light));
        button.setLayoutParams(new LinearLayout.LayoutParams(dlgWidth / 5, buttonHeight));
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        button.setText(text);
        button.setOnClickListener(v -> {
            Log.d(MainActivity.DEBUG_TAG, "ConfigDialog, " + text);
            ConfigDialog.this.dismiss();
        });
        button.setFocusable(true);
        setFocusListener(button);
        return button;
    }

    private RelativeLayout createConfigView() {
        RelativeLayout configLayout = new RelativeLayout(context);
        configLayout.setPadding(5, 5, 5, 5);

        addPicker(configLayout, ConfigParam.animTime,0);
        addLabel(configLayout, "Themes:");
        addThemeCheckBoxes(configLayout, 0);
        themeTime = addPicker(configLayout, ConfigParam.themeTime,0);

        addLabel(configLayout, "Custom Theme:");
        addColorPicker(configLayout, ConfigParam.bgColor, CUST_FLAG);
        addColorPicker(configLayout, ConfigParam.handsColor, CUST_FLAG);

        addCheckBox(configLayout, ConfigParam.drawDials, CUST_FLAG);
        addColorPicker(configLayout, ConfigParam.dialColor, DIAL_FLAG);
        addPicker(configLayout, ConfigParam.borderWidth, DIAL_FLAG);
        addColorPicker(configLayout, ConfigParam.borderColor, DIAL_FLAG);

        addPicker(configLayout, ConfigParam.shadowWidth, DIAL_FLAG);
        addColorPicker(configLayout, ConfigParam.shadowColor, DIAL_FLAG);

        return configLayout;
    }

    void setFocusListener(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> context.onFocusColor(v, hasFocus));
    }

    private void addLabel(RelativeLayout configLayout, String text) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        label.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        label.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        label.setText(text);
        LayoutParams rlp = new LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
        rlp.addRule(RelativeLayout.BELOW, prevId);
        label.setId(++prevId);
        configLayout.addView(label, rlp);
    }

    private void addThemeCheckBoxes(RelativeLayout configLayout, int flag) {
        LinearLayout lineLayout = getLineLinearLayout();
        lineLayout.setBackgroundColor(Color.parseColor("#eeeeee"));

        List<View> themeRow = new ArrayList<>();
        // theme checkboxes:
        for (final Theme theme : config.themes) {
            @SuppressLint("AppCompatCustomView") CheckBox cb = new CheckBox(context);
            cb.setFocusable(true);
            setFocusListener(cb);
            cb.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            cb.setText(theme.name);
            cb.setChecked(theme.active);
            cb.setOnClickListener(v -> {
                cb.requestFocus();
                config.enableTheme(theme, cb.isChecked());
            });

            add2groups(cb, flag);
            setFocusListener(cb);
            themeRow.add(cb);
            lineLayout.addView(cb);
        }
        focusRows.add(themeRow);
        configLayout.addView(lineLayout);
    }

    private Picker addPicker(RelativeLayout configLayout, ConfigParam configParam, int flag) {
        LinearLayout lineLayout = getLineLinearLayout();
        TextView attrLabel = new TextView(context);
        attrLabel.setSingleLine();
        attrLabel.setTextColor(Color.BLACK);
        attrLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        attrLabel.setGravity(Gravity.CENTER_VERTICAL);
        attrLabel.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        attrLabel.setText(configParam.toLabel() + ":");
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, lineHeight, LABEL_WEIGHT);
        lineLayout.addView(attrLabel, llp);

        Picker picker = new Picker(context, configParam);
        llp = new LinearLayout.LayoutParams(0, lineHeight, CONTROL_WEIGHT);
        lineLayout.addView(picker, llp);
        configLayout.addView(lineLayout);
        setFocusListener(picker);
        add2groups(picker, flag);
        View pickerFocusView = picker.getFocusView();
        pickerFocusViews.add(pickerFocusView);
        List<View> pickerRow = new ArrayList<>();
        pickerRow.add(pickerFocusView);
        focusRows.add(pickerRow);
        return picker;
    }

    private void addColorPicker(RelativeLayout configLayout, ConfigParam configParam, int flag) {
        LinearLayout lineLayout = getLineLinearLayout();
        TextView attrLabel = new TextView(context);
        attrLabel.setSingleLine();
        attrLabel.setTextColor(Color.BLACK);
        attrLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        attrLabel.setGravity(Gravity.CENTER_VERTICAL);
        attrLabel.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        attrLabel.setText(configParam.toLabel() + ":");
        int h = 3 * lineHeight + 2 * ColorPicker.COMPONENT_Y_GAP;
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0, h, LABEL_WEIGHT);
        lineLayout.addView(attrLabel, llp);

        ColorPicker picker = new ColorPicker(context, configParam);
        llp = new LinearLayout.LayoutParams(0, h, CONTROL_WEIGHT);
        lineLayout.addView(picker, llp);

        configLayout.addView(lineLayout);
        add2groups(picker, flag);
        for (View v : picker.getFocusViews()) {
            pickerFocusViews.add(v);
            List<View> componentRow = new ArrayList<>();
            componentRow.add(v);
            focusRows.add(componentRow);
        }
    }

    private void addCheckBox(RelativeLayout configLayout, ConfigParam configParam, int flag) {
        CheckBox cb = new CheckBox(context);
        cb.setText(configParam.toLabel());
        cb.setChecked(config.getBooleanParam(configParam));
        cb.setOnClickListener(v -> {
            cb.requestFocus();
            config.setParam(configParam, cb.isChecked());
        });
        LayoutParams rlp = new LayoutParams(LayoutParams.MATCH_PARENT, lineHeight);
        rlp.addRule(RelativeLayout.BELOW, prevId);
        cb.setId(++prevId);
        add2groups(cb, flag);
        setFocusListener(cb);
        List<View> cbRow = new ArrayList<>();
        cbRow.add(cb);
        focusRows.add(cbRow);
        configLayout.addView(cb, rlp);
    }

    private void checkControls() {
        int activeThemes = config.countActiveThemes();
        okButton.setEnabled(activeThemes > 0);
        themeTime.setEnabled(activeThemes > 1);

        boolean customEnabled = config.isCustomThemeEnabled();
        boolean drawDials = customEnabled & config.getBooleanParam(ConfigParam.drawDials);
        for (Pair<View, Integer> pair : groupedControls) {
            View view = pair.first;
            int flag = pair.second;
            if ((flag & CUST_FLAG) != 0) {
                view.setEnabled(customEnabled);
            }
            if ((flag & DIAL_FLAG) != 0) {
                view.setEnabled(drawDials);
            }
        }
        Animator.getInstance().refresh(config);
        configView.invalidate();
        configView.requestLayout();
    }
}