/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.binding.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.util.Assert;

/**
 * Adapts the {@link ValueModel}interface to the <code>Document</code>
 * interface, which is the model interface for Swing text components. Used to
 * bind String values to text components, for example a <code>JTextField</code>.
 * At construction time the document is updated with the provided value model's
 * contents.
 * <p>
 * 
 * Instead of extending <code>AbstractDocument</code> or the specialized
 * <code>PlainDocument</code> this class holds a reference to a Document
 * instance and forwards all Document messages to the corresponding method in
 * the reference. By default the delegate is initialized as an instance of
 * PlainDocument; the two parameter constructor allows to provide any other
 * Document implementation. The latter can be useful if the text component uses
 * a custom Document, for example a custom <code>IntegerDocument</code>,
 * <code>MaskedDocument</code>, or <code>StyledDocument</code>.
 * <p>
 * 
 * <strong>Constraints: </strong> The ValueModel must return/set type
 * <code>String</code>.
 * <p>
 * <strong>Example Usage: </strong>
 * 
 * <pre>
 *                ValueModel lastNameModel = new PropertyAdapter(customer, &quot;lastName&quot;, true);
 *                JTextField lastNameField = new JTextField();
 *                lastNameField.setDocument(new DocumentAdapter(lastNameModel));
 *                
 *                ValueModel codeModel = new PropertyAdapter(shipment, &quot;code&quot;, true);
 *                JTextField codeField = new JTextField();
 *                codeField.setDocument(new DocumentAdapter(codeModel), 
 *                                      new MaskedDocument(...));
 * </pre>
 * 
 * @see ValueModel
 * @see Document
 * @see PlainDocument
 */
public final class DocumentAdapter implements Document, ValueChangeListener,
        DocumentListener {

    /**
     * Holds the underlying ValueModel that is used to read values, to update
     * the document and to write values if the document changes.
     */
    private final ValueModel valueModel;

    /**
     * Holds a Document instance delegate that is used to forward all Document
     * messages of the outer adapter. By default it is initialized as an
     * instance of <code>PlainDocument</code>.
     */
    private final Document delegate;

    /**
     * Constructs a <code>DocumentAdapter</code> on the specified value model.
     * The value model must return values of type <code>String</code>.
     * 
     * @param valueModel
     *            a <code>ValueModel</code> that returns Strings
     * @throws NullPointerException
     *             if the subject is <code>null</code>
     */
    public DocumentAdapter(ValueModel valueModel) {
        this(valueModel, new PlainDocument());
    }

    /**
     * Constructs a <code>DocumentAdapter</code> on the specified value model.
     * The value model must return values of type <code>String</code>.
     * 
     * @param valueModel
     *            the underlying String typed ValueModel
     * @param document
     *            the underlying Document implementation
     * @throws NullPointerException
     *             if the subject or document is <code>null</code>
     */
    public DocumentAdapter(ValueModel valueModel, Document document) {
        Assert.notNull(valueModel, "The valueModel property is required");
        Assert.notNull(document, "The document property is required");
        this.valueModel = valueModel;
        this.delegate = document;
        document.addDocumentListener(this);
        valueModel.addValueChangeListener(this);
        updateDocumentText();
    }

    /**
     * Reads the current text from the subject and updates the document
     * contents. Removes the old document content and inserts the new.
     */
    private void updateDocumentText() {
        setDocumentTextSilently(getText());
    }

    private void setDocumentTextSilently(String newText) {
        delegate.removeDocumentListener(this);
        try {
            delegate.remove(0, delegate.getLength());
            delegate.insertString(0, newText, null);
        }
        catch (BadLocationException e) {
            // Should not happen in the way we invoke #remove and #insertString
        }
        delegate.addDocumentListener(this);
    }

    /**
     * Reads the current text from the document and sets the value model.
     */
    private void updateValueModel() {
        setValueSilently(getDocumentText());
    }

    /**
     * Reads the current text from the document and sets it as new value of the
     * value model.
     * 
     * @param newText
     *            the text to be set in the value model
     */
    private void setValueSilently(String newText) {
        valueModel.removeValueChangeListener(this);
        valueModel.setValue(newText);
        valueModel.addValueChangeListener(this);
    }

    /**
     * Returns the text contained in the document.
     * 
     * @return the text contained in the document
     */
    private String getDocumentText() {
        int length = delegate.getLength();
        try {
            return delegate.getText(0, length);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Returns the value model's text value.
     * 
     * @return the value models's text value
     * @throws ClassCastException
     *             if the subject value is not a String
     */
    private String getText() {
        String str = (String)valueModel.getValue();
        return str == null ? "" : str;
    }

    /**
     * The subject value has changed; update the document.
     */
    public void valueChanged() {
        updateDocumentText();
    }

    /**
     * There was an insert into the document; update the subject.
     * 
     * @param e
     *            the document event
     */
    public void insertUpdate(DocumentEvent e) {
        updateValueModel();
    }

    /**
     * A portion of the document has been removed; update the subject.
     * 
     * @param e
     *            the document event
     */
    public void removeUpdate(DocumentEvent e) {
        updateValueModel();
    }

    public void changedUpdate(DocumentEvent e) {
        // Do nothing on attribute changes.
    }

    public int getLength() {
        return delegate.getLength();
    }

    public void addDocumentListener(DocumentListener listener) {
        delegate.addDocumentListener(listener);
    }

    public void removeDocumentListener(DocumentListener listener) {
        delegate.removeDocumentListener(listener);
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        delegate.addUndoableEditListener(listener);
    }

    public void removeUndoableEditListener(UndoableEditListener listener) {
        delegate.removeUndoableEditListener(listener);
    }

    public Object getProperty(Object key) {
        return delegate.getProperty(key);
    }

    public void putProperty(Object key, Object value) {
        delegate.putProperty(key, value);
    }

    public void remove(int offs, int len) throws BadLocationException {
        delegate.remove(offs, len);
    }

    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
        delegate.insertString(offset, str, a);
    }

    public String getText(int offset, int length) throws BadLocationException {
        return delegate.getText(offset, length);
    }

    public void getText(int offset, int length, Segment txt)
            throws BadLocationException {
        delegate.getText(offset, length, txt);
    }

    public Position getStartPosition() {
        return delegate.getStartPosition();
    }

    public Position getEndPosition() {
        return delegate.getEndPosition();
    }

    public Position createPosition(int offs) throws BadLocationException {
        return delegate.createPosition(offs);
    }

    public Element[] getRootElements() {
        return delegate.getRootElements();
    }

    public Element getDefaultRootElement() {
        return delegate.getDefaultRootElement();
    }

    public void render(Runnable r) {
        delegate.render(r);
    }

}