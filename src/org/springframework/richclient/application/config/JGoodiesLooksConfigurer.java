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
package org.springframework.richclient.application.config;

import java.util.Map;

import org.springframework.util.Assert;

import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

public class JGoodiesLooksConfigurer {
    private UIManagerConfigurer configurer;

    public JGoodiesLooksConfigurer() {
        this(new UIManagerConfigurer());
    }

    public JGoodiesLooksConfigurer(UIManagerConfigurer configurer) {
        Assert.notNull(configurer);
        this.configurer = configurer;
    }

    public void setPlasticLookAndFeel(Map properties) {
        if (properties != null) {
            PlasticTheme theme = (PlasticTheme)properties.get("theme");
            if (theme != null) {
                PlasticLookAndFeel.setMyCurrentTheme(theme);
            }
            FontSizeHints fontSize = (FontSizeHints)properties.get("fontSizeHints");
            if (fontSize != null) {
                PlasticLookAndFeel.setFontSizeHints(fontSize);
            }
            Boolean threeDEnabled = (Boolean)properties.get("3dEnabled");
            if (threeDEnabled != null) {
                PlasticLookAndFeel.set3DEnabled(threeDEnabled.booleanValue());
            }
            Boolean highContrastEnabled = (Boolean)properties.get("highConstrastFocusColorsEnabled");
            if (highContrastEnabled != null) {
                PlasticLookAndFeel.setHighContrastFocusColorsEnabled(highContrastEnabled.booleanValue());
            }
            String tabStyle = (String)properties.get("tabStyle");
            if (tabStyle != null) {
                PlasticLookAndFeel.setTabStyle(tabStyle);
            }
        }
        configurer.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
    }

}