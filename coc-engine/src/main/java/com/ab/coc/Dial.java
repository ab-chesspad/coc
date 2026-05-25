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
package com.ab.coc;

import com.ab.util.Couple;
import com.ab.util.Point;

public class Dial {
    public static final int DEBUG_COL = -1;
    public static final int DEBUG_ROW = 4;
    public static final int HIDE_ANGLE = 135;

    private final Point coord;  // column, row
    private final Animator.Quadrant quadrant;
    private int symbolOffset;
    private int symbolIndex;
    private final Couple<Point> time = new Couple<>(new Point(0, 0), new Point(0, 0));    // hours, minutes
    private int handsAlpha = 255;   // 0 - invisible, transparent; 255 - visible, opaque
    private int dialAlpha = 0;

    public Dial(int col, int row, Animator.Quadrant quadrant) {
        this.coord = new Point(col, row);
        this.quadrant = quadrant;
    }

    public boolean isErasableDial() {
        return time.target.first.equals(HIDE_ANGLE) && time.target.second.equals(HIDE_ANGLE);
    }

    public int getHours() {
        return time.current.first;
    }

    public int getMinutes() {
        return time.current.second;
    }

    public Animator.Quadrant getQuadrant() {
        return quadrant;
    }

    public void setGoal(int hours, int minutes) {
        time.target.first = mod(hours, 360);
        time.target.second = mod(minutes, 360);
    }

    public int getCol() {
        return coord.first;
    }

    public int getRow() {
        return coord.second;
    }

    public int getSymbolOffset() {
        return symbolOffset;
    }

    public void setSymbolOffset(int symbolOffset) {
        this.symbolOffset = symbolOffset;
    }

    public int getSymbolIndex() {
        return symbolIndex;
    }

    public void setSymbolIndex(int symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    public int getHandsAlpha() {
        return handsAlpha;
    }

    public int getDialAlpha() {
        return dialAlpha;
    }

    private int _updateAlpha(int alpha, int delta) {
        alpha += delta;
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 255) {
            alpha = 255;
        }
        return alpha;
    }

    public void updateAlpha(int handsUpdate, int dialUpdate) {
        handsAlpha = _updateAlpha(handsAlpha, handsUpdate);
        dialAlpha = _updateAlpha(dialAlpha, dialUpdate);
        if (DEBUG_COL >= 0) {
            if (coord.first == DEBUG_COL && coord.second == DEBUG_ROW) {
                System.out.printf("%dx%d, cur=%dx%d, goal=%dx%d, hands=%d, dials=%d\n",
                    coord.first, coord.second, time.current.first, time.current.second,
                        time.target.first, time.target.second, handsAlpha, dialAlpha);
            }
        }
    }

    @SuppressWarnings("DefaultLocale")
    public void advanceMinutes(boolean goalSeeking, int advance) {
        if (goalSeeking && time.current.second.equals(time.target.second)) {
            return;
        }
        int diff = time.target.second - time.current.second;
        advance += mod(diff, advance);
        time.current.second = advance(time.current.second, advance);
    }

    public void
    advanceHours(boolean goalSeeking, int advance) {
        if (Animator.DEBUG && getRow() == 0 && getCol() == 0) {
            System.out.printf("advanceHours: %b, %b\n",
                    goalSeeking, time.current.first.equals(time.target.first));
        }
        if (goalSeeking && time.current.first.equals(time.target.first)) {
            return;
        }
        int diff = time.target.first - time.current.first;
        advance += mod(diff, advance);
        time.current.first = advance(time.current.first, advance);
    }

    public boolean handsAreOnTarget() {
        return time.current.first.equals(time.target.first) && time.current.second.equals(time.target.second);
    }

    private int advance(int current, int advance) {
        return mod(current + advance, 360);
    }

    private int mod(int a, int b) {
        int res = a % b;
        if (res < 0) {
            res += b;
        }
        return res;
    }

    //{ DEBUG!!
    public void toGoal() {
        time.current.first = time.target.first;
        time.current.second = time.target.second;
    }
    //} DEBUG!!
}