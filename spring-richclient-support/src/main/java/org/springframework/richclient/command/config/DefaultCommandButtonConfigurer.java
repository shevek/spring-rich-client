/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.config;

import javax.swing.AbstractButton;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.util.Assert;

/**
 * The default implementation of the {@link CommandButtonConfigurer} interface. This implementation
 * ignores the command object and only configures the button.
 * 
 * @author Keith Donald
 */
public class DefaultCommandButtonConfigurer implements CommandButtonConfigurer {

    /**
     * {@inheritDoc}
     */
    public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
        Assert.notNull(button, "The button to configure cannot be null.");
        Assert.notNull(faceDescriptor, "The command face descriptor cannot be null.");
        faceDescriptor.configureLabel(button);
        faceDescriptor.configureIcon(button);
        faceDescriptor.configureColor(button);
        button.setToolTipText(faceDescriptor.getCaption());
    }
    
}
