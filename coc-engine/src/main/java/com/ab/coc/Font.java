/**
 Original is located at https://github.com/colugo/finless-porpoise/blob/master/reference/src/model/Font.java
 Modified by Alexander Bootman on 7/27/2023
 */
package com.ab.coc;

import com.ab.util.Point;

import java.util.HashMap;
import java.util.Map;

public class Font {
    public static final int
        DIGIT_WIDTH = 3,
        COLON_WIDTH = 2,
        HEIGHT = 6,
        dummy_int = 0;

    private final Map<Character, Map<Point, Point>> map = new HashMap<>();

    public Font() {
        add0();
        add1();
        add2();
        add3();
        add4();
        add5();
        add6();
        add7();
        add8();
        add9();
        addColon();
    }

    private void add(Map<Point, Point> map, int x, int y, int hours, int minutes){
        map.put(new Point(x, y), new Point(hours, minutes));
    }

    private void add0() {
        Map<Point, Point> map1 = new HashMap<>();
//        add(map1, 0, 0, 180, 90);
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 90);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 270, 90);
        add(map1, 1, 2, 270, 90);
        add(map1, 2, 2, 270, 90);
//
        add(map1, 0, 3, 90, 270);
        add(map1, 1, 3, 90, 270);
        add(map1, 2, 3, 90, 270);

        add(map1, 0, 4, 270, 90);
        add(map1, 1, 4, 270, 270);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('0', map1);
    }

    private void add1() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 0);
        add(map1, 1, 1, 90, 180);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 2, 270, 90);
        add(map1, 2, 2, 270, 90);

        add(map1, 0, 3, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 3, 270, 90);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 4, 270, 90);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 5, 270, 0);
        add(map1, 2, 5, 270, 180);
        map.put('1', map1);
    }

    private void add2() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 0);
        add(map1, 1, 1, 90, 180);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 90, 0);
        add(map1, 1, 2, 270, 180);
        add(map1, 2, 2, 270, 90);

        add(map1, 0, 3, 270, 90);
        add(map1, 1, 3, 90, 0);
        add(map1, 2, 3, 270, 180);

        add(map1, 0, 4, 270, 90);
        add(map1, 1, 4, 270, 0);
        add(map1, 2, 4, 180, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('2', map1);
    }

    private void add3() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 0);
        add(map1, 1, 1, 90, 180);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 0, 90);
        add(map1, 1, 2, 270, 180);
        add(map1, 2, 2, 270, 90);

        add(map1, 0, 3, 270, 0);
        add(map1, 1, 3, 90, 180);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, 0, 90);
        add(map1, 1, 4, 270, 180);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('3', map1);
    }

    private void add4() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 90);
        add(map1, 2, 0, 90, 180);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 270);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 270, 90);
        add(map1, 1, 2, 270, 270);
        add(map1, 2, 2, 270, 90);

        add(map1, 0, 3, 270, 0);
        add(map1, 1, 3, 180, 90);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 4, 270, 90);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 5, 270, 0);
        add(map1, 2, 5, 270, 180);
        map.put('4', map1);
    }

    private void add5() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 0);
        add(map1, 2, 1, 270, 180);

        add(map1, 0, 2, 270, 90);
        add(map1, 1, 2, 270, 0);
        add(map1, 2, 2, 180, 90);

        add(map1, 0, 3, 270, 0);
        add(map1, 1, 3, 90, 180);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, 0, 90);
        add(map1, 1, 4, 270, 180);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('5', map1);
    }

    private void add6() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 0);
        add(map1, 2, 1, 270, 180);

        add(map1, 0, 2, 270, 90);
        add(map1, 1, 2, 270, 0);
        add(map1, 2, 2, 180, 90);

        add(map1, 0, 3, 270, 90);
        add(map1, 1, 3, 90, 90);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, 270, 90);
        add(map1, 1, 4, 270, 270);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('6', map1);
    }

    private void add7() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 0);
        add(map1, 1, 1, 90, 180);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 2, 270, 135);
        add(map1, 2, 2, 270, 135);

        add(map1, 0, 3, 90, 315);
        add(map1, 1, 3, 90, 315);
        add(map1, 2, 3, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);

        add(map1, 0, 4, 270, 90);
        add(map1, 1, 4, 270, 90);
        add(map1, 2, 4, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 270, 180);
        add(map1, 2, 5, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        map.put('7', map1);
    }

    private void add8() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 90);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 270, 45);
        add(map1, 1, 2, 270, 270);
        add(map1, 2, 2, 270, 135);

        add(map1, 0, 3, 90, 315);
        add(map1, 1, 3, 90, 90);
        add(map1, 2, 3, 90, 225);

        add(map1, 0, 4, 270, 90);
        add(map1, 1, 4, 270, 270);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('8', map1);
    }

    private void add9() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, 90, 0);
        add(map1, 1, 0, 180, 0);
        add(map1, 2, 0, 180, 90);

        add(map1, 0, 1, 270, 90);
        add(map1, 1, 1, 90, 90);
        add(map1, 2, 1, 270, 90);

        add(map1, 0, 2, 270, 90);
        add(map1, 1, 2, 270, 270);
        add(map1, 2, 2, 270, 90);

        add(map1, 0, 3, 270, 0);
        add(map1, 1, 3, 90, 180);
        add(map1, 2, 3, 270, 90);

        add(map1, 0, 4, 0, 90);
        add(map1, 1, 4, 270, 180);
        add(map1, 2, 4, 270, 90);

        add(map1, 0, 5, 270, 0);
        add(map1, 1, 5, 180, 0);
        add(map1, 2, 5, 180, 270);

        map.put('9', map1);
    }

    private void addColon() {
        Map<Point, Point> map1 = new HashMap<>();
        add(map1, 0, 0, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 0, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);

        add(map1, 0, 1, 90, 0);
        add(map1, 1, 1, 90, 180);

        add(map1, 0, 2, 270, 0);
        add(map1, 1, 2, 180, 270);

        add(map1, 0, 3, 90, 0);
        add(map1, 1, 3, 90, 180);

        add(map1, 0, 4, 270, 0);
        add(map1, 1, 4, 180, 270);

        add(map1, 0, 5, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        add(map1, 1, 5, Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);

        map.put(':', map1);
    }

    public Point getHands(char ch, int x, int y) {
        Point p = null;
        Map<Point, Point> m = map.get(ch);
        if (m != null) {
            p = m.get(new Point(x, y));
        }
        if (p == null) {
            p = new Point(Dial.HIDE_ANGLE, Dial.HIDE_ANGLE);
        }
        return p;
    }
}
