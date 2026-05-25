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

import com.ab.config.Config;
import static com.ab.config.Config.ConfigParam;
import com.ab.util.Pair;
import com.ab.util.Point;

//import java.awt.*;    // no java.awt references!
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Animator {
    static final boolean DEBUG = false;
    public static final EnumGoal DEBUG_TARGET = null;

    public static final int ADVANCE_ALPHA = 5;

    private static final Random rand = new Random();

    public enum EnumAnimation {
        QUADRANTS,
        CLOCKWISE,
        COUNTERCLOCKWISE,
        PACMAN,
        OPPOSITE,
        WAVE,
        ;

        public static EnumAnimation random() {
            if (DEBUG_TARGET != null) {
                return EnumAnimation.WAVE;
            }
            EnumAnimation[] choices = EnumAnimation.values();
            return choices[rand.nextInt(choices.length)];
        }
    }

    public enum EnumGoal {
        BURST(1),
        CHICKENWIRE(1),
        CIRCLES(1),
        ALL_ALIGNED(1),
        STAR(1),
        CUP(2),
        TIME(5),
        ;

        private final int weight;

        EnumGoal(int weight) {
            this.weight = weight;
        }

        private static int totalWeight = 0;

        public static int randomAngle;
        public static EnumGoal random() {
            if (DEBUG_TARGET != null) {
                return DEBUG_TARGET;
            }
            randomAngle = rand.nextInt(180);
            EnumGoal[] choices = EnumGoal.values();
            if (totalWeight == 0) {
                // unexpected Java limitation
                for (EnumGoal choice : choices) {
                    totalWeight += choice.weight;
                }
            }
            int randInt = rand.nextInt(totalWeight);
            int tier = 0;
            for (EnumGoal choice : choices) {
                tier += choice.weight;
                if (tier > randInt) {
                    return choice;
                }
            }
            // should never be here
            return choices[choices.length - 1];
        }
    }

    public enum Quadrant {
        UL,
        UR,
        LL,
        LR,
        ;

        public static Quadrant quadrant(int v) {
            return Quadrant.values()[v];
        }
    }

    public enum AnimStage {
        Free_Wheel(120),    // 120
        To_Target(0),       // end when all on target
        Hide_Dials(0),      // end when all erased
        On_Target(40),      // 60
        ;

        private final int count;

        AnimStage(int count) {
            this.count = count;
        }

        public static AnimStage initAnimStage() {
            return values()[0];
        }

        public int getCount() {
            return count;
        }

        public AnimStage next() {
            int v = this.ordinal() + 1;
            if (v >= values().length) {
                v = 0;
            }
            return values()[v];
        }
    }

    public static final int
        MIN_Y_SIZE = Font.HEIGHT,
        MIN_X_SIZE = 4 * Font.DIGIT_WIDTH + Font.COLON_WIDTH,
        ADVANCE_ANGLE = 3,
        dummy_int = 0;

    public static final int[] SYMBOL_OFFSET = {Font.DIGIT_WIDTH, 2 * Font.DIGIT_WIDTH,
            2 * Font.DIGIT_WIDTH + Font.COLON_WIDTH, 3 * Font.DIGIT_WIDTH + Font.COLON_WIDTH,
            4 * Font.DIGIT_WIDTH + Font.COLON_WIDTH};

    public static int xSize, xMargin, ySize, yMargin;

    public double xCenter, yCenter;

    private long themeStartTime;

    private final Font font = new Font();
    private final Cup cup = new Cup();
    private final Observer observer;
    private final Dial[] dials;
    private static final AnimationData animationData = new AnimationData();
    private boolean freeze = false;
    private int animDelay;

    private Timer timer;
    private TimerTask timerTask;

    private Config config;

    protected static Animator instance;

    public static Animator getInstance() {
        return instance;
    }

    public Animator(Observer observer, Config config) {
        this.observer = observer;
        instance = this;
        this.config = config;
        adjustSizes();
        dials = new Dial[xSize * ySize];

        int indx = -1;
        for (int y = 0; y < ySize; y++) {
            int panelOffset = 0;
            int xOffset = 0;
            for (int x = 0; x < xSize; x++) {
                int v = ((y / (ySize / 2)) << 1) + x / (xSize / 2);
                Dial d = new Dial(x, y, Quadrant.quadrant(v));
                int vv = -1;
                if (x >= xMargin && x - xMargin < MIN_X_SIZE) {
                    if (x >= xMargin + SYMBOL_OFFSET[panelOffset]) {
                        xOffset = SYMBOL_OFFSET[panelOffset++];
                    }
                    vv = x - xMargin - xOffset;
                }
                d.setSymbolOffset(vv);
                d.setSymbolIndex(panelOffset);
                dials[++indx] = d;
            }
        }
    }

    void adjustSizes() {
        Pair<Integer, Integer> mainSize = config.mainSize;
        int _xSize = mainSize.first / MIN_X_SIZE;
        int _ySize = mainSize.second / MIN_Y_SIZE;
        int dialSize = Math.min(_xSize, _ySize);
        xSize = (mainSize.first / dialSize / 2) * 2;     // make it even
        ySize = (mainSize.second / dialSize / 2) * 2;    // make it even
        xMargin = (xSize - MIN_X_SIZE) / 2;
        yMargin = (ySize - MIN_Y_SIZE) / 2;
        xCenter = ((double)xSize - 1) / 2;
        yCenter = ((double)ySize - 1) / 2;
    }

    public synchronized void refresh(Config config) {
        this.config = config;
        int animDelay = config.getIntParam(ConfigParam.animTime);
        if (DEBUG)
        {
            System.out.printf("refresh, animDelay %s\n", animDelay);
        }
        themeStartTime = 0; // to advance to next theme
        if (this.animDelay != animDelay) {
            this.animDelay = animDelay;
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            if (timerTask != null) {
                timerTask.cancel();
            }
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            };
            timer.schedule(timerTask, 0, animDelay);
        }
    }

    private void update() {
        if (DEBUG) {
            System.out.printf("%d thread %s\n",
                System.currentTimeMillis(), Thread.currentThread().getName());
        }
        if (freeze) {
            return;
        }

        long now = System.currentTimeMillis();
        long duration = (now - themeStartTime) / 60 / 1000; // to minutes
        if (duration >= config.getCurIntParam(ConfigParam.themeTime)) {
            themeStartTime = now;
            config.toNextTheme();
        }

        updateMode();
        if (DEBUG) {
            verifyTargets();
        }

        for (Dial d : dials) {
            int alphaUpdate;
            switch (animationData.animStage) {
                case To_Target:
                case Free_Wheel:
                    alphaUpdate = -ADVANCE_ALPHA;
                    if (config.getCurBooleanParam(ConfigParam.drawDials)) {
                        alphaUpdate = ADVANCE_ALPHA;
                    }
                    d.updateAlpha(ADVANCE_ALPHA, alphaUpdate);
                    break;

                case Hide_Dials:
                    alphaUpdate = ADVANCE_ALPHA;
                    if (d.isErasableDial()) {
                        alphaUpdate = -ADVANCE_ALPHA;
                    }
                    d.updateAlpha(alphaUpdate, -ADVANCE_ALPHA);
                    break;

                case On_Target:
                    break;
            }

            switch (animationData.animationMode) {
                case QUADRANTS:
                    animateQuadrants(d);
                    break;
                case CLOCKWISE:
                    animateClockwise(d);
                    break;
                case COUNTERCLOCKWISE:
                    animateCounterClockwise(d);
                    break;
                case PACMAN:
                    animatePacman(d);
                    break;
                case OPPOSITE:
                    animateOpposite(d);
                    break;
                case WAVE:
                    animateWave(d);
                    break;
            }
        }
        observer.update(dials);
    }

    @SuppressWarnings("DefaultLocale")
    private void updateMode() {
        ++animationData.updateCount;
        boolean stageFinished = false;
        switch (animationData.animStage) {
            case Free_Wheel:
                stageFinished = animationData.updateCount >= animationData.animStage.getCount();
                break;

            case To_Target:
                stageFinished = onTargetCount() >= dials.length;
                break;

            case Hide_Dials:
                stageFinished = hiddenDialsCount() >= dials.length;
                break;

            case On_Target:
                stageFinished = animationData.updateCount >= animationData.animStage.getCount();
                break;
        }

        if (!stageFinished) {
            return;
        }

        animationData.animStage = animationData.animStage.next();
        animationData.updateCount = 0;

        switch (animationData.animStage) {
            case Free_Wheel:
                animationData.goalSeeking = false;
                animationData.animationMode = EnumAnimation.random();
                break;

            case To_Target:
                animationData.goalSeeking = true;
                animationData.goalMode = EnumGoal.random();
                break;

            case On_Target:
                animationData.goalSeeking = true;
                return;
        }

        if (DEBUG) {
            System.out.printf("next %s: %s, %s updates=%s, targets=%s\n",
                    animationData.animStage,
                    animationData.goalMode, animationData.animationMode,
                    animationData.updateCount, onTargetCount());
        }

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        for (Dial d : dials) {
            switch (animationData.goalMode) {
                case BURST:
                    goalSetBurst(d);
                    break;
                case CHICKENWIRE:
                    goalSetChickenWire(d);
                    break;
                case CIRCLES:
                    goalSetCircles(d);
                    break;
                case ALL_ALIGNED:
                    goalAllAligned(d);
                    break;
                case STAR:
                    goalSetStar(d);
                    break;
                case TIME:
                    goalSetTime(time, d);
                    break;
                case CUP:
                    setCup(d);
                    break;
            }
        }
    }

    public void goalSetChickenWire(Dial d) {
        Quadrant q = d.getQuadrant();
        switch (q) {
            case UL:
            case LR:
                d.setGoal(225, 45);
                break;

            case UR:
            case LL:
                d.setGoal(315, 135);
                break;
        }
    }

    public void goalAllAligned(Dial d) {
        d.setGoal(EnumGoal.randomAngle, EnumGoal.randomAngle + 180);
    }

    public void goalSetStar(Dial d) {
        int col = d.getCol();
        int row = d.getRow();
        int MAX_ANGLE = 30;
        int MIN_ANGLE = 0;

        double x = col - xCenter;
        double y = row - yCenter;
        double centralAngle = Math.atan2(-y, -x);
        int angle = MAX_ANGLE + (int)((MIN_ANGLE - MAX_ANGLE) *
                Math.sqrt(x * x + y * y) / Math.sqrt(xSize * xSize + ySize * ySize) * 2);
        d.setGoal((int)Math.toDegrees(centralAngle) + angle, (int)Math.toDegrees(centralAngle) - angle);
    }

    public void goalSetBurst(Dial d) {
        int col = d.getCol();
        int row = d.getRow();

        double x = col - xCenter;
        double y = row - yCenter;

        double centralAngle = Math.atan2(y, x);
        d.setGoal((int)Math.toDegrees(centralAngle) + 1, (int)Math.toDegrees(centralAngle) + 1);    // avoid HIDE_ANGLE
    }

    public void goalSetCircles(Dial d) {
        int col = d.getCol();
        int row = d.getRow();
        int MAX_ANGLE = 120;
        int MIN_ANGLE = 60;

        double x = col - xCenter;
        double y = row - yCenter;
        double centralAngle = Math.atan2(-y, -x);
        int angle = MIN_ANGLE + (int)((MAX_ANGLE - MIN_ANGLE) *
            Math.sqrt(x * x + y * y) / Math.sqrt(xSize * xSize + ySize * ySize) * 2);
        d.setGoal((int)Math.toDegrees(centralAngle) + angle, (int)Math.toDegrees(centralAngle) - angle);
    }

    private void goalSetTime(String time, Dial d) {
        char ch = time.charAt(d.getSymbolIndex());
        Point hands = font.getHands(ch, d.getSymbolOffset(), d.getRow() - yMargin);
        d.setGoal(hands.first, hands.second);
    }

    private void setCup(Dial d) {
        int xMargin = (xSize - Cup.WIDTH) / 2;
        int yMargin = (ySize - Cup.HEIGHT) / 2;
        Point hands = cup.getHands(d.getCol() - xMargin, d.getRow() - yMargin);
        d.setGoal(hands.first, hands.second);
    }

    private void animateClockwise(Dial d) {
        if (DEBUG && d.getRow() == 0 && d.getCol() == 0) {
            System.out.println("animateClockwise start loop");
        }
        d.advanceHours(animationData.goalSeeking, ADVANCE_ANGLE);
        d.advanceMinutes(animationData.goalSeeking, ADVANCE_ANGLE);
    }

    private void animateCounterClockwise(Dial d) {
        d.advanceHours(animationData.goalSeeking, -ADVANCE_ANGLE);
        d.advanceMinutes(animationData.goalSeeking, -ADVANCE_ANGLE);
    }

    private void animatePacman(Dial d) {
        d.advanceHours(animationData.goalSeeking, 2 * ADVANCE_ANGLE);
        d.advanceMinutes(animationData.goalSeeking, ADVANCE_ANGLE);
    }

    private void animateOpposite(Dial d) {
        d.advanceHours(animationData.goalSeeking, ADVANCE_ANGLE);
        d.advanceMinutes(animationData.goalSeeking, -ADVANCE_ANGLE);
    }

    private void animateQuadrants(Dial d) {
        Quadrant quadrant = d.getQuadrant();
        switch (quadrant) {
            case UL:
            case LR:
                d.advanceHours(animationData.goalSeeking, ADVANCE_ANGLE);
                d.advanceMinutes(animationData.goalSeeking, ADVANCE_ANGLE);
                break;

            case UR:
            case LL:
                d.advanceHours(animationData.goalSeeking, -ADVANCE_ANGLE);
                d.advanceMinutes(animationData.goalSeeking, -ADVANCE_ANGLE);
                break;
        }
    }

    private void animateWave(Dial d) {
        int updateColumn = animationData.updateCount * 3 / xSize;
        int updateRow = animationData.updateCount * 3 / ySize;

        if (d.getCol() <= updateColumn &&
                d.getRow() <= updateRow) {
            d.advanceHours(animationData.goalSeeking, ADVANCE_ANGLE);
            d.advanceMinutes(animationData.goalSeeking, ADVANCE_ANGLE);
        }
    }

    private static class AnimationData {
        AnimStage animStage = AnimStage.initAnimStage();
        int updateCount;
        boolean goalSeeking = false;
        EnumAnimation animationMode = EnumAnimation.random();
        EnumGoal goalMode = EnumGoal.random();
    }

    private int onTargetCount() {
        int count = 0;
        for (Dial d : dials) {
            if (d.handsAreOnTarget()) {
                ++count;
            }
        }
        return count;
    }

    private int hiddenDialsCount() {
        int count = 0;
        for (Dial d : dials) {
            if (d.getHandsAlpha() == 0 || !d.isErasableDial()) {
                ++count;
            }
        }
        return count;
    }

    public interface Observer {
        void update(Dial[] dials);
    }

    // DEBUG!! {
    public int verifyTargets() {
        int flagCount = 0;
        int count = 0;
        int alphaCount = 0;
        int index = -1;
        for (Dial d : dials) {
            ++index;
            if (d.handsAreOnTarget()) {
                ++count;
            } else {
                System.out.printf("%d -> %sx%s hands not on target\n", index, d.getCol(), d.getRow());
            }
            if (d.getHandsAlpha() == 255) {
                ++alphaCount;
            } else {
                System.out.printf("%d -> %sx%s opacity=%d\n", index, d.getCol(), d.getRow(), d.getHandsAlpha());
            }
        }
        return alphaCount * 10000 + count * 100 + flagCount;
    }

    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public void setTime(String time) {
        for (Dial d : dials) {
            goalSetTime(time, d);
            d.toGoal();
        }
        observer.update(dials);
    }

    public interface GoalSetter {
        void setGoal(Dial d);
    }
    // DEBUG!! }
}