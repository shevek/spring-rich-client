package org.springframework.richclient.form.binding.swing;

import org.jdesktop.xswingx.JXSearchField;
import org.springframework.richclient.form.binding.swing.text.DocumentBinder;
import org.springframework.richclient.text.SelectAllFocusListener;

import javax.swing.*;
import java.util.Map;

/**
 * Binder that shows a JXSearchField
 *
 * @author Lieven Doclo
 */
public class JXSearchFieldBinder extends DocumentBinder
{
    protected JXSearchFieldBinder()
    {
        super();
    }

    @Override
    protected JComponent createControl(Map context)
    {
        JXSearchField field = new JXSearchField();
        if (getDocumentFactory() != null)
        {
            field.setDocument(getDocumentFactory().createDocument());
        }
        field.addFocusListener(new SelectAllFocusListener(field));
        return field;
    }
}
