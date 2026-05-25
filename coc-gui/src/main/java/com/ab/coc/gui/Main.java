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
 * Created by Alexander Bootman on 8/22/2023.
 */
package com.ab.coc.gui;

import com.ab.coc.gui.config.CConfig;
import com.ab.coc.Animator;
import com.ab.util.Pair;
import com.ab.util.Util;
import static com.ab.util.Util.OS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    static final boolean DEBUG = false;

    static final Rectangle mainRectangle = new Rectangle();
    public final Insets insets = new Insets(0,0,0,0);
    private static Container mainContainer;
    private static ClockPanel clockPanel;

    public final boolean isFullScreen;
    transient private final Animator animator;

    static final Util util = Util.getInstance();
    static final OS os = util.getOS();

    public static JFrame mainFrame;

    /**
     * Windows ScreenSaver:
     * @param args
     * run ScreenSaver  - /s
     * config           - /c:460436
     * preview          - /p 1967990
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> new Main(args));
    }

    public CConfig config() {
        return CConfig.getInstance();
    }

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public Main(String[] args) {
        instance = this;
        isFullScreen = args.length > 0 && "--root".equals(args[0]);
        GraphicsDevice mainGD = getGraphicsDevice();
        mainFrame = new JFrame(mainGD.getDefaultConfiguration());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(mainRectangle.width, mainRectangle.height);
        mainFrame.setLocation(mainRectangle.x, mainRectangle.y);
        mainFrame.setUndecorated(true);
        mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        mainFrame.setResizable(false);
        if (isFullScreen && mainGD.isFullScreenSupported()) {
            mainGD.setFullScreenWindow(mainFrame);
        }

        mainFrame.addMouseListener(new MouseAdapter() {
            @Override
                public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    System.exit(0);
                } else if (e.getButton() == 3) {
                    new ConfigPopup();
                    System.out.println("setup done");
                }
            }
        });

        mainFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                System.out.printf("mainFrame: %dx%d\n", mainRectangle.width, mainRectangle.height);
                config().mainSize = new Pair<>(mainRectangle.width, mainRectangle.height);
            }
        });

        if (os == OS.windows) {
            // todo: find fullscreen solution
//            mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
//            mainFrame.setUndecorated(true); // <-- the title bar is removed here
        }
        mainContainer = mainFrame.getContentPane();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.X_AXIS));
        clockPanel = new ClockPanel();
        mainContainer.add(clockPanel);
        animator = new Animator(clockPanel, config());
        animator.refresh(config());
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.out.println("Error occurred!");
        }

        mainFrame.setState(Frame.NORMAL);
        mainFrame.setVisible(true);
    }

    private GraphicsDevice getGraphicsDevice() {
        GraphicsDevice mainGD = null;
        Rectangle fullScreen = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            System.out.printf("device:'%s' size=(%dx%d)\n",
                    gd.getIDstring(), gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());

            if (fullScreen.width < gd.getDisplayMode().getWidth()) {
                // will use the widest display
                fullScreen.width = gd.getDisplayMode().getWidth();
                fullScreen.height = gd.getDisplayMode().getHeight();
                mainGD = gd;
            }
        }

        for (GraphicsConfiguration graphicsConfiguration : mainGD.getConfigurations()) {
            Insets thisInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
            if (insets.top < thisInsets.top) {
                insets.top = thisInsets.top;
            }
            if (insets.left < thisInsets.left) {
                insets.left = thisInsets.left;
            }
            if (insets.bottom < thisInsets.bottom) {
                insets.bottom = thisInsets.bottom;
            }
            if (insets.right < thisInsets.right) {
                insets.right = thisInsets.right;
            }
        }
        mainRectangle.width = fullScreen.width;
        mainRectangle.height = fullScreen.height;
        if (!isFullScreen) {
            mainRectangle.width = fullScreen.width - insets.left - insets.right;
            mainRectangle.height = fullScreen.height - insets.top - insets.bottom;
            mainRectangle.x = (fullScreen.width - mainRectangle.width) / 2;
            mainRectangle.y = (fullScreen.height - mainRectangle.height) / 2;
        }
        config().mainSize = new Pair<>(mainRectangle.width, mainRectangle.height);
        return mainGD;
    }
}