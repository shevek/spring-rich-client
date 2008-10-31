package org.springframework.richclient.form.builder;

import org.springframework.richclient.form.binding.BindingFactory;

/**
 * Testcase for <code>GridBagLayoutFormBuilder</code>.
 * 
 * @author Peter De Bruycker
 */
public class GridBagLayoutFormBuilderTests extends AbstractFormBuilderTestCase {

	protected AbstractFormBuilder createFormBuilder(BindingFactory bindingFactory) {
		return new GridBagLayoutFormBuilder(bindingFactory);
	}

}
