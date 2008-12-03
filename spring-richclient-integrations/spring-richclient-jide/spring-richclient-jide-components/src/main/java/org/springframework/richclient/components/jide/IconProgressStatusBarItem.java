/*
 * Copyright 2005 the original author or authors.
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
package org.springframework.richclient.components.jide;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.image.IconSource;

import com.jidesoft.status.ProgressStatusBarItem;
import com.jidesoft.swing.JideButton;

/**
 * Extension of the ProgressStatusBarItem that uses an icon instead of
 * the default textual button for cancel. Uses 'statusbar.cancel' to
 * obtain the Icon and 'statusbar.tooltip.cancel' to get the tooltip for the
 * cancel button.
 * 
 * @author Jonny Wray
 *
 */
public class IconProgressStatusBarItem extends ProgressStatusBarItem {

	private static final String CANCEL_ICON = "statusbar.cancel";
	private static final String CANCEL_TOOLTIP = "statusbar.tooltip.cancel";
	
	
	
	public IconProgressStatusBarItem(){
	}

	public AbstractButton createCancelButton(){
		IconSource iconSource = (IconSource)ApplicationServicesLocator.services().getService(IconSource.class);
		MessageSource messageSource = (MessageSource)ApplicationServicesLocator.services().getService(MessageSource.class);
		AbstractAction action = new AbstractAction(null, iconSource.getIcon(CANCEL_ICON)){
			public void actionPerformed(ActionEvent event) {
				CancelCallback callback = getCancelCallback();
				if(callback != null){
					callback.cancelPerformed();
				}
			}
		};
		AbstractButton button = new JideButton(action);
		button.setToolTipText(messageSource.getMessage(CANCEL_TOOLTIP, new Object[]{}, Locale.getDefault()));
		return button;
	}
}
