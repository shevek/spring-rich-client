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
package org.springframework.richclient.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.rules.reporting.Severity;

import com.jgoodies.forms.factories.FormFactory;

/**
 * A container class that that has a title area for displaying a title and an
 * image as well as a common area for displaying a description, a message, or an
 * error message.
 */
public class TitleAreaPane extends AbstractControlFactory implements MessageAreaPane, TitleConfigurable,
        ImageConfigurable {

    /**
     * Image source key for banner image (value <code>dialog_title_banner</code>).
     */
    public static final String DEFAULT_TITLE_IMAGE = "titledDialog.icon";

    private JComponent titleArea;

    private JLabel titleLabel;

    private JLabel iconLabel;

    private Image image;

    private MessageAreaPane messageAreaPane;

    public TitleAreaPane() {
        this(DefaultMessageAreaPane.DEFAULT_LINES_TO_DISPLAY);
    }

    public TitleAreaPane(int linesToDisplay) {
        this.messageAreaPane = new DefaultMessageAreaPane(linesToDisplay, this);
    }

    public void setTitle(String newTitle) {
        if (newTitle == null) {
            newTitle = "";
        }
        createControlIfNecessary();
        titleLabel.setText(newTitle);
    }

    public void setImage(Image titleImage) {
        this.image = titleImage;
        if (isControlCreated()) {
            iconLabel.setIcon(getIcon());
        }
    }

    protected JComponent createControl() {
        titleLabel = new JLabel();
        titleLabel.setOpaque(false);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setText(" ");

        iconLabel = new JLabel();
        iconLabel.setBackground(getBackgroundColor());
        iconLabel.setIcon(getIcon());

        JPanel panel = new JPanel();
        panel.setBackground(getBackgroundColor());
        TableLayoutBuilder table = new TableLayoutBuilder(panel);
        table.row(FormFactory.LINE_GAP_ROWSPEC);
        table.gapCol();
        table.cell(titleLabel);
        table.gapCol();
        table.cell(iconLabel, "rowspan=2 colspec=pref");
        table.row(FormFactory.NARROW_LINE_GAP_ROWSPEC);
        table.cell(messageAreaPane.getControl());
        table.row(FormFactory.NARROW_LINE_GAP_ROWSPEC);
        return table.getPanel();
    }

    private Icon getIcon() {
        return new ImageIcon(getImage());
    }

    private Image getImage() {
        if (image != null) {
            return image;
        }
        else {
            return getImageSource().getImage(DEFAULT_TITLE_IMAGE);
        }
    }

    private Color getBackgroundColor() {
        Color c = UIManager.getLookAndFeel().getDefaults().getColor("primaryControlHighlight");
        if (c == null) {
            c = UIManager.getColor("controlLtHighlight");
        }
        return c;
    }

    public boolean messageShowing() {
        return messageAreaPane.messageShowing();
    }

    public void setMessage(String errorMessage, Severity severity) {
        messageAreaPane.setMessage(errorMessage, severity);
    }

    public void setErrorMessage(String errorMessage) {
        messageAreaPane.setErrorMessage(errorMessage);
    }

    public void setMessage(String newMessage) {
        messageAreaPane.setMessage(newMessage);
    }

    public void addMessageAreaChangeListener(MessageAreaChangeListener messageListener) {
        messageAreaPane.addMessageAreaChangeListener(messageListener);
    }

    public void removeMessageAreaChangeListener(MessageAreaChangeListener messageListener) {
        messageAreaPane.removeMessageAreaChangeListener(messageListener);
    }

}