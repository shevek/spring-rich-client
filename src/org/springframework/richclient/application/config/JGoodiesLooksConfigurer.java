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

import java.awt.Dimension;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.jgoodies.looks.FontSizeHints;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

public class JGoodiesLooksConfigurer implements InitializingBean {
    private UIManagerConfigurer configurer;

    public JGoodiesLooksConfigurer() {
        this(new UIManagerConfigurer());
    }

    public JGoodiesLooksConfigurer(UIManagerConfigurer configurer) {
        Assert.notNull(configurer);
        this.configurer = configurer;
    }
    
    public void setDefaultIconSize(Dimension size) { 
        Options.setDefaultIconSize(size);
    }

    public void setTheme(PlasticTheme theme) {        
        PlasticLookAndFeel.setMyCurrentTheme(theme);
    }

    public void setFontSizeHints(FontSizeHints fontSize) {
        PlasticLookAndFeel.setFontSizeHints(fontSize);
    }

    public void set3DEnabled(boolean threeDEnabled) {
        PlasticLookAndFeel.set3DEnabled(threeDEnabled);
    }

    public void setHighContrastFocusColorsEnabled(boolean highContrastEnabled) {
        PlasticLookAndFeel.setHighContrastFocusColorsEnabled(highContrastEnabled);
    }

    public void setTabStyle(String tabStyle) {
        PlasticLookAndFeel.setTabStyle(tabStyle);
    }

    public void afterPropertiesSet() throws Exception {
        configurer.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
    }
}