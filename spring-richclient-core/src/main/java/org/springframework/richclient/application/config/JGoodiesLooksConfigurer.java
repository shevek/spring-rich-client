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

import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

/**
 * <p>JGoodies looks configurer bean. Allows to set various options available in the JGoodies looks library.
 * Use this as follows:</p>
 * <p/>
 * <pre>
 * &lt;bean id="lookAndFeelConfigurer"
 * class="org.springframework.richclient.application.config.JGoodiesLooksConfigurer"&gt;
 *  &lt;property name="popupDropShadowEnabled" value="false" /&gt;
 *  &lt;property name="theme"&gt;
 *      &lt;bean class="com.jgoodies.looks.plastic.theme.ExperienceBlue" /&gt;
 *  &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * <p/>
 * <p>Additionally the LaF FQN can be set using {@link #setLaFName(String)}. Use the static name constants defined in
 * JGoodies {@link com.jgoodies.looks.Options} to set this property. Default LaF if not specified is the Options#PLASTICXP_NAME.
 * It is possible to define other LaF's as well, but any other property set in this bean will not be in effect if used in that way.
 * If you do need to set another LaF without additional configuration use {@link org.springframework.richclient.application.config.UIManagerConfigurer}
 * and/or create a specific configurer bean for the LaF of your choice. 
 * </p>
 */
public class JGoodiesLooksConfigurer implements InitializingBean {

    private UIManagerConfigurer configurer;

    private String laFName = Options.PLASTICXP_NAME;

    /**
     * Default constructor.
     */
    public JGoodiesLooksConfigurer() {
        this(new UIManagerConfigurer());
    }

    /**
     * Constructor allowing to pass your own UIManagerConfigurer.
     *
     * @param configurer the UIManagerConfigurer to use when setting the LaF.
     */
    public JGoodiesLooksConfigurer(UIManagerConfigurer configurer) {
        Assert.notNull(configurer);
        this.configurer = configurer;
    }

    /**
     * @param size default Dimension for the icons.
     * @see com.jgoodies.looks.Options#setDefaultIconSize(java.awt.Dimension)
     */
    public void setDefaultIconSize(Dimension size) {
        Options.setDefaultIconSize(size);
    }

    /**
     * @param theme PlasticTheme to use.
     * @see com.jgoodies.looks.plastic.PlasticLookAndFeel#setPlasticTheme(com.jgoodies.looks.plastic.PlasticTheme)
     */
    public void setTheme(PlasticTheme theme) {
        PlasticLookAndFeel.setPlasticTheme(theme);
    }

    /**
     * @param enabled set to <code>true</true> if drop shadows should be used.
     * @see com.jgoodies.looks.Options#setPopupDropShadowEnabled(boolean)
     */
    public void setPopupDropShadowEnabled(boolean enabled) {
        Options.setPopupDropShadowEnabled(enabled);
    }

    /**
     * @param enabled set to <code>true</code> if tab icons should be enabled.
     * @see com.jgoodies.looks.Options#setTabIconsEnabled(boolean)
     */
    public void setTabIconsEnabled(boolean enabled) {
        Options.setTabIconsEnabled(enabled);
    }

    /**
     * @param enabled set to <code>true</code> if narrow buttons should be used.
     * @see com.jgoodies.looks.Options#setUseNarrowButtons(boolean)
     */
    public void setUseNarrowButtons(boolean enabled) {
        Options.setUseNarrowButtons(enabled);
    }

    /**
     * @param enabled set to <code>true</code> if narrow buttons should be used.
     * @see com.jgoodies.looks.Options#setUseSystemFonts(boolean)
     */
    public void setUseSystemFonts(boolean enabled) {
        Options.setUseSystemFonts(enabled);
    }

    /**
     * @param fontPolicy the font policy.
     * @see com.jgoodies.looks.plastic.PlasticLookAndFeel#setFontPolicy(com.jgoodies.looks.FontPolicy)
     */
    public void setFontSizeHints(FontPolicy fontPolicy) {
        PlasticLookAndFeel.setFontPolicy(fontPolicy);
    }

    /**
     * @param threeDEnabled set to <code>true</code> if 3D should be enabled.
     * @see com.jgoodies.looks.plastic.PlasticLookAndFeel#set3DEnabled(boolean)
     */
    public void set3DEnabled(boolean threeDEnabled) {
        PlasticLookAndFeel.set3DEnabled(threeDEnabled);
    }

    /**
     * @param highContrastEnabled set to <code>true</code> if high contrast should be enabled.
     * @see com.jgoodies.looks.plastic.PlasticLookAndFeel#setHighContrastFocusColorsEnabled(boolean)
     */
    public void setHighContrastFocusColorsEnabled(boolean highContrastEnabled) {
        PlasticLookAndFeel
                .setHighContrastFocusColorsEnabled(highContrastEnabled);
    }

    /**
     * @param tabStyle set the tab style that should be used.
     * @see com.jgoodies.looks.plastic.PlasticLookAndFeel#setTabStyle(String)   
     */
    public void setTabStyle(String tabStyle) {
        PlasticLookAndFeel.setTabStyle(tabStyle);
    }

    /**
     * <p>
     * Set the FQN of the LaF to use. This should be on of:
     * </p>
     * <ul>
     * <li>{@link com.jgoodies.looks.Options#PLASTIC_NAME</li>
     * <li>{@link com.jgoodies.looks.Options#PLASTIC3D_NAME</li>
     * <li>{@link com.jgoodies.looks.Options#PLASTICXP_NAME</li>
     * <li>{@link com.jgoodies.looks.Options#JGOODIES_WINDOWS_NAME</li>
     * <li>{@link com.jgoodies.looks.Options#DEFAULT_LOOK_NAME</li>
     * </ul>
     *
     * <p>Default LaF if not specified is Options#PLASTICXP_NAME. Note that you could mention any LaF FQN here, but all other options would then be ignored.</p> 
     *
     * @param laFName the FQN of the LaF you want to install on the UIManagerConfigurer.
     */
    public void setLaFName(String laFName) {
        this.laFName = laFName;
    }

    public void afterPropertiesSet() throws Exception {
        configurer.setLookAndFeel(laFName);
    }
}