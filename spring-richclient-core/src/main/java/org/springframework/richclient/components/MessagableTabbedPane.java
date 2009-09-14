package org.springframework.richclient.components;

import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.application.ApplicationServicesLocator;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map;
import java.awt.*;

/**
 * @author Jan Hoskens
 * @author Geoffrey De Smet
 */
public class MessagableTabbedPane extends JTabbedPane implements MessagableTab {

    private List<MessagableTab> messagableTabs = new ArrayList<MessagableTab>();

    private IconSource iconSource;

    public MessagableTabbedPane() {
        super();
    }

    public MessagableTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }

    public MessagableTabbedPane(int tabPlacement, int tabPolicy) {
        super(tabPlacement, tabPolicy);
    }

    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        super.insertTab(title, icon, component, tip, index);
        messagableTabs.add(index, new MessagableTab());
    }

    @Override
    public void setIconAt(int index, Icon icon) {
        // Hack to allow the error icon to overwrite the real icon
        MessagableTab messagableTab = messagableTabs.get(index);
        messagableTab.setIcon(icon);
        if (!messagableTab.hasErrors()) {
            super.setIconAt(index, icon);
        }
    }

    @Override
    public void setToolTipTextAt(int index, String toolTipText) {
        // Hack to allow the error toolTipText to overwrite the real toolTipText
        MessagableTab messagableTab = messagableTabs.get(index);
        messagableTab.setToolTipText(toolTipText);
        if (!messagableTab.hasErrors()) {
            super.setToolTipTextAt(index, toolTipText);
        }
    }

    public void setMessage(Object source, Message message, int index) {
        MessagableTab messagableTab = messagableTabs.get(index);
        messagableTab.put(source, message);
        if (messagableTab.hasErrors()) {
            // Calling super to avoid the error icon/toolTipText overwrite hack
            super.setIconAt(index, loadIcon(Severity.ERROR.getLabel()));
            super.setToolTipTextAt(index, messagableTab.getFirstErrorMessage());
        } else {
            // Calling super to avoid the error icon/toolTipText overwrite hack
            super.setIconAt(index, messagableTab.getIcon());
            super.setToolTipTextAt(index, messagableTab.getToolTipText());
        }
    }

    private Icon loadIcon(String severityLabel) {
        if (iconSource == null) {
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        }
        return iconSource.getIcon("severity." + severityLabel + ".overlay");
    }

    private static class MessagableTab {

        private Map<Object, Message> messageMap = new HashMap<Object, Message>();
        private Stack<Message> errorMessageStack = new Stack<Message>();

        private Icon icon = null;
        private String toolTipText = null;

        public void put(Object key, Message message) {
            Message oldMessage = messageMap.get(key);
            if (oldMessage != message) {
                // Update errorMessageStack
                if ((oldMessage != null) && (oldMessage.getSeverity() == Severity.ERROR)) {
                    errorMessageStack.remove(oldMessage);
                }
                if ((message != null) && (message.getSeverity() == Severity.ERROR)) {
                    errorMessageStack.add(message);
                }
                // Update messageMap
                if (message != null) {
                    messageMap.put(key, message);
                } else {
                    messageMap.remove(key);
                }
            }
        }

        public Message get(Object key) {
            return messageMap.get(key);
        }

        public String getFirstErrorMessage() {
            if (!hasErrors()) {
                return null;
            }
            return errorMessageStack.firstElement().getMessage();
        }

        public boolean hasErrors() {
            return errorMessageStack.size() > 0;
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        public String getToolTipText() {
            return toolTipText;
        }

        public void setToolTipText(String toolTipText) {
            this.toolTipText = toolTipText;
        }
    }

}
