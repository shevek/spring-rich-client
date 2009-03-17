package org.springframework.richclient.form.binding.swing;

import org.jdesktop.xswingx.JXSearchField;

import javax.swing.text.JTextComponent;

/**
 * Binder that shows a JXSearchField instead of a simple JTextField
 *
 * @author Lieven Doclo
 */
public class JXSearchFieldBinder extends TextComponentBinder
{
    protected JXSearchFieldBinder()
    {
        super();
        setSelectAllOnFocus(true);
    }

    @Override
    protected JTextComponent createTextComponent()
    {
        return new JXSearchField();
    }
}
