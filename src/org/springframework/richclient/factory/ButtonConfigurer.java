/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.AbstractButton;

/**
 * @author Keith Donald
 */
public interface ButtonConfigurer {
    public AbstractButton configure(AbstractButton button);
}