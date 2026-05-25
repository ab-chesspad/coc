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
 * Copyright (C) 2026 Alexander Bootman <ab.jpref@gmail.com>
 *
 * Created: 5/13/26
 *
 */
package com.ab.droid.coc.util;

import com.ab.droid.coc.MainActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DUtil {
    private static DUtil instance;

    private final MainActivity context;

    public static DUtil getInstance() {
        return instance;
    }

    public DUtil(MainActivity context) {
        this.context = context;
        instance = this;
    }

    public String getDataDirectory() {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String dataDir = packageInfo.applicationInfo.dataDir;
        return dataDir;
    }
}
