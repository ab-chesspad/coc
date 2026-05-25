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
package com.ab.droid.coc.config;

import com.ab.config.Config;
import com.ab.droid.coc.util.DUtil;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Map;

// to reuse com.ab.config.Config we need to override its color properties access code
public class DConfig extends Config implements Serializable {
    public final int bgColor = Color.parseColor("#cccccc");
    public final int focusColor = Color.parseColor("#00aa00");

    public transient int fontSize, popupLineHeight;

    public static DConfig getInstance() {
        if (instance == null) {
            instance = unserialize();
        }
        if (instance == null) {
            instance = new DConfig();

        }
        return (DConfig)instance;
    }

    public static DConfig unserialize() {
        return (DConfig)unserialize(DUtil.getInstance().getDataDirectory());
    }

    public static void refresh() {
        instance = DConfig.unserialize();
    }

    public void serialize() {
        serialize(DUtil.getInstance().getDataDirectory());
    }

    public int getColorParam(ConfigParam param) {
        String color = getParam(param);
        if (color == null) {
            return 0;
        }
        return Color.parseColor(color);
    }

    public int getCurColorParam(ConfigParam param) {
        String color = null;
        Map<ConfigParam, String> currentMap = getCurrentTheme();
        if (currentMap != null) {
            color = getCurrentTheme().get(param);
        }
        if (color == null) {
            return 0;
        }
        return Color.parseColor(color);
    }

    public void setColorParam(ConfigParam configParam, int color) {
        super.setParam(configParam, String.format("#%06x", 0xffffff & color));
    }
}
