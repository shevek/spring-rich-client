/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.util;

import javax.swing.SwingConstants;

import org.springframework.enums.ShortCodedEnum;

/**
 * 
 * @author Keith Donald
 */
public class Alignment extends ShortCodedEnum {
    public static final Alignment LEFT = new Alignment(SwingConstants.LEFT,
            "Left");

    public static final Alignment RIGHT = new Alignment(SwingConstants.RIGHT,
            "Right");

    private Alignment(int code, String label) {
        super(code, label);
    }
}