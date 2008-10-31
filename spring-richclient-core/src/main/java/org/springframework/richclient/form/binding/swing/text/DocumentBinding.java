package org.springframework.richclient.form.binding.swing.text;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.util.StringUtils;

/**
 * A binding using a {@link javax.swing.text.JTextComponent}.
 *
 * TODO check how to replace this with TextComponentBinding
 * the latter is actually not using convertEmptyStringToNull and it uses
 * a ValueCommitPolicy(currently fixed). 
 *
 * @author Lieven Doclo
 * @author Jan Hoskens
 */
public class DocumentBinding  extends CustomBinding implements DocumentListener {

    /** The TextComponent to bind to. */
    private JTextComponent textComponent;

    /** Convert empty strings to null? */
    private boolean convertEmptyStringToNull;

    /**
     *  This binding will listen to changes of the Document of the JTextComponent. A specific conversion between <code>null</code> and the empty string can be configured.
     *
     * @param textComponent a JTextComponent.
     * @param formModel the FormModel.
     * @param formPropertyPath the property to bind to.
     * @param convertEmptyStringToNull <code>true</code> if you want all empty strings converted to <code>null</code> before pushing the value to the ValueModel.
     */
    public DocumentBinding(JTextComponent textComponent, FormModel formModel, String formPropertyPath, boolean convertEmptyStringToNull) {
        super(formModel, formPropertyPath, String.class);
        this.textComponent = textComponent;
        this.convertEmptyStringToNull = convertEmptyStringToNull;
    }

    /** {@inheritDoc}*/
    protected void valueModelChanged(Object newValue) {
        this.textComponent.setText((String) newValue);
    }

    /** {@inheritDoc}*/
    protected JComponent doBindControl() {
        textComponent.getDocument().addDocumentListener(this);
        valueModelChanged(getValue());
        return textComponent;
    }

    /** {@inheritDoc}*/
    protected void readOnlyChanged() {
        textComponent.setEditable(!isReadOnly());
    }

    /** {@inheritDoc}*/
    protected void enabledChanged() {
        textComponent.setEnabled(isEnabled());
    }

    /** {@inheritDoc}*/
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    /** {@inheritDoc}*/
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    /** {@inheritDoc}*/
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    /**
     * Simple check to convert <code>null</code> values if necessary.
     */
    private void documentChanged() {
        String textFieldValue = textComponent.getText();
        if (convertEmptyStringToNull && !StringUtils.hasText(textFieldValue)) {
            controlValueChanged(null);
        }
        else {
            controlValueChanged(textFieldValue);
        }
    }
}