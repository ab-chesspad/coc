/**
 * to avoid dependency on Java or android libraries
 */
package com.ab.util;

import java.io.Serializable;

public class Point extends Pair<Integer, Integer> implements Serializable {
    /**
     * Constructor for a Point.
     *
     * @param x  the first int the Point
     * @param y the second int the Point
     */
    public Point(int x, int y) {
        super(x, y);
    }

}