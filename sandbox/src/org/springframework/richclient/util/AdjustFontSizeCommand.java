package org.springframework.richclient.util;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.ActionCommand;

import com.jgoodies.forms.layout.Sizes;

public class AdjustFontSizeCommand extends ActionCommand {
    private double fontAdjustment = +1.0d;

    public double getFontAdjustment() {
        return this.fontAdjustment;
    }

    public void setFontAdjustment(final double fontAdjustment) {
        this.fontAdjustment = fontAdjustment;
    }

    protected void adjustFont(final double adjustAmount) {
        System.out.println(Sizes.getUnitConverter().dialogUnitYAsPixel(10, new JLabel()));
        final Object[] objs = UIManager.getLookAndFeel().getDefaults().keySet().toArray();
        for (int i = 0; i < objs.length; i++) {
            if (objs[i].toString().toUpperCase().indexOf("FONT") != -1) {
                final Font font = UIManager.getFont(objs[i]);
                UIManager.put(objs[i], new FontUIResource(font.deriveFont((float)(font.getSize() + adjustAmount))));
            }
        }        
        System.out.println(Sizes.getUnitConverter().dialogUnitYAsPixel(10, new JLabel()));
        ApplicationWindow[] applicationWindows = Application.instance().getWindowManager().getWindows();
        for (int i = 0; i < applicationWindows.length; i++) {
            ApplicationWindow window = applicationWindows[i];
            SwingUtilities.updateComponentTreeUI(window.getControl());
            window.getControl().repaint();
        }        
    }

    protected void doExecuteCommand() {
        final double fontAdjustment;

        final Object fontAdjustmentParam = getParameter("fontAdjustment");
        if (fontAdjustmentParam != null) {
            if (fontAdjustmentParam instanceof Number) {
                fontAdjustment = ((Number)fontAdjustmentParam).doubleValue();
            }
            else {
                fontAdjustment = Double.parseDouble(fontAdjustmentParam.toString());
            }
        }
        else {
            fontAdjustment = getFontAdjustment();
        }

        adjustFont(fontAdjustment);
    }
}