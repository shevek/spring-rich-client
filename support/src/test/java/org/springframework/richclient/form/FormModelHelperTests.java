package org.springframework.richclient.form;

import junit.framework.TestCase;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.form.support.DefaultFormModel;

/**
 * Unit tests for {@link FormModelHelper}
 */
public class FormModelHelperTests extends TestCase {

    public void testGetChild() throws Exception {
        // test the edge conditions first
        try {
            FormModelHelper.getChild(null, "");
            fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException exp) {
            // should have happened
        }

        try {
            FormModelHelper.getChild(new DefaultFormModel(), null);
            fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException exp) {
            // should have happened
        }

        HierarchicalFormModel parentModel = new DefaultFormModel();
        assertNull(FormModelHelper.getChild(parentModel, "testChildName"));

        final ValidatingFormModel childFormModel = FormModelHelper.createFormModel(new Object(), "testChildName");
        parentModel.addChild(childFormModel);

        assertNotNull(FormModelHelper.getChild(parentModel, "testChildName"));
        assertNull(FormModelHelper.getChild(parentModel, "bogusChildName"));
    }

}
