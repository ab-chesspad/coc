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
import com.ab.droid.coc.config.DConfig;
import com.ab.droid.coc.util.DUtil;
import com.ab.util.Pair;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    public static final boolean DEBUG = false;
    public static final String DEBUG_TAG = "coc.Main";

    public DConfig config() {
        return DConfig.getInstance();
    }

    transient public Pair<Integer, Integer> mainSize;

    transient private Animator animator;
    transient private ClockView clockView;
    transient ConfigDialog configDialog;

    transient private boolean freshStart = true;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setPadding(0,0,0,0);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        this.setContentView(mainLayout, llp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        new DUtil(this);    // need this to be able to use config()
        mainSize = new Pair<>(Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);
        config().mainSize = mainSize;
        clockView = new ClockView(this);
        llp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        mainLayout.addView(clockView, llp);
        freshStart = true;
    }

    // called after onCreate() or onRestart()
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(DEBUG_TAG, "onStart");
        if (!freshStart) {
            return;
        }
        if (animator == null) {
            animator = new Animator(clockView, config());
            animator.refresh(config());
        }
        freshStart = false;
    }

    @Override
    protected void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy");
        if (configDialog != null) {
            // some strange bug on samsung device
            configDialog.dismiss();
        }
        super.onDestroy();
    }


    private void kill() {
        Log.d(DEBUG_TAG, "kill!");
        // no need
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onUserLeaveHint() {
        kill();
        super.onUserLeaveHint();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(MainActivity.DEBUG_TAG, "main, " + event);
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (this.configDialog == null) {
                    new ConfigDialog(this);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onFocusColor(View v, boolean hasFocus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasFocus) {
                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.TRANSPARENT);
                border.setStroke(6, Color.parseColor("#00aa00"));
                v.setForeground(border);
            } else {
                v.setForeground(null);
            }
        } else {
            int bgColor = config().bgColor;
            if (hasFocus) {
                bgColor = config().focusColor;
            }
            v.setBackgroundColor(bgColor);
        }
    }

}