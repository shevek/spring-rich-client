/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.command.config;

import org.springframework.richclient.command.AbstractCommand;

/**
 * 
 * @author HP
 */
public interface CommandFaceDescriptorRegistry {
    public CommandFaceDescriptor getFaceDescriptor(AbstractCommand command,
            String faceDescriptorKey);

}