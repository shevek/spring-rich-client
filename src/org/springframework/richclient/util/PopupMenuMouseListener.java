package org.springframework.richclient.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class PopupMenuMouseListener extends MouseAdapter {

    private JPopupMenu popupMenu;
    
    public PopupMenuMouseListener(JPopupMenu popupMenu) {
        Assert.notNull(popupMenu);
        this.popupMenu = popupMenu;
    }
    
    
    public void mousePressed(MouseEvent e) {
        checkEvent(e);
    }
    
    public void mouseReleased(MouseEvent e) {
        checkEvent(e);
    }
    
    private void checkEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onAboutToShow(e);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
            popupMenu.setVisible(true);
        }
    }
    
    protected void onAboutToShow(MouseEvent e) {
        
    }

}