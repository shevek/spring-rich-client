/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.table;

import java.util.Date;

import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.test.TestBean;

/**
 * Testcase for BeanTableModel
 * 
 * @author Peter De Bruycker
 */
public class BeanTableModelTests extends AbstractBaseTableModelTests {

	public void testConstructorWithoutMessageSource() {
		BeanTableModel beanTableModel = new BeanTableModel(TestBean.class) {

			protected String[] createColumnPropertyNames() {
				return new String[] { "stringProperty", "dateProperty" };
			}

			protected Class[] createColumnClasses() {
				return new Class[] { String.class, Date.class };
			}
		};

		try {
			beanTableModel.setRowNumbers(false);
			fail("Must throw IllegalStateException: no messagesource set");
		}
		catch (IllegalStateException e) {
			// test passes
		}

		StaticMessageSource messageSource = new StaticMessageSource();
		beanTableModel.setMessageSource(messageSource);

		beanTableModel.setRowNumbers(false);
	}

	protected BaseTableModel getBaseTableModel() {
		return new BeanTableModel(TestBean.class, new StaticMessageSource()) {

			protected String[] createColumnPropertyNames() {
				return new String[] { "stringProperty", "dateProperty" };
			}

			protected Class[] createColumnClasses() {
				return new Class[] { String.class, Date.class };
			}
		};
	}
}
