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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.springframework.richclient.application.ApplicationServicesAccessorSupport;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.forms.Form;

/**
 * Helper implementation of the wizard interface.
 * 
 * @author keith
 */
public abstract class AbstractWizard extends ApplicationServicesAccessorSupport
        implements Wizard, TitleConfigurable {
    public static final String DEFAULT_IMAGE_KEY = "wizard.pageIcon";

    private String wizardId;

    private String title;

    private boolean forcePreviousAndNextButtons;

    private List pages = new ArrayList(6);

    private WizardContainer container;

    private EventListenerList listeners = new EventListenerList();

    private boolean autoConfigureChildPages = true;

    public AbstractWizard() {
        this(null);
    }

    public AbstractWizard(String wizardId) {
        this.wizardId = wizardId;
    }

    /**
     * Returns this wizards name.
     * 
     * @return the name of this wizard
     */
    public String getId() {
        return wizardId;
    }

    public void setAutoConfigureChildPages(boolean autoConfigure) {
        this.autoConfigureChildPages = autoConfigure;
    }

    /**
     * Controls whether the wizard needs Previous and Next buttons even if it
     * currently contains only one page.
     * <p>
     * This flag should be set on wizards where the first wizard page adds
     * follow-on wizard pages based on user input.
     * </p>
     * 
     * @param b
     *            <code>true</code> to always show Next and Previous buttons,
     *            and <code>false</code> to suppress Next and Previous buttons
     *            for single page wizards
     */
    public void setForcePreviousAndNextButtons(boolean b) {
        this.forcePreviousAndNextButtons = b;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Sets the window title for the container that hosts this page to the given
     * string.
     * 
     * @param newTitle
     *            the window title for the container
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * @return Returns the container.
     */
    public WizardContainer getContainer() {
        return container;
    }

    /**
     * @param container
     *            the container to set
     */
    public void setContainer(WizardContainer container) {
        this.container = container;
    }

    /**
     * Adds a new page to this wizard. The page is inserted at the end of the
     * page list.
     * 
     * @param page
     *            the new page
     */
    public void addPage(WizardPage page) {
        addPage(getId(), page);
    }

    /**
     * Adds a new page to this wizard. The page is inserted at the end of the
     * page list.
     * 
     * @param wizardConfigurationKey
     *            the parent configuration key of the page, used for
     *            configuration, by default this wizard's id *
     * @param page
     *            the new page
     */
    protected void addPage(String wizardConfigurationKey, WizardPage page) {
        pages.add(page);
        page.setWizard(this);
        if (autoConfigureChildPages) {
            String key = ((wizardConfigurationKey != null) ? wizardConfigurationKey
                    + "."
                    : "")
                    + page.getId();
            getObjectConfigurer().configure(page, key);
        }
    }

    /**
     * Adds a new page to this wizard. The page is created by wrapping the form
     * page in a FormBackedWizardPage and is inserted at the end of the page
     * list.
     * 
     * @param formPage
     *            the form page to be insterted
     * @return the WizardPage that wraps formPage
     */
    public WizardPage addForm(Form formPage) {
        WizardPage page = new FormBackedWizardPage(formPage,
                !autoConfigureChildPages);
        addPage(page);
        return page;
    }

    /**
     * Removes a page from this wizard.
     * 
     * @param page
     *            the page
     */
    public void removePage(WizardPage page) {
        if (pages.remove(page)) {
            page.setWizard(null);
        }
    }

    /**
     * The <code>Wizard</code> implementation of this <code>Wizard</code>
     * method does nothing. Subclasses should extend if extra pages need to be
     * added before the wizard opens. New pages should be added by calling
     * <code>addPage</code>.
     */
    public void addPages() {
    }

    public boolean canFinish() {
        // Default implementation is to check if all pages are complete.
        for (int i = 0; i < pages.size(); i++) {
            if (!((WizardPage)pages.get(i)).isPageComplete())
                return false;
        }
        return true;
    }

    public Image getDefaultPageImage() {
        return getImageSource().getImage(DEFAULT_IMAGE_KEY);
    }

    public WizardPage getNextPage(WizardPage page) {
        int index = pages.indexOf(page);
        if (index == pages.size() - 1 || index == -1) {
            // last page or page not found
            return null;
        }
        return (WizardPage)pages.get(index + 1);
    }

    public WizardPage getPage(String pageId) {
        Iterator it = pages.iterator();
        while (it.hasNext()) {
            WizardPage page = (WizardPage)it.next();
            if (page.getId().equals(pageId)) { return page; }
        }
        return null;
    }

    public int getPageCount() {
        return pages.size();
    }

    public WizardPage[] getPages() {
        return (WizardPage[])pages.toArray(new WizardPage[pages.size()]);
    }

    public WizardPage getPreviousPage(WizardPage page) {
        int index = pages.indexOf(page);
        if (index == 0 || index == -1) {
            // first page or page not found
            return null;
        }
        else {
            logger.debug("Returning previous page...");
            return (WizardPage)pages.get(index - 1);
        }
    }

    public WizardPage getStartingPage() {
        if (pages.size() == 0) { return null; }
        return (WizardPage)pages.get(0);
    }

    public boolean needsPreviousAndNextButtons() {
        return forcePreviousAndNextButtons || pages.size() > 1;
    }

    public void addWizardListener(WizardListener wizardListener) {
        listeners.add(WizardListener.class, wizardListener);
    }

    public void removeWizardListener(WizardListener wizardListener) {
        listeners.remove(WizardListener.class, wizardListener);
    }

    /**
     * Fires a onPerformFinish event to all listeners.
     */
    protected void fireFinishedPerformed(boolean result) {
        Object[] array = listeners.getListenerList();
        for (int i = 0; i < array.length; i++) {
            WizardListener listener = (WizardListener)array[i];
            try {
                listener.onPerformFinish(this, result);
            }
            catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * Fires a onPerformCancel event to all listeners.
     */
    protected void fireCancelPerformed(boolean result) {
        Object[] array = listeners.getListenerList();
        for (int i = 0; i < array.length; i++) {
            WizardListener listener = (WizardListener)array[i];
            try {
                listener.onPerformCancel(this, result);
            }
            catch (Throwable t) {
                logger.error(t);
            }
        }
    }

    /**
     * This method invokes the performFinishImpl method and then fires
     * appropraiate events to any wizard listeners listening to this wizard.
     */
    public boolean performFinish() {
        boolean result = onFinish();
        fireFinishedPerformed(result);
        return result;
    }

    /**
     * This method invokes the performFinishImpl method and then fires
     * appropraiate events to any wizard listeners listening to this wizard.
     */
    public boolean performCancel() {
        boolean result = onCancel();
        fireCancelPerformed(result);
        return result;
    }

    /**
     * Subclasses should implement this method to do processing on finish.
     */
    protected abstract boolean onFinish();

    /**
     * Subclasses should implement this method to do processing on cancellation.
     */
    protected boolean onCancel() {
        return true;
    }

}