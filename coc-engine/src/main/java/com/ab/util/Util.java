/*  This file is part of Clock-ofclocks project.
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
 * Copyright (C) 2026 Alexander Bootman <ab.clock26@gmail.com>
 *
 * Created: 04 May 2026
 *
 */

package com.ab.util;

import static com.ab.config.Config.PROJECT_NAME;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

public class Util {
    private static Util instance;

    public enum OS {
        linux,
        mac,
        windows,
        unknown
    }

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public OS getOS() {
        OS os = OS.unknown;
        String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (osName.contains("nux")) {
            os = OS.linux;
        } else if ((osName.contains("mac")) || (osName.contains("darwin"))) {
            os = OS.mac;
        } else if ((osName.startsWith("windows"))) {
            os = OS.windows;
        }
        return os;
    }

    public String getDataDirectory() {
        OS os = getOS();
        File file;
        if (os == Util.OS.windows) {
            String userHome = System.getProperty("user.home");
            file = new File(userHome, PROJECT_NAME);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            try {
                file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                file = new File(file.getParent());
                file.mkdirs();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return file.getAbsolutePath();
    }
}