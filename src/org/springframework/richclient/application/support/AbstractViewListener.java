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
package org.springframework.richclient.application.support;

import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewListener;

public abstract class AbstractViewListener implements ViewListener {
    private View activeView;

    protected View getActiveView() {
        return activeView;
    }

    public void viewActivated(View view) {
        this.activeView = view;
    }

    public void viewDeactivated(View view) {
        this.activeView = null;
    }

}