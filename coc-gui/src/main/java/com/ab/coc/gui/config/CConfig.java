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
package com.ab.coc.gui.config;

import com.ab.config.Config;
import com.ab.util.Util;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

public class CConfig extends Config implements Serializable {
    public static final Color focusColor = Color.decode("#00ee00");

    public transient int fontSize;

    public static CConfig getInstance() {
        if (instance == null) {
            instance = unserialize();
        }
        if (instance == null) {
            instance = new CConfig();

        }
        return (CConfig)instance;
    }


    public static CConfig unserialize() {
        return (CConfig)unserialize(Util.getInstance().getDataDirectory());
    }

    public static void refresh() {
        instance = CConfig.unserialize();
    }

    public void serialize() {
        serialize(Util.getInstance().getDataDirectory());
    }

    public Color getColorParam(ConfigParam param) {
        String color = getParam(param);
        if (color == null) {
            return new Color(0,0,0,0);
        }
        return Color.decode(color);
    }

    public Color getCurColorParam(ConfigParam param) {
        String color = null;
        Map<ConfigParam, String> currentMap = getCurrentTheme();
        if (currentMap != null) {
            color = getCurrentTheme().get(param);
        }
        if (color == null) {
            return new Color(0,0,0,0);
        }
        return Color.decode(color);
    }

    public void setColorParam(ConfigParam configParam, Color color) {
        setParam(configParam, String.format("#%06X", color.getRGB() & 0xffffff));
    }

}