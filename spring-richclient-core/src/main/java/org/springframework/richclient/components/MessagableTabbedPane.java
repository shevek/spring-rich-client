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
import java.awt.*;

public class MessagableTabbedPane extends JTabbedPane implements MessagableTab
{

    private List<TabMessages> messages = new ArrayList<TabMessages>();
    private IconSource iconSource;


    public MessagableTabbedPane()
    {
        super();
    }

    public MessagableTabbedPane(int tabPlacement)
    {
        super(tabPlacement);
    }

    public MessagableTabbedPane(int tabPlacement, int tabPolicy)
    {
        super(tabPlacement, tabPolicy);
    }

    private static class TabMessages
    {
        private int numberOfErrors = 0;
        private HashMap<Object, Message> messages = new HashMap<Object, Message>();
        private Stack<Message> messageStack = new Stack<Message>();

        public boolean put(Object key, Message message)
        {
            boolean firstErrorOrLessErrors = false;
            Message oldMessage = (Message)this.messages.get(key);
            if (oldMessage != message)
            {
                if ((oldMessage == null) || (message == null) || (oldMessage.getSeverity() != message.getSeverity()))
                {
                    if ((message != null) && (message.getSeverity() == Severity.ERROR))
                    {
                        if (numberOfErrors == 0)
                            firstErrorOrLessErrors = true;
                        ++this.numberOfErrors;
                        messageStack.add(message);
                    }
                    else if ((oldMessage != null) && (oldMessage.getSeverity() == Severity.ERROR))
                    {
                        --this.numberOfErrors;
                        messageStack.remove(oldMessage);
                        firstErrorOrLessErrors = true;
                    }
                }
                if (message != null)
                    this.messages.put(key, message);
                else
                    this.messages.remove(key);
            }
            return firstErrorOrLessErrors;
        }

        public Message get(Object key)
        {
            return (Message)this.messages.get(key);
        }

        public int getNumberOfErrors()
        {
            return this.numberOfErrors;
        }

        public boolean hasErrors()
        {
            return this.numberOfErrors > 0;
        }

        public String getFirstMessage()
        {
            if (messageStack.size() > 0)
                return ((Message)messageStack.firstElement()).getMessage();

            return null;
        }
    }

    public void insertTab(String title, Icon icon, Component component, String tip, int index)
    {
        super.insertTab(title, icon, component, tip, index);
        messages.add(index, new TabMessages());
    }

    private Icon getIcon(String severityLabel)
    {
        if (iconSource == null)
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        return iconSource.getIcon("severity." + severityLabel + ".overlay");
    }

    public void setMessage(Object source, Message message, int tabIndex)
    {
        TabMessages tabMessages = (TabMessages)this.messages.get(tabIndex);
        // if first error or less errors than before, update icon/tooltip
        if (tabMessages.put(source, message))
        {
            if (tabMessages.hasErrors())
            {
                setIconAt(tabIndex, getIcon(Severity.ERROR.getLabel()));
                setToolTipTextAt(tabIndex, tabMessages.getFirstMessage());
            }
            else
            {
                setIconAt(tabIndex, null);
                setToolTipTextAt(tabIndex, null);
            }
        }
    }
}
