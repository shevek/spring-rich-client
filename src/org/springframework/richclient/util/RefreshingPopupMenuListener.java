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
package org.springframework.richclient.util;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.springframework.binding.value.support.RefreshableValueHolder;

/**
 * Popup menu listener that refreshes a value holder -- typically a holder
 * holding the list's contents -- when the popup is made visible.
 * 
 * @author Jim Moore
 */
public class RefreshingPopupMenuListener implements PopupMenuListener {
    private final RefreshableValueHolder itemsValueModel;

    public RefreshingPopupMenuListener(RefreshableValueHolder itemsValueModel) {
        this.itemsValueModel = itemsValueModel;
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        this.itemsValueModel.refresh();
    }
}

