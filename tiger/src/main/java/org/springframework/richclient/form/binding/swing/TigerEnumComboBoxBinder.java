package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.ComboBoxBinder;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.list.TextValueListRenderer;
import org.springframework.util.Assert;

import javax.swing.ComboBoxEditor;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Binds a Tiger enum in a combobox and supports i18n.
 * The i18n key of an enum is the full classname + "." + enumfield.
 * For example:
 * x.y.Season.WINTER = Winter
 *
 * configuration happens like this:
 *
 *  &lt;bean id="binderSelectionStrategy"
 *          class="org.springframework.richclient.form.binding.swing.SwingBinderSelectionStrategy"&gt;
 *      &lt;property name="bindersForPropertyTypes"&gt;
 *          &lt;map&gt;
 *              &lt;entry&gt;
 *                  &lt;key&gt;
 *                      &lt;value type="java.lang.Class"&gt;java.lang.Enum&lt;/value&gt;
 *                  &lt;/key&gt;
 *                  &lt;bean class="be.kahosl.thot.swingui.util.TigerEnumComboBoxBinder" /&gt;
 *              &lt;/entry&gt;
 *          &lt;/map&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 *
 * @author Geoffrey De Smet
 */
public class TigerEnumComboBoxBinder extends ComboBoxBinder {

    protected TigerEnumComboBoxBinder() {
        super();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        ComboBoxBinding binding = (ComboBoxBinding) super.doBind(control, formModel, formPropertyPath, context);
        binding.setSelectableItemsHolder(createEnumSelectableItemsHolder(formModel, formPropertyPath));
        MessageSourceAccessor messageSourceAccessor = (MessageSourceAccessor)
                ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
        binding.setRenderer(new TigerEnumListRenderer(messageSourceAccessor));
        binding.setEditor(new TigerEnumComboBoxEditor(messageSourceAccessor, binding.getEditor()));
        return binding;
    }

    private ValueModel createEnumSelectableItemsHolder(FormModel formModel, String formPropertyPath) {
        Class propertyType = getPropertyType(formModel, formPropertyPath);
        Class<Enum> enumPropertyType = propertyType;
        Enum[] enumConstants = enumPropertyType.getEnumConstants();
        return new ValueHolder(enumConstants);
    }


    public class TigerEnumListRenderer extends TextValueListRenderer {

        private MessageSourceAccessor messageSourceAccessor;

        public TigerEnumListRenderer(MessageSourceAccessor messageSourceAccessor) {
            this.messageSourceAccessor = messageSourceAccessor;
        }

        protected String getTextValue(Object value) {
            if (value == null) {
                return "";
            }
            Enum valueEnum = (Enum) value;
            Class<? extends Enum> valueClass = valueEnum.getClass();
            return messageSourceAccessor.getMessage(valueClass.getName() + "." + valueEnum.name());
        }

    }

    public class TigerEnumComboBoxEditor implements ComboBoxEditor {

        private Object current;

        private MessageSourceAccessor messageSourceAccessor;

        private ComboBoxEditor inner;

        public TigerEnumComboBoxEditor(MessageSourceAccessor messageSourceAccessor, ComboBoxEditor editor) {
            Assert.notNull(editor, "Editor cannot be null");
            this.inner = editor;
            this.messageSourceAccessor = messageSourceAccessor;
        }

        public void selectAll() {
            inner.selectAll();
        }

        public Component getEditorComponent() {
            return inner.getEditorComponent();
        }

        public void addActionListener(ActionListener l) {
            inner.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            inner.removeActionListener(l);
        }

        public Object getItem() {
            return current;
        }

        public void setItem(Object value) {
            current = value;
            if (value != null) {
                Enum valueEnum = (Enum) value;
                Class<? extends Enum> valueClass = valueEnum.getClass();
                inner.setItem(messageSourceAccessor.getMessage(valueClass.getName() + "." + valueEnum.name()));
            } else {
                inner.setItem(null);
            }
        }
    }

}

