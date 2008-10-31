package org.springframework.richclient.samples.showcase.binding;

import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This dialog shows a ShuttleList in which several items can be selected and added to a list on the backing formObject.
 * Mind that this is far from perfect, but does illustrate the binding and the specific component that is created.
 *
 * @see org.springframework.richclient.components.ShuttleList
 * @see org.springframework.richclient.form.binding.swing.ShuttleListBinder
 */
public class ShuttleListBindingDialog extends TitledApplicationDialog {

    /** The fixed set of regions that can be selected. */
    private List<String> selectableRegions;

    public ShuttleListBindingDialog() {
        selectableRegions = new ArrayList<String>();
        selectableRegions.add("Land of the Dead");
        selectableRegions.add("My little Pony Heaven");
        selectableRegions.add("Bikini World");
        selectableRegions.add("Paradise City");
        selectableRegions.add("The Big Banana");
    }

    /** Value object with a list of regions that is a subselection of a given set. */
    private class Values {

        private List<String> selectedRegions;

        public List<String> getSelectedRegions() {
            return selectedRegions;
        }

        public void setSelectedRegions(List<String> selectedRegions) {
            this.selectedRegions = selectedRegions;
        }
    }

    private class ShuttleListForm extends AbstractForm {

        public ShuttleListForm() {
            super(FormModelHelper.createFormModel(new Values()));
        }

        @Override
        protected JComponent createFormControl() {
            SwingBindingFactory bindingFactory = new SwingBindingFactory(getFormModel());
            TableFormBuilder builder = new TableFormBuilder(bindingFactory);
            builder.setLabelAttributes("colGrId=label colSpec=left:pref rowSpec=top:pref");
            builder.add(bindingFactory.createBoundShuttleList("selectedRegions", selectableRegions));
            return builder.getForm();
        }

    }

    @Override
    protected JComponent createTitledDialogContentPane() {
        return (new ShuttleListForm()).getControl();
    }

    @Override
    protected boolean onFinish() {
        return true;
    }

}