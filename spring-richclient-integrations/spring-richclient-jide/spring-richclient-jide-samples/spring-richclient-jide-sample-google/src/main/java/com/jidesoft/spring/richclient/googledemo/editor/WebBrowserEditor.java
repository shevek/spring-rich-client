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
package com.jidesoft.spring.richclient.googledemo.editor;

import com.google.soap.search.GoogleSearchResultElement;
import org.jdesktop.jdic.browser.WebBrowser;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.docking.jide.editor.AbstractEditor;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Editor that uses the JDIC project WebBrowser to display
 * an external web page in an embedded browser.
 * 
 * @author Jonny Wray
 *
 */
public class WebBrowserEditor extends AbstractEditor
{

	private static final String PRINT_COMMAND_ID = "printCommand";
	private static final String REFRESH_COMMAND_ID = "webBrowserEditor.refreshCommand";
	private RefreshCommandExecutor refreshCommandExecutor = new RefreshCommandExecutor();
	private PrintCommandExecutor printCommandExecutor = new PrintCommandExecutor();
	
	private GoogleSearchResultElement element;
	private JPanel control;
	private WebBrowser browser;
	
	public void initialize(Object editorObject){
		if(!(editorObject instanceof GoogleSearchResultElement)){
			throw new IllegalArgumentException("Editor object should be a GoogleSearchResultElement");
		}
		element = (GoogleSearchResultElement)editorObject;
	}
	
	public String getId() {
		return element.getURL();
	}

	public String getDisplayName() {
		return "<html>"+element.getTitle();
	}
	
    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register(REFRESH_COMMAND_ID, refreshCommandExecutor);
    	context.register(PRINT_COMMAND_ID, printCommandExecutor);
    }
	
	public JComponent getControl() {
		if(control == null){
			control = new JPanel(new BorderLayout());
			control.setBackground(Color.WHITE);
			control.add(getUrlPanel(), BorderLayout.NORTH);
			try{
				JPanel browserPanel = new JPanel(new BorderLayout());
				browser = new WebBrowser(new URL(element.getURL()));
				browserPanel.add(browser, BorderLayout.CENTER);
				control.add(browserPanel, BorderLayout.CENTER);
			}
			catch(MalformedURLException e){
				control.add(new JLabel("Unable to open browser for url "+element.getURL()),
						BorderLayout.CENTER);
			}
		}
		return control;
	}
	
	
	private JComponent getUrlPanel(){
		JTextField urlComponent = new JTextField(" URL: "+element.getURL());
		urlComponent.setBackground(Color.WHITE);
		urlComponent.setBorder(BorderFactory.createLoweredBevelBorder());
		urlComponent.setEditable(false);
		return urlComponent;	
	}
	
    // Disable saving by always returning false for being dirty
	public boolean isDirty() {
		return false;
	}

	private class PrintCommandExecutor extends AbstractActionCommandExecutor{

        public boolean isEnabled() {
			return true;
		}

		public void execute() {
			// Newer version of org.jdesktop.jdic.browser.WebBrowser has this method.
			//browser.print();
			String message = "I'm sorry, printing is not supported with this version of JDIC";
			JOptionPane.showMessageDialog(null, message, 
					"Printing Information", JOptionPane.INFORMATION_MESSAGE);
        }
	}
	
    private class RefreshCommandExecutor extends AbstractActionCommandExecutor {
    	
        public boolean isEnabled() {
			return true;
		}

		public void execute() {
			browser.refresh();
        }
    }
}
