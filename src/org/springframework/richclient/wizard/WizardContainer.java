/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.wizard;

/**
 * Interface for containers that can host a wizard. It displays wizard pages,
 * at most one of which is considered the current page. <code>getCurrentPage</code>
 * returns the current page; <code>showPage</code> programmatically changes
 * the the current page. Note that the pages need not all belong to the same
 * wizard.
 * <p>
 * The class <code>WizardDialog</code> provides a fully functional
 * implementation of this interface which will meet the needs of most clients.
 * However, clients are also free to implement this interface if <code>WizardDialog</code>
 * does not suit their needs.
 * </p>
 * <p>
 * Implementors are responsible for disposing of their wizards.
 * </p>
 */
public interface WizardContainer {

    /**
	 * Returns the current wizard page for this container.
	 * 
	 * @return the current wizard page, or <code>null</code> if the container
	 *         is not yet showing the wizard
	 * @see #showPage
	 */
    public WizardPage getCurrentPage();

    /**
	 * Makes the given page visible.
	 * <p>
	 * This method should not be use for normal page sequencing (back, next)
	 * which is handled by the container itself. It may, however, be used to
	 * move to another page in response to some custom action such as double
	 * clicking in a list.
	 * </p>
	 * 
	 * @param page
	 *            the page to show
	 * @see #getCurrentPage
	 */
    public void showPage(WizardPage page);
    
    /**
	 * Adjusts the enable state of the Back, Next, and Finish buttons to
	 * reflect the state of the currently active page in this container.
	 * <p>
	 * This method is called by the container itself when its wizard page
	 * changes and may be called by the page at other times to force a button
	 * state update.
	 * </p>
	 */
    public void updateButtons();
    
    /**
	 * Updates the message (or error message) shown in the message line to
	 * reflect the state of the currently active page in this container.
	 * <p>
	 * This method is called by the container itself when its wizard page
	 * changes and may be called by the page at other times to force a message
	 * update.
	 * </p>
	 */
    public void updateMessage();
    
    /**
	 * Updates the title bar (title, description, and image) to reflect the
	 * state of the currently active page in this container.
	 * <p>
	 * This method is called by the container itself when its wizard page
	 * changes and may be called by the page at other times to force a title
	 * bar update.
	 * </p>
	 */
    public void updateTitleBar();

}
