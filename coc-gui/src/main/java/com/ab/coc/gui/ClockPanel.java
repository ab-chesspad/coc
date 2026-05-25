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
package com.ab.coc.gui;

import static com.ab.config.Config.ConfigParam;

import com.ab.coc.gui.config.CConfig;
import com.ab.coc.Animator;
import com.ab.coc.Dial;

import javax.swing.*;
import java.awt.*;

public class ClockPanel extends JPanel implements Animator.Observer {
    private Dial[] dials;

    private int xMargin, yMargin, dialSize;
    private final Rectangle mainRectangle = Main.mainFrame.getBounds();

    public CConfig config() {
        return CConfig.getInstance();
    }

    @Override
    public void update(Dial[] dials) {
        this.dials = dials;
        adjustSizes();
        this.repaint();
    }

    void adjustSizes() {
        int x_size = Animator.xSize, y_size = Animator.ySize;
        int xSize = mainRectangle.width / x_size;
        int ySize = mainRectangle.height / y_size;
        dialSize = Math.min(xSize, ySize);
        int wPanel = dialSize * x_size;
        xMargin = (mainRectangle.width - wPanel) / 2;
        int hPanel = dialSize * y_size;
        yMargin = (mainRectangle.height - hPanel) / 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g == null || dials == null) {
            return;
        }
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(config().getCurColorParam(ConfigParam.bgColor));
        g2d.fillRect(0, 0, mainRectangle.width, mainRectangle.height);

        for (Dial dial : dials) {
            paintDial(dial, g2d);
        }
    }

    private void paintDial(Dial dial, Graphics2D g) {
        int col = dial.getCol();
        int row = dial.getRow();

        int xOffs = xMargin + col * dialSize;
        int yOffs = yMargin + row * dialSize;

        double dia;      // inner circle diameter
        int dialRadius;
        int xCenter, yCenter;

        // dial:
        int border = config().getCurIntParam(ConfigParam.borderWidth);
        int shadow = config().getCurIntParam(ConfigParam.shadowWidth);
        dia = dialSize - 2 * border - shadow;
        if (dia < 2) {
            dia = 2;
        }

        // outer circle, border
        g.setColor(getColorParam(ConfigParam.borderColor, dial.getDialAlpha()));
        g.fillOval(xOffs, yOffs, dialSize, dialSize);

        // shadow circle
        g.setColor(getColorParam(ConfigParam.shadowColor, dial.getDialAlpha()));
        g.fillOval(xOffs + border, yOffs + border,
                dialSize - 2 * border, dialSize - 2 * border);

        // assuming 'light' comes from the top left corner
        double angle = Math.toRadians(45);
        if (row != 0 || col != 0) {
            angle = Math.atan2(row, col);
        }

        // inner circle must be tangential to shadow circle
        double deltaRad = border + (double)shadow / 2;
        double dx = deltaRad * (1 + Math.cos(angle));
        double dy = deltaRad * (1 + Math.sin(angle));
        dx -= border * Math.cos(angle);
        dy -= border * Math.sin(angle);
        Color dialColor = getColorParam(ConfigParam.dialColor, dial.getDialAlpha());
        g.setColor(dialColor);
        g.fillOval((int) (xOffs + dx), (int) (yOffs + dy),
                (int) (dia), (int) (dia));

        dialRadius = (int)(dia / 2);
        xCenter = (int)(xOffs  + dx + dialRadius);
        yCenter = (int)(yOffs  + dy + dialRadius);

        // hands:
        Color color = getColorParam(ConfigParam.handsColor, dial.getHandsAlpha());
        g.setColor(color);
        if (Animator.DEBUG_TARGET != null) {
            g.setStroke(new BasicStroke((float) (dia / 7)));
        } else {
            g.setStroke(new BasicStroke((float) (dia / 10)));
        }
        double rad;

        rad = Math.toRadians(dial.getHours());
        int xDial = (int)(xCenter + dialRadius * 0.7 * Math.cos(rad));
        int yDial = (int)(yCenter + dialRadius * 0.7 * Math.sin(rad));
        g.drawLine(xCenter, yCenter, xDial, yDial);

        rad = Math.toRadians(dial.getMinutes());
        xDial = (int)(xCenter + dialRadius * 0.9 * Math.cos(rad));
        yDial = (int)(yCenter + dialRadius * 0.9 * Math.sin(rad));
        g.drawLine(xCenter, yCenter, xDial, yDial);
    }

    private Color getColorParam(ConfigParam configParam, int alpha) {
        Color color = config().getCurColorParam(configParam);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        if (color.getAlpha() == 0) {
            alpha = 0;
        }
        color = new Color(red, green, blue, alpha);
        return  color;
    }
}