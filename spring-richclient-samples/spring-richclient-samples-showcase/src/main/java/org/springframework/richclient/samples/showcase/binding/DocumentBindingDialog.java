package org.springframework.richclient.samples.showcase.binding;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.TextComponentBinder;
import org.springframework.richclient.form.binding.swing.text.RegExDocumentFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

import javax.swing.*;
import java.util.Collections;

/**
 * This dialog shows how the {@link org.springframework.richclient.form.binding.swing.TextComponentBinder} can be used to create
 * a binding with a specific {@link javax.swing.text.Document} as model behind the {@link javax.swing.JTextField}.
 *
 * @author Jan Hoskens
 */
public class DocumentBindingDialog extends TitledApplicationDialog {
    
    class StringValues {

        String regExp = "[0-9A-z]";

        boolean convertToUppercase = false;

        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRegExp() {
            return regExp;
        }

        public void setRegExp(String regExp) {
            this.regExp = regExp;
        }

        public boolean isConvertToUppercase() {
            return convertToUppercase;
        }

        public void setConvertToUppercase(boolean convertToUppercase) {
            this.convertToUppercase = convertToUppercase;
        }

    }

    private class ParentForm extends AbstractForm {
        private final JPanel childPanel = new JPanel(new FormLayout("fill:default:grow","fill:default:grow"));

        public ParentForm() {
			super(FormModelHelper.createFormModel(new StringValues()));
		}

		@Override
		protected JComponent createFormControl() {
            JPanel panel = new JPanel(new FormLayout("fill:default:grow","default, 4dlu, default, 4dlu, 30dlu:grow"));
            TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
            builder.add("regExp");
            builder.row();
            builder.add("convertToUppercase");
            CellConstraints cc = new CellConstraints();
            panel.add(builder.getForm(), cc.xy(1,1));
            panel.add(createBuildBindingCommand().createButton(), cc.xy(1,3));
            panel.add(childPanel, cc.xy(1,5));
            return panel;
		}

        private ActionCommand createBuildBindingCommand() {
            ActionCommand actionCommand = new ActionCommand("buildBinding"){
                protected void doExecuteCommand() {
                    String pattern = (String)getValue("regExp");
                    boolean upperCaseOnly = (Boolean) getValue("convertToUppercase");
                    CellConstraints cc = new CellConstraints();
                    ChildForm form = new ChildForm(pattern, upperCaseOnly);
                    childPanel.removeAll();
                    childPanel.add(form.getControl(), cc.xy(1,1));
                    childPanel.revalidate();

                }
            };
            ((CommandConfigurer) ApplicationServicesLocator.services().getService(CommandConfigurer.class))
                    .configure(actionCommand);
            return actionCommand;
        }

        private class ChildForm extends AbstractForm {

            final String pattern;

            final boolean upperCaseOnly;

            public ChildForm(String pattern, boolean  upperCaseOnly) {
                super(FormModelHelper.createFormModel(new StringValues()));
                this.pattern = pattern;
                this.upperCaseOnly = upperCaseOnly;
            }

            protected JComponent createFormControl() {
                TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
                TextComponentBinder binder = new TextComponentBinder();
                binder.setDocumentFactory(new RegExDocumentFactory(pattern, upperCaseOnly));
                builder.add(binder.bind(getFormModel(), "value", Collections.emptyMap()), "rowSpec=fill:default:grow");
                return builder.getForm();
            }
        }
    }

	@Override
	protected JComponent createTitledDialogContentPane() {
		return (new ParentForm()).getControl();
	}

	@Override
	protected boolean onFinish() {
		return true;
	}
}
