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
 * Created by Alexander Bootman on 6/24/2024.
 */
package com.ab.coc;

import com.ab.util.Point;

import java.util.HashMap;
import java.util.Map;

public class Cup {
    public static final int
            WIDTH = 8,
            HEIGHT = 6,
            dummy_int = 0;

    private final Map<Point, Point> map = new HashMap<>();

    private void add(int x, int y, int hours, int minutes){
        map.put(new Point(x, y), new Point(hours, minutes));
    }

    public Point getHands(int x, int y) {
        Point p = map.get(new Point(x, y));
        if (p == null) {
            p = new Point(Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        }
        return p;
    }

    public Cup() {

        add(2, 0, 125, 235);
        add(2, 1, 125, 235);

        add(3, 0, 110, 235);
        add(3, 1, 110, 235);

        add(4, 0, 125, 235);
        add(4, 1, 125, 235);

        add(0, 2, 90, 0);
        add(0, 3, 45, 270);
        add(1, 4, 45, 225);
        add(2, 5, 0, 225);
        add(3, 5, 0, 180);
        add(4, 5, 315, 180);
        add(5, 4, 315, 135);
        add(6, 3, 135, 270);
        add(6, 2, 90, 180);

        add(1, 2, 180, 0);
        add(2, 2, 180, 0);
        add(3, 2, 180, 0);
        add(4, 2, 180, 0);
        add(5, 2, 180, 0);

        add(6, 4, 0, 170);
        add(7, 4, 180, 300);
        add(7, 3, 60, 180);

    }
}
