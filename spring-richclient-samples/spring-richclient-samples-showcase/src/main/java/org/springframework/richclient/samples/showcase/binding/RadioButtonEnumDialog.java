package org.springframework.richclient.samples.showcase.binding;

import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.swing.EnumRadioButtonBinder;
import org.springframework.richclient.samples.showcase.util.AbstractReporterForm;
import org.springframework.richclient.samples.showcase.util.AbstractReporterTitledApplicationDialog;
import org.springframework.richclient.samples.showcase.util.Reporter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RadioButtonEnumDialog extends AbstractReporterTitledApplicationDialog {

    public static enum SmallEnum {
        CHOICE_1, CHOICE_2, CHOICE_3;
    }

    public class EnumContainer {
        private SmallEnum smallEnum = SmallEnum.CHOICE_1;

        private SmallEnum nullableEnum = null;

        public SmallEnum getSmallEnum() {
            return smallEnum;
        }

        public void setSmallEnum(SmallEnum smallEnum) {
            this.smallEnum = smallEnum;
        }

        public SmallEnum getNullableEnum() {
            return nullableEnum;
        }

        public void setNullableEnum(SmallEnum nullableEnum) {
            this.nullableEnum = nullableEnum;
        }
    }

    private class RadioButtonEnumForm extends AbstractReporterForm {

        public RadioButtonEnumForm() {
            super(FormModelHelper.createFormModel(new EnumContainer()));
        }

        @Override
        protected JComponent createFormControl() {
            JPanel panel = new JPanel(new FormLayout("center:pref", "pref,3dlu,pref,3dlu,pref,3dlu, pref"));
            CellConstraints cc = new CellConstraints();

            panel.add(new JLabel(getMessage("radioButtonEnumForm.smallEnum.label")), cc.xy(1, 1));
            EnumRadioButtonBinder binder = new EnumRadioButtonBinder();
            Binding binding = binder.bind(getFormModel(), "smallEnum", Collections.EMPTY_MAP);
            panel.add(binding.getControl(), cc.xy(1, 3));

            panel.add(new JLabel(getMessage("radioButtonEnumForm.nullableEnum.label")), cc.xy(1, 5));
            binder = new EnumRadioButtonBinder();
            binder.setNullable(true);
            binding = binder.bind(getFormModel(), "nullableEnum", Collections.EMPTY_MAP);
            panel.add(binding.getControl(), cc.xy(1, 7));
            return panel;
        }

    }

    @Override
    protected Reporter getReporter() {
        return new RadioButtonEnumForm();
    }

}
