/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import org.springframework.util.Assert;

/**
 * Utility functions for manipulating the display of windows.
 * 
 * @author Keith Donald
 */
public class WindowUtils {

    private WindowUtils() {
    }

    /**
     * Return the system screen size.
     * 
     * @return The dimension of the system screen size.
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Return the centering point on the screen for the object with the
     * specified dimension.
     * 
     * @param dimension
     *            the dimension of an object
     * @return The centering point on the screen for that object.
     */
    public static Point getCenteringPointOnScreen(Dimension dimension) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (dimension.width > screen.width) {
            dimension.width = screen.width;
        }
        if (dimension.height > screen.height) {
            dimension.height = screen.height;
        }
        return new Point((screen.width - dimension.width) / 2,
                (screen.height - dimension.height) / 2);
    }

    /**
     * Pack the window, center it on the screen, and set the window visible.
     * 
     * @param window
     *            the window to center and show.
     */
    public static void centerOnScreenAndSetVisible(Window window) {
        window.pack();
        window.setLocationRelativeTo(window);
        window.setVisible(true);
    }

    /**
     * Pack the window, center it relative to it's parent, and set the window
     * visible.
     * 
     * @param window
     *            the window to center and show.
     */
    public static void centerOnParentAndSetVisible(Window window) {
        window.pack();
        window.setLocationRelativeTo(window.getParent());
        window.setVisible(true);
    }

    /**
     * Return a <code>Dimension</code> whose size is defined not in terms of
     * pixels, but in terms of a given percent of the screen's width and height.
     * 
     * <P>
     * Use to set the preferred size of a component to a certain percentage of
     * the screen.
     * 
     * @param percentWidth
     *            percentage width of the screen, in range <code>1..100</code>.
     * @param percentHeight
     *            percentage height of the screen, in range <code>1..100</code>.
     */
    public static final Dimension getDimensionFromPercent(int percentWidth,
            int percentHeight) {
        Assert.isInRange(percentWidth, 1, 100);
        Assert.isInRange(percentHeight, 1, 100);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return calcDimensionFromPercent(screenSize, percentWidth, percentHeight);
    }

    private static Dimension calcDimensionFromPercent(Dimension dimension,
            int percentWidth, int percentHeight) {
        int width = dimension.width * percentWidth / 100;
        int height = dimension.height * percentHeight / 100;
        return new Dimension(width, height);
    }

}