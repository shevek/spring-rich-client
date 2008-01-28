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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.springframework.richclient.core.UIConstants;

/**
 * Utility functions to assist using the horridly complex Grid bag layout.
 * 
 * @author Keith Donald
 */
public class GridBagCellConstraints {

    public static Insets RIGHT_INSETS = new Insets(0, 0, 0, UIConstants.ONE_SPACE);

    public static Insets LEFT_INSETS = new Insets(0, UIConstants.ONE_SPACE, 0, 0);

    public static Insets TOP_INSETS = new Insets(UIConstants.ONE_SPACE, 0, 0, 0);

    public static Insets BOTTOM_INSETS = new Insets(0, 0, UIConstants.ONE_SPACE, 0);

    public static Insets TITLE_LABEL_INSETS = new Insets(0, 0, UIConstants.ONE_SPACE, UIConstants.ONE_SPACE);

    public static Insets RIGHT_INSETS_TWO_SPACES = new Insets(0, 0, 0, UIConstants.ONE_SPACE);

    public static Insets EVEN_INSETS = new Insets(UIConstants.ONE_SPACE, UIConstants.ONE_SPACE, UIConstants.ONE_SPACE,
            UIConstants.ONE_SPACE);

    public GridBagConstraints xy(int x, int y) {
        GridBagConstraints result = new GridBagConstraints();
        result.gridx = x;
        result.gridy = y;
        return result;
    }

    public GridBagConstraints xywh(int x, int y, int width, int height) {
        GridBagConstraints result = xy(x, y);
        result.gridheight = height;
        result.gridwidth = width;
        return result;
    }

    public GridBagConstraints xyf(int x, int y, int fill) {
        return xyfi(x, y, fill, null);
    }

    public GridBagConstraints xyfi(int x, int y, int fill, Insets insets) {
        GridBagConstraints result = xy(x, y);
        result.fill = fill;
        if (insets != null) {
            result.insets = insets;
        }
        switch (result.fill) {
        case GridBagConstraints.NONE: {
        }
            break;
        case GridBagConstraints.BOTH: {
            result.weightx = result.weighty = 1.0;
        }
            break;
        case GridBagConstraints.VERTICAL: {
            result.weighty = 1.0;
        }
            break;
        case GridBagConstraints.HORIZONTAL: {
            result.weightx = 1.0;
        }
            break;
        default: {
            result.fill = GridBagConstraints.NONE;
        }
            break;
        }
        return result;
    }

    public GridBagConstraints xya(int x, int y, int anchor) {
        GridBagConstraints result = xy(x, y);
        result.anchor = anchor;
        return result;
    }

    public GridBagConstraints xyaf(int x, int y, int anchor, int fill) {
        return xyaf(x, y, anchor, fill, null);
    }

    public GridBagConstraints xyaf(int x, int y, int anchor, int fill, Insets insets) {
        GridBagConstraints result = xyfi(x, y, fill, insets);
        result.anchor = anchor;
        return result;
    }

    public GridBagConstraints title(int x, int y) {
        GridBagConstraints result = xy(x, y);
        result.anchor = GridBagConstraints.WEST;
        result.insets = TITLE_LABEL_INSETS;
        return result;
    }

    public GridBagConstraints label(int x, int y) {
        return label(x, y, RIGHT_INSETS);
    }

    public GridBagConstraints label(int x, int y, Insets insets) {
        GridBagConstraints result = xy(x, y);
        result.anchor = GridBagConstraints.WEST;
        result.insets = insets;
        return result;
    }

    public GridBagConstraints textField(int x, int y) {
        return xyf(x, y, GridBagConstraints.HORIZONTAL);
    }

    public GridBagConstraints textField(int x, int y, Insets insets) {
        return xyfi(x, y, GridBagConstraints.HORIZONTAL, insets);
    }

}