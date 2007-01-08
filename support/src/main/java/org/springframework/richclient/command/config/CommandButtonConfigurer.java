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

/**
 * A configurer for Swing buttons that have an associated command.
 * 
 * @author Keith Donald
 */
public interface CommandButtonConfigurer {
    
    /**
     * Configures the given button and optional command object with the properties of the given 
     * descriptor. 
     *
     * @param button The button to be configured. Must not be null.
     * @param command The command to be configured. May be null.
     * @param faceDescriptor The object describing the visual properties of the command button.
     * Must not be null.
     * 
     * @throws IllegalArgumentException if {@code button} or {@code faceDescriptor} are null.
     */
    public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor);
    
}
