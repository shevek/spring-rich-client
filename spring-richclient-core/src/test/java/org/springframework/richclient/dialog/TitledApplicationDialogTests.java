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
package org.springframework.richclient.dialog;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Testcase for TitledApplicationDialog
 * 
 * @author Peter De Bruycker
 */
public class TitledApplicationDialogTests extends TitledApplicationDialogTestCase {
	protected TitledApplicationDialog createTitledApplicationDialog(final Runnable onAboutToShow) {
		return new TitledApplicationDialog() {
			protected JComponent createTitledDialogContentPane() {
				return new JLabel("content");
			}

			protected boolean onFinish() {
				return true;
			}

			protected void onAboutToShow() {
				onAboutToShow.run();
			}
		};
	}
}
