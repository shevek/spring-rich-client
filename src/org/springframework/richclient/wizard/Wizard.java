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

import java.awt.Image;

/**
 * Interface for a wizard. A wizard maintains a list of wizard pages, stacked on
 * top of each other in card layout fashion.
 * <p>
 * The class <code>AbstractWizard</code> provides an abstract implementation
 * of this interface. However, clients are also free to implement this interface
 * if <code>Wizard</code> does not suit their needs.
 * 
 * @author Keith Donald
 * @see Wizard
 */
public interface Wizard {
    
    /**
     * Returns this wizards name.
     * 
     * @return the name of this wizard
     */
    public String getWizardId();    

    /**
     * Adds any last-minute pages to this wizard.
     * <p>
     * This method is called just before the wizard becomes visible, to give the
     * wizard the opportunity to add any lazily created pages.
     * </p>
     */
    public void addPages();

    /**
     * Returns the default page image for this wizard.
     * <p>
     * This image can be used for pages which do not supply their own image.
     * </p>
     * 
     * @return the default page image
     */
    public Image getDefaultPageImage();

    /**
     * Returns the first page to be shown in this wizard.
     * 
     * @return the first wizard page
     */
    public WizardPage getStartingPage();

    /**
     * Returns the predecessor of the given page.
     * 
     * @param page
     *            the page
     * @return the previous page, or <code>null</code> if none
     */
    public WizardPage getPreviousPage(WizardPage page);

    /**
     * Returns the successor of the given page.
     * 
     * @param page
     *            the page
     * @return the next page, or <code>null</code> if none
     */
    public WizardPage getNextPage(WizardPage page);

    /**
     * Returns the wizard page with the given name belonging to this wizard.
     * 
     * @param pageName
     *            the name of the wizard page
     * @return the wizard page with the given name, or <code>null</code> if
     *         none
     */
    public WizardPage getPage(String pageName);

    /**
     * Returns the number of pages in this wizard.
     * 
     * @return the number of wizard pages
     */
    public int getPageCount();

    /**
     * Returns all the pages in this wizard.
     * 
     * @return a list of pages
     */
    public WizardPage[] getPages();

    /**
     * Returns the window title string for this wizard.
     * 
     * @return the window title string, or <code>null</code> for no title
     */
    public String getTitle();

    /**
     * Returns the container managing the display of this wizard. Generally a
     * dialog.
     * 
     * @return the wizard container
     */
    public WizardContainer getContainer();

    /**
     * Sets or clears the container of this wizard.
     * 
     * @param wizardContainer
     *            the wizard container, or <code>null</code>
     */
    public void setContainer(WizardContainer wizardContainer);

    /**
     * Returns whether this wizard needs Previous and Next buttons.
     * <p>
     * The result of this method is typically used by the container.
     * </p>
     * 
     * @return <code>true</code> if Previous and Next buttons are required,
     *         and <code>false</code> if none are needed
     */
    public boolean needsPreviousAndNextButtons();

    /**
     * Returns whether this wizard could be finished without further user
     * interaction.
     * <p>
     * The result of this method is typically used by the wizard container to
     * enable or disable the Finish button.
     * </p>
     * 
     * @return <code>true</code> if the wizard could be finished, and
     *         <code>false</code> otherwise
     */
    public boolean canFinish();

    /**
     * Performs any actions appropriate in response to the user having pressed
     * the Finish button, or refuse if finishing now is not permitted.
     * 
     * @return <code>true</code> to indicate the finish request was accepted,
     *         and <code>false</code> to indicate that the finish request was
     *         refused
     */
    public boolean performFinish();

    /**
     * Performs any actions appropriate in response to the user having pressed
     * the Cancel button, or refuse if canceling now is not permitted.
     * 
     * @return <code>true</code> to indicate the cancel request was accepted,
     *         and <code>false</code> to indicate that the cancel request was
     *         refused
     */
    public boolean performCancel();

    /**
     * Add a listener to this wizard
     */
    public void addWizardListener(WizardListener wizardListener);

    /**
     * Remove a listener to this wizard
     */
    public void removeWizardListener(WizardListener wizardListener);

}