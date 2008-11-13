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
package org.springframework.richclient.application.docking.flexdock;

import org.springframework.richclient.application.support.DefaultViewDescriptor;

/**
 * @author Peter De Bruycker
 */
public class FlexDockViewDescriptor extends DefaultViewDescriptor {
    private boolean closable;
    private boolean pinnable;
    private boolean dockable;

    public boolean isDockable() {
        return dockable;
    }

    public void setDockable( boolean dockable ) {
        this.dockable = dockable;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable( boolean closable ) {
        this.closable = closable;
    }

    public boolean isPinnable() {
        return pinnable;
    }

    public void setPinnable( boolean pinnable ) {
        this.pinnable = pinnable;
    }
}
