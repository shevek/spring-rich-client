package org.springframework.richclient.form.builder;

import org.springframework.richclient.form.binding.BindingFactory;

/**
 * Testcase for <code>TableFormBuilder</code>.
 * 
 * @author Peter De Bruycker
 */
public class TableFormBuilderTests extends AbstractFormBuilderTestCase {

	protected AbstractFormBuilder createFormBuilder(BindingFactory bindingFactory) {
		return new TableFormBuilder(bindingFactory);
	}
}
