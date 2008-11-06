package org.springframework.richclient.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.form.binding.swing.text.TimeFormatter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TimeTextField extends JFormattedTextField
{

    Log log = LogFactory.getLog(TimeTextField.class);

    private static final String INPUT_SEPARATORS = "[hHuU\\s\\.,+*-/]";

    public TimeTextField()
    {
        super(new TimeFormatter());
        this.setDocument(new TimeDocument());
        setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        addFocusListener(new UpdateFocusListener());
    }

    private void updateText(String txt)
    {
        try
        {
            getDocument().remove(0, getText().length());
            getDocument().insertString(0, txt, null);
        }
        catch (BadLocationException e)
        {
            log.error("Text out of boundaries. ", e);
        }
    }

    protected void processFocusEvent(FocusEvent e)
    {
        super.processFocusEvent(e);
        if (e.getID() == FocusEvent.FOCUS_GAINED)
        {
            setCaretPosition(0);
            moveCaretPosition(getDocument().getLength());
        }
    }

    class UpdateFocusListener implements FocusListener
    {

        public void focusGained(FocusEvent e)
        {
        }

        public void focusLost(FocusEvent e)
        {
            try
            {
                commitEdit();
                String valueToString = getFormatter().valueToString(getValue());
                updateText(valueToString);
            }
            catch (Exception e1)
            {
                log.error("Invalid date format. ", e1);
            }
        }
    }

    class TimeDocument extends PlainDocument
    {

        public void replace(int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException
        {
            remove(offset, length);
            insertString(offset, text, attrs);
        }

        public void remove(int offs, int len) throws BadLocationException
        {
            super.remove(offs, len);
        }

        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
        {
            if ((str = isValidString(offset, str)) != null)
                super.insertString(offset, str, a);
        }

        private String isValidString(int offset, String str) throws BadLocationException
        {
            if (str == null)
                return null;
            str = str.replaceAll(INPUT_SEPARATORS, TimeFormatter.SEPARATOR_STRING); // set
            // correct
            // Separator

            String s = getText(0, offset) + str;
            if (offset < getLength())
                s += getText(offset, getLength() - offset);

            char[] strArray = s.toCharArray();
            int sepPos = -1;
            for (int i = 0; i < strArray.length; ++i) // check on only
            // digits/one separator
            {
                if (!Character.isDigit(strArray[i]))
                {
                    if (!(TimeFormatter.SEPARATOR == strArray[i]) || (sepPos != -1))
                        return null;

                    sepPos = i;
                    if ((sepPos > 2) || ((getLength() - sepPos) > 2)) // separator
                        // on a
                        // correct
                        // position
                        return null;
                }
            }

            int length = sepPos == -1 ? s.length() : s.length() - 1;
            if (length > 4) // correct number of digits
                return null;

            return str;
        }
    }
}
