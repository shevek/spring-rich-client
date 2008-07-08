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
package org.springframework.richclient.application.mdi;

import org.springframework.richclient.application.support.DefaultViewDescriptor;

/**
 * @author Peter De Bruycker
 */
public class DesktopViewDescriptor extends DefaultViewDescriptor {
    private boolean resizable = true;
    private boolean maximizable = true;
    private boolean iconifiable = true;
    private boolean closable = true;

    public boolean isClosable() {
        return closable;
    }

    public void setClosable( boolean closable ) {
        this.closable = closable;
    }

    public boolean isIconifiable() {
        return iconifiable;
    }

    public void setIconifiable( boolean iconifiable ) {
        this.iconifiable = iconifiable;
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public void setMaximizable( boolean maximizable ) {
        this.maximizable = maximizable;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable( boolean resizable ) {
        this.resizable = resizable;
    }
}
