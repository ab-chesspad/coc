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
package com.ab.config;

import com.ab.util.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;

public class Config implements Serializable {
    public static final String PROJECT_NAME = "coc";
    public static final String VERSION = "1.0";
    private static final String CONFIG_FILENAME = PROJECT_NAME + ".config";

    public transient Pair<Integer, Integer> mainSize;

    public enum ConfigParam {
        animTime("50,10,100"),
        themeTime("5,1,720"),

        // custom theme parameters:
        bgColor("#102057"),
        handsColor("#0099ff"),       // gold
        drawDials("yes"),
        borderWidth("2,0,20"),
        shadowWidth("1,0,20"),
        borderColor("#777777"),
        shadowColor("#888855"),
        dialColor("#0000c4"),
        ;

        private final String value;

        ConfigParam(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toLabel() {
            String name = toString();
            if (Character.isUpperCase(name.charAt(1))) {
                return name;
            }
            name = name.replaceAll("([A-Z])", " $1");
            name = name.substring(0,1).toUpperCase() + name.substring(1);
            return name;
        }
    }

    public final Theme[] themes = new Theme[5];

    private int currentTheme = 0;

    public final Map<ConfigParam, String> properties = new HashMap<>();
    private final PropertyChangeSupport changes;

    protected static Config instance;

    public static Config unserialize(String dir) {
        Config object = null;
        try (FileInputStream fis = new FileInputStream(new File(dir, CONFIG_FILENAME));
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            object = (Config)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if (object != null) {
            restoreTransient(object);
            object.firePropertyChange();
        }
        return object;
    }

    private static void restoreTransient(Config newConfig) {
        if (instance == null) {
            return;
        }
        newConfig.mainSize = instance.mainSize;
    }

    private void firePropertyChange() {
        changes.firePropertyChange("config reload", "old", "new");
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void clearPropertyChangeListeners() {
        PropertyChangeListener[] listeners = changes.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            changes.removePropertyChangeListener(listener);
        }
    }

    public void serialize(String dir) {
        try (FileOutputStream fos = new FileOutputStream(new File(dir, CONFIG_FILENAME));
                ObjectOutputStream oot = new ObjectOutputStream(fos) ) {
            oot.writeObject(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Config() {
        changes = new PropertyChangeSupport(properties);
        for (ConfigParam p : ConfigParam.values()) {
            properties.put(p, p.value);
        }
        init();
        firePropertyChange();
    }

    public boolean isCustomThemeEnabled() {
        return themes[themes.length - 1].active;
    }

    public int countActiveThemes() {
        int count = 0;
        for (Theme theme : themes) {
            if (theme.active) {
                ++count;
            }
        }
        return count;
    }

    public void toNextTheme() {
        int index = currentTheme;
        for (int i = 0; i < themes.length; ++i) {
            ++index;
            if (index >= themes.length) {
                index = 0;
            }
            Theme theme = themes[index];
            if (theme.active) {
                currentTheme = index;
                return;
            }
        }
        // when all themes are disabled in ConfigPopup, don't update currentTheme
    }

    protected Map<ConfigParam, String> getCurrentTheme() {
        if (currentTheme < 0) {
            return null;
        }
        return themes[currentTheme].properties;
    }

    // to get color as text
    public String getParam(ConfigParam configParam) {
        String s = properties.get(configParam);
        return s;
    }

    public int[] getTriInt(ConfigParam param) {
        int[] res = new int[3];
        String s = properties.get(param);
        if (s != null) {
            String[] parts = s.split(",");
            res[0] = Integer.parseInt(parts[0]);
            res[1] = Integer.parseInt(parts[1]);
            res[2] = Integer.parseInt(parts[2]);
        }
        return res;
    }

    public int getIntParam(ConfigParam param) {
        String s = properties.get(param);
        if (s == null) {
            return 0;
        }
        String[] parts = s.split(",");
        return Integer.valueOf(parts[0]);
    }

    public int getCurIntParam(ConfigParam param) {
        String s = null;
        Map<ConfigParam, String> currentMap = getCurrentTheme();
        if (currentMap != null) {
            s = getCurrentTheme().get(param);
        }
        if (s == null) {
            s = properties.get(param);
        }
        if (s == null) {
            return 0;
        }
        String[] parts = s.split(",");
        return Integer.valueOf(parts[0]);
    }

    public int getCurIntParamMin(ConfigParam param) {
        String s = getCurrentTheme().get(param);
        if (s == null) {
            return 0;
        }
        String[] parts = s.split(",");
        if (parts.length > 1) {
            return Integer.valueOf(parts[1]);
        }
        return 0;
    }

    public int getCurIntParamMax(ConfigParam param) {
        String s = getCurrentTheme().get(param);
        if (s == null) {
            return 0;
        }
        String[] parts = s.split(",");
        if (parts.length > 2) {
            return Integer.valueOf(parts[2]);
        }
        return 100;
    }

    public boolean getCurBooleanParam(ConfigParam param) {
        String s = getCurrentTheme().get(param);
        if (s == null) {
            return false;
        }
        return s.equalsIgnoreCase("yes");
    }

    public boolean getBooleanParam(ConfigParam param) {
        String s = properties.get(param);
        if (s == null) {
            return false;
        }
        return s.equalsIgnoreCase("yes");
    }

    public void setParam(ConfigParam configParam, String text) {
//        String s = getCurrentTheme().get(configParam);

        if (configParam == ConfigParam.handsColor &&
                text.split(",").length > 1) {
            System.out.printf("error %s -> %s\n", configParam, text);
        }
        properties.put(configParam, text);
        Theme custom = themes[themes.length - 1];
        if (custom.get(configParam) != null) {
            custom.put(configParam, text);
        }
        firePropertyChange();
    }

    public void setParam(ConfigParam configParam, int[] values) {
        setParam(configParam, values[0], values[1], values[2]);
    }

    public void setParam(ConfigParam configParam, int v, int min, int max) {
        @SuppressWarnings("DefaultLocale") String newVal = String.format("%d,%d,%d", v, min, max);
        setParam(configParam, newVal);
    }

    public void setParam(ConfigParam configParam, boolean val) {
        String newVal = val ? "yes" : "no";
        setParam(configParam, newVal);
    }

    public void enableTheme(Theme theme, boolean enable) {
        theme.active = enable;
        firePropertyChange();
    }

    private void init() {
        // set themes
        Theme theme;

        int index = -1;
        // fixed themes: hands, bg, dials

        theme = new Theme("BL");    // black hands, light bg, no dials
        theme.put(ConfigParam.bgColor,"#dddddd");
        theme.put(ConfigParam.handsColor,"#000000");
        themes[++index] = theme;

        theme = new Theme("BLL");   // black hands, light bg, light dials
        theme.put(ConfigParam.bgColor, "#dddddd");
        theme.put(ConfigParam.handsColor, "#000000");
        theme.put(ConfigParam.drawDials, "yes");
        theme.put(ConfigParam.borderWidth, "1");
        theme.put(ConfigParam.borderColor, "#444444");
        theme.put(ConfigParam.shadowWidth, "1");
        theme.put(ConfigParam.shadowColor, "#888888");
        theme.put(ConfigParam.dialColor,"#cccccc");
        themes[++index] = theme;

        theme = new Theme("GD");    // gold hands, dark bg, no dials
        theme.put(ConfigParam.bgColor, "#444444");
        theme.put(ConfigParam.handsColor, "#ffd700");
        theme.put(ConfigParam.drawDials, "no");
        themes[++index] = theme;

        theme = new Theme("WDD");   // white hands, dark bg, dark dials
        theme.put(ConfigParam.bgColor, "#444444");
        theme.put(ConfigParam.handsColor, "#ffffff");
        theme.put(ConfigParam.drawDials, "yes");
        theme.put(ConfigParam.borderWidth, "5");
        theme.put(ConfigParam.borderColor, "#888888");
        theme.put(ConfigParam.shadowWidth, "2");
        theme.put(ConfigParam.shadowColor, "#222222");
        theme.put(ConfigParam.dialColor, "#444444");
        themes[++index] = theme;

        // custom
        theme = new Theme("custom");  // white hands, dark bg, dark dials
        for (int i = 2; i < ConfigParam.values().length; ++i) {
            ConfigParam configParam = ConfigParam.values()[i];
            theme.put(configParam, configParam.value);
        }
        themes[++index] = theme;
    }
    public static class Theme implements Serializable {
        public final String name;
        public final Map<ConfigParam, String> properties = new HashMap<>();
        public boolean active;

        public Theme(String name) {
            this.name = name;
            active = true;
        }

        public void put(ConfigParam configParam, String value) {
            properties.put(configParam, value);
        }

        public String get(ConfigParam configParam) {
            return properties.get(configParam);
        }
    }
}