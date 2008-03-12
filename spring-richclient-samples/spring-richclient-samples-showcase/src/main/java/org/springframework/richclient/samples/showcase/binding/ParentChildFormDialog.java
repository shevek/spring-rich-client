package org.springframework.richclient.samples.showcase.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.samples.showcase.util.AbstractReporterForm;
import org.springframework.richclient.samples.showcase.util.AbstractReporterTitledApplicationDialog;
import org.springframework.richclient.samples.showcase.util.Reporter;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.factory.Constraints;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ParentChildFormDialog extends AbstractReporterTitledApplicationDialog {

	private class ChildPOJO implements PropertyConstraintProvider {
		private String childName;

		private String childDescription;

		private Map<String, PropertyConstraint> propertyConstraints;

		public ChildPOJO() {
			propertyConstraints = new HashMap<String, PropertyConstraint>(1);
			propertyConstraints.put("childName", Constraints.instance().required("childName"));
		}

		public String getChildName() {
			return childName;
		}

		public void setChildName(String childName) {
			this.childName = childName;
		}

		public String getChildDescription() {
			return childDescription;
		}

		public void setChildDescription(String childDescription) {
			this.childDescription = childDescription;
		}

		@Override
		public String toString() {
			return "childName = " + childName + ", childDescription = " + childDescription;
		}

		public PropertyConstraint getPropertyConstraint(String propertyName) {
			return propertyConstraints.get(propertyName);
		}
	}

	private class ParentPOJO implements PropertyConstraintProvider {
		private String parentName;

		private String parentDescription;

		private Map<String, PropertyConstraint> propertyConstraints;

		public ParentPOJO() {
			propertyConstraints = new HashMap<String, PropertyConstraint>(1);
			propertyConstraints.put("parentName", Constraints.instance().required("parentName"));
		}

		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
		}

		public String getParentDescription() {
			return parentDescription;
		}

		public void setParentDescription(String parentDescription) {
			this.parentDescription = parentDescription;
		}

		@Override
		public String toString() {
			return "parentName = " + parentName + ", parentDescription = " + parentDescription;
		}

		public PropertyConstraint getPropertyConstraint(String propertyName) {
			return propertyConstraints.get(propertyName);
		}
	}

	private class ChildForm extends AbstractReporterForm {

		public ChildForm() {
			super(FormModelHelper.createFormModel(new ChildPOJO(), "child"), "child");
		}

		@Override
		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("childName");
			builder.row();
			builder.add("childDescription");
			return builder.getForm();
		}

	}

	private class ParentForm extends AbstractReporterForm {

		private ChildForm childForm;

		public ParentForm() {
			super(FormModelHelper.createFormModel(new ParentPOJO(), "parent"), "parent");
		}

		@Override
		protected JComponent createFormControl() {
			TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
			builder.add("parentName");
			builder.row();
			builder.add("parentDescription");
			JPanel panel = new JPanel(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC }, new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
					FormFactory.UNRELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, FormFactory.UNRELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
					FormFactory.UNRELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));
			CellConstraints cc = new CellConstraints();
			panel.add(new JLabel(getMessage("parentForm.label")), cc.xy(1, 1));
			panel.add(builder.getForm(), cc.xy(3, 3));
			CommandGroup parentFormcommandGroup = CommandGroup.createCommandGroup(new ActionCommand[] {
					getEnableFormModelCommand(), getReadOnlyFormModelCommand(), getValidatingFormModelCommand() });
			panel.add(parentFormcommandGroup.createButtonBar(), cc.xy(3, 5));
			panel.add(new JLabel(getMessage("childForm.label")), cc.xy(1, 7));
			childForm = new ChildForm();
			childForm.setMessageArea(getMessageArea());
			panel.add(childForm.getControl(), cc.xy(3, 9));
			CommandGroup childFormcommandGroup = CommandGroup.createCommandGroup(new ActionCommand[] {
					childForm.getEnableFormModelCommand(), childForm.getReadOnlyFormModelCommand(),
					childForm.getValidatingFormModelCommand() });
			panel.add(childFormcommandGroup.createButtonBar(), cc.xy(3, 11));
			addChildForm(childForm);
			newSingleLineResultsReporter(ParentChildFormDialog.this);
			return panel;
		}

		@Override
		public StringBuilder getFieldsDetails(StringBuilder builder, FormModel formModel) {
			builder = super.getFieldsDetails(builder, formModel);
			return super.getFieldsDetails(builder, childForm.getFormModel());
		}

		@Override
		public StringBuilder getFormObjectDetails(StringBuilder builder, FormModel formModel) {
			builder = super.getFormObjectDetails(builder, formModel);
			return super.getFormObjectDetails(builder, childForm.getFormModel());
		}

		@Override
		public StringBuilder getFormModelDetails(StringBuilder builder, FormModel formModel) {
			builder = super.getFormModelDetails(builder, formModel);
			return super.getFormModelDetails(builder, childForm.getFormModel());
		}

		@Override
		public void registerFormModelPropertyChangeListener() {
			childForm.registerFormModelPropertyChangeListener();
			super.registerFormModelPropertyChangeListener();
		}

		@Override
		public void unregisterFormModelPropertyChangeListener() {
			childForm.unregisterFormModelPropertyChangeListener();
			super.unregisterFormModelPropertyChangeListener();
		}
	}

	@Override
	protected Reporter getReporter() {
		return new ParentForm();
	}

}
