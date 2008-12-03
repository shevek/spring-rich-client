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
package com.jidesoft.spring.richclient.googledemo.view;

import com.google.soap.search.GoogleSearchResultElement;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultsSelectionEvent;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.docking.jide.view.JideAbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.text.HtmlPane;

import javax.swing.*;
import java.awt.*;

/**
 * View to display properties via the JIDE property pane component
 * 
 * @author Jonny Wray
 *
 */
public class DetailsView extends JideAbstractView implements ApplicationListener{
	private JLabel title = new JLabel(" ");
	private StyledLabel url = new StyledLabel(" ");
	private HtmlPane snippit = new HtmlPane();
    private static final Color GOOGLE_GREEN = new Color(40, 180, 40);
	
    private PrintCommandExecutor printCommandExecutor = new PrintCommandExecutor();
	private static final String PRINT_COMMAND_ID = "printCommand";
    
    public DetailsView(){	
    }
    	

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register(PRINT_COMMAND_ID, printCommandExecutor);
    }
    
    protected JComponent createControl() {
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.setBackground(Color.WHITE);
    	
    	snippit.setEditable(false);
    	snippit.setAntiAlias(true);
    	JScrollPane scrollPane = new JScrollPane(snippit,
    			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
    			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	url.setStyleRanges(new StyleRange[]{new StyleRange(GOOGLE_GREEN)});
    	
    	panel.add(title, BorderLayout.NORTH);
    	panel.add(scrollPane, BorderLayout.CENTER);
    	panel.add(url, BorderLayout.SOUTH);
    	return panel;
    }

	/**
	 * Listens for search results selection event. These events should occur on the EDT
	 * but I check to make sure.
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof SearchResultsSelectionEvent){
			final SearchResultsSelectionEvent selectionEvent = (SearchResultsSelectionEvent)event;
			if(SwingUtilities.isEventDispatchThread()){
				updateView(selectionEvent);
			}
			else{
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						updateView(selectionEvent);
					}
				});
			}
		}
	}
	
	private void updateView(SearchResultsSelectionEvent selectionEvent){
		GoogleSearchResultElement element = selectionEvent.getSearchResult();
		title.setText(getTitleHtmlText(element.getTitle()));
		snippit.setText(wrapInHtmlTags(element.getSnippet()));
		String urlText = element.getURL() + " - " + element.getCachedSize();
		url.setText(urlText);
		url.setToolTipText(urlText);
	}
	
	/*
	 * Use Swing's HTML capabilities, as opposed to
	 * the JIDE StyledLabel to style the title
	 * since the google title string is returned
	 * as HTML.
	 */
	private String getTitleHtmlText(String base){
		StringBuffer builder = new StringBuffer();
		builder.append("<html>");
		builder.append("<h3 style=\"color: blue;\">");
		builder.append(base);
		builder.append("</h3>");
		builder.append("</html>");
		return builder.toString();
	}
	
	private String wrapInHtmlTags(String text){
		StringBuffer builder = new StringBuffer();
		builder.append("<html>");
		builder.append("<head></head>");
		builder.append("<body><p>");
		builder.append(text);
		builder.append("</p></body>");
		builder.append("</html>");
		return builder.toString();
	}
	
	/*
	 *	Example print executor that illustrates use of the executor pattern
	 *  in Spring RCP.
	 * 
	 * @author Jonny Wray
	 *
	 */
	private class PrintCommandExecutor extends AbstractActionCommandExecutor{

        public boolean isEnabled() {
			return true;
		}

		public void execute() {
			String message = "I'm sorry, printing has not yet been implemented";
			JOptionPane.showMessageDialog(null, message, 
					"Printing Information", JOptionPane.INFORMATION_MESSAGE);
        }
	}
}
