/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.application;

import java.util.List;

import org.springframework.richclient.factory.ControlFactory;

/**
 * An <code>ApplicationPage</code> is a container for <code>PageComponent</code> s.
 */
public interface ApplicationPage extends ControlFactory {
    String getId();

    ApplicationWindow getWindow();

    void addPageComponentListener(PageComponentListener listener);

    void removePageComponentListener(PageComponentListener listener);

    PageComponent getActiveComponent();

    void setActiveComponent(PageComponent pageComponent);

    /**
     * Shows the {@link View} with the given id.
     * <p>
     * If the {@link View} is already opened, the view will be reused.
     * <p>
     * NOTE: this is NOT the same as calling <code>this.showView(id, null)</code>.
     * 
     * @param id
     *            the view id, cannot be empty
     * 
     * @return the {@link View} that is shown
     */
    View showView(String id);

    /**
     * Shows the {@link View} with the given id, and passes the input to the {@link View}, by calling
     * {@link View#setInput(Object)}.
     * <p>
     * If the {@link View} is already opened, the view will be reused.
     * 
     * @param id
     *            the view id, cannot be empty
     * @param input
     *            the input, can be <code>null</code>
     * 
     * @return the {@link View} that is shown
     */
    View showView(String id, Object input);

    /**
     * Returns the {@link View} with the given id. Returns <code>null</code> if no {@link View} with the given id is
     * shown.
     * <p>
     * This method is "generified" to avoid extra casts when calling this method:
     * 
     * <pre>
     * ApplicationPage page = ...; // get a reference to the ApplicationPage
     * InitialView initialView = page.getView(&quot;initialView&quot;);
     * </pre>
     * 
     * @param id
     *            the id, cannot be <code>null</code>
     * @return the {@link View}, or <code>null</code>
     */
    <T extends View> T getView(String id);

    void openEditor(Object editorInput);

    boolean closeAllEditors();

    boolean close();

    boolean close(PageComponent pageComponent);

    /**
     * Returns the list of {@link PageComponent}s on this {@link ApplicationPage}.
     * 
     * @return the page components
     */
    public List<PageComponent> getPageComponents();

}