package org.springframework.richclient.form.binding.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.list.TextValueListRenderer;
import org.springframework.util.Assert;

/**
 * Binds a Tiger enum in a combobox and supports i18n.<br/> The i18n key of an enum is the full classname + "." +
 * enumfield.<br/> For example:<br/> x.y.Season.WINTER = Winter<br/>
 * <p>
 * configuration happens like this:
 * </p>
 *
 * <pre>
 *   &lt;bean id=&quot;binderSelectionStrategy&quot;
 *           class=&quot;org.springframework.richclient.form.binding.swing.SwingBinderSelectionStrategy&quot;&gt;
 *       &lt;property name=&quot;bindersForPropertyTypes&quot;&gt;
 *           &lt;map&gt;
 *               &lt;entry&gt;
 *                   &lt;key&gt;
 *                       &lt;value type=&quot;java.lang.Class&quot;&gt;java.lang.Enum&lt;/value&gt;
 *                   &lt;/key&gt;
 *                   &lt;bean class=&quot;org.springframework.richclient.form.binding.swing.TigerEnumComboBoxBinder&quot; /&gt;
 *               &lt;/entry&gt;
 *           &lt;/map&gt;
 *       &lt;/property&gt;
 *   &lt;/bean&gt;
 * </pre>
 *
 * @author Geoffrey De Smet
 */
public class EnumComboBoxBinder extends ComboBoxBinder {

    public EnumComboBoxBinder() {
        super();
    }

    protected AbstractListBinding createListBinding(JComponent control, FormModel formModel, String formPropertyPath) {
        ComboBoxBinding binding = (ComboBoxBinding) super.createListBinding(control, formModel, formPropertyPath);
        binding.setSelectableItems(createEnumSelectableItems(formModel, formPropertyPath));
        MessageSourceAccessor messageSourceAccessor = getMessages();
        binding.setRenderer(new EnumListRenderer(messageSourceAccessor));
        binding.setEditor(new EnumComboBoxEditor(messageSourceAccessor, binding.getEditor()));
        return binding;
    }

    protected Enum[] createEnumSelectableItems(FormModel formModel, String formPropertyPath) {
        Class propertyType = getPropertyType(formModel, formPropertyPath);
        Class<Enum> enumPropertyType = propertyType;
        return enumPropertyType.getEnumConstants();
    }

    public class EnumListRenderer extends TextValueListRenderer {

        private MessageSourceAccessor messageSourceAccessor;

        public EnumListRenderer(MessageSourceAccessor messageSourceAccessor) {
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

    public class EnumComboBoxEditor implements ComboBoxEditor {

        private Object current;

        private MessageSourceAccessor messageSourceAccessor;

        private ComboBoxEditor inner;

        public EnumComboBoxEditor(MessageSourceAccessor messageSourceAccessor, ComboBoxEditor editor) {
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