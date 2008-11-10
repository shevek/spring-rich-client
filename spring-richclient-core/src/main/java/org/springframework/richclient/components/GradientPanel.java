/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.springframework.util.Assert;

/**
 * A JPanel with a nice gradient background (should be move to another package)
 * @author cro
 */
public class GradientPanel extends JPanel {

    private Color upperLeftColor;

    private Color lowerRightColor;

    public GradientPanel(Color lowerRightColor, Color upperLeftColor) {
        this.lowerRightColor = lowerRightColor;
        this.upperLeftColor = upperLeftColor;
    }

    public GradientPanel(Color lowerRightColor) {
        this(lowerRightColor, Color.white);
    }

    public GradientPanel() {
        this(null);
    }

    private void paintGradientComponent(int w, int h, Graphics2D g2) {
        if (lowerRightColor == null)
            lowerRightColor = getBackground();

        Assert.notNull(upperLeftColor, "The OuterColor cannot be null");

        Rectangle2D rect1 = new Rectangle2D.Float(0, 0, w, h);
        GradientPaint gp = new GradientPaint(w * .80f, h * .30f, upperLeftColor, w * .90f, h * .70f, lowerRightColor);
        g2.setPaint(gp);
        g2.fill(rect1);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Dimension d = getSize();
        g2.setBackground(lowerRightColor);
        g2.clearRect(0, 0, d.width, d.height);
        paintGradientComponent(d.width, d.height, g2);
    }

    public Color getLowerRightColor() {
        return lowerRightColor;
    }

    public void setLowerRightColor(Color lowerRightColor) {
        this.lowerRightColor = lowerRightColor;
    }

    public Color getUpperLeftColor() {
        return upperLeftColor;
    }

    public void setUpperLeftColor(Color upperLeftColor) {
        this.upperLeftColor = upperLeftColor;
    }
}