package org.springframework.richclient.form.binding.swing.text;

import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.text.SelectAllFocusListener;
import org.springframework.binding.form.FormModel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.Map;

/**
 * <p>
 * This binder creates {@link DocumentBinding}s. Here you can specify the {@link org.springframework.richclient.form.binding.swing.text.DocumentFactory}
 * that generates the {@link javax.swing.text.Document} to be attached to a {@link javax.swing.text.JTextComponent}. Additionally empty strings can be converted
 * to <code>null</code>.
 * </p>
 * <p>
 * To use this binding copy the following in your context at the appropriate place and adapt the configuration to your needs.
 * The given configuration will create a binding accepting only alfa-numeric characters, capitalizes lower-case letters and will translate empty
 * strings to <code>null</code>.
 * <pre>
 * &lt;bean id="alfaNumericDocumentBinder" class="org.springframework.richclient.form.binding.swing.text.DocumentBinder" lazy-init="true"&gt;
 *   &lt;property name="documentFactory"&gt;
 *     &lt;bean id="alfaNumericDocumentFactory" class="org.springframework.richclient.form.binding.swing.text.RegExDocumentFactory"&gt;
 *       &lt;property name="characterPattern" value="[0-9A-z]"/&gt;
 *       &lt;property name="convertToUppercase" value="true"/&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 *   &lt;property name="convertEmptyToNull" value="true"/&gt;
 * &lt;/bean&gt;
 * </pre>
 * </p>
 *
 * @author Lieven Doclo
 * @author Jan Hoskens
 */
public class DocumentBinder extends AbstractBinder {

    /** The factory providing the document behind the JTextComponent. */
    private DocumentFactory documentFactory;

    /** Convert the value to <code>null</code> when the Document contains an empty string. */
    private boolean convertEmptyToNull = true;

    /** Default constructor. */
    public DocumentBinder() {
        super(String.class);
    }

    /**
     * Returns the {@link DocumentFactory}.
     *
     * @return the <code>DocumentFactory</code>.
     */
    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    /**
     * Set the {@link DocumentFactory}.
     *
     * @param documentFactory the <code>DocumentFactory</code>.
     */
    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    /**
     * Returns whether or not an empty string in the Document is converted to <code>null</code>.
     *
     * @return <code>true</code> if all empty strings should be converted to <code>null</code>.
     */
    public boolean isConvertEmptyToNull() {
        return convertEmptyToNull;
    }

    /**
     * Set to <code>true</code> if empty strings in a Document should be converted to <code>null</code>.
     *
     * @param convertEmptyToNull when <code>true</code> all empty strings are converted to <code>null</code>.
     */
    public void setConvertEmptyToNull(boolean convertEmptyToNull) {
        this.convertEmptyToNull = convertEmptyToNull;
    }

    /** {@inheritDoc} */
    protected JComponent createControl(Map context) {
        JTextComponent textComponent;
        if (getDocumentFactory() != null) {
            textComponent = new JTextField(getDocumentFactory().createDocument(), null, 0);
        } else {
            textComponent = new JTextField();
        }
        textComponent.addFocusListener(new SelectAllFocusListener(textComponent));
        return textComponent;
    }

    /** {@inheritDoc} */
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        return new DocumentBinding((JTextComponent) control, formModel, formPropertyPath, isConvertEmptyToNull());
    }
}