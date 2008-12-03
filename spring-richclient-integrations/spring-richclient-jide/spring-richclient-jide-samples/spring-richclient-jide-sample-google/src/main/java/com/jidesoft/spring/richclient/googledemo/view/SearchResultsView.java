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

import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;
import com.jidesoft.spring.richclient.googledemo.command.SearchWorker;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultEvent;
import com.jidesoft.spring.richclient.googledemo.events.SearchResultsSelectionEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.docking.jide.editor.OpenEditorEvent;
import org.springframework.richclient.application.docking.jide.view.JideAbstractView;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.tree.FocusableTreeCellRenderer;
import org.springframework.richclient.util.PopupMenuMouseListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A view that displays the google search results within a tree. 
 * 
 * Demonstrates how to add view specific toolbar in a declarative manner
 * via the Spring RCP executor framework. See the commandContext.xml for
 * the declarative aspect.
 * 
 * @author Jonny Wray
 *
 */
public class SearchResultsView extends JideAbstractView implements ApplicationListener,
		TreeSelectionListener{

	private static final String CLEAR_RESULTS_ID = "searchResultsView.clearCommand";
	private static final String NEXT_PAGE_ID = "searchResultsView.nextPageCommand";
	private static final String PREVIOUS_PAGE_ID = "searchResultsView.previousPageCommand";
	private ClearCommandExecutor clearCommandExecutor = new ClearCommandExecutor();
	private NextPageCommandExecutor nextPageCommandExecutor = new NextPageCommandExecutor();
	private PreviousPageCommandExecutor previousPageCommandExecutor = new PreviousPageCommandExecutor();
	
	private OpenBrowserCommand openBrowserCommand = new OpenBrowserCommand();
	
	private MouseListener doubleClickListener = new DoubleClickListener();
	private JTree tree;
	private DefaultTreeModel treeModel = null;
	private GoogleSearchResult searchResults;
	private static final int PAGE_DELTA = 10;
	
	public SearchResultsView(){
	}
	
	
	public void valueChanged(TreeSelectionEvent e) {
		JTree tree = (JTree)e.getSource();
        if(tree.getSelectionCount() == 1){
	        Object selected = 
	        	((DefaultMutableTreeNode)e.getPath().getLastPathComponent()).getUserObject();
	        if(selected instanceof GoogleSearchResultElement){
	        	GoogleSearchResultElement element = (GoogleSearchResultElement)selected;
	        	getApplicationContext().publishEvent(new SearchResultsSelectionEvent(this,
	        			element));
	        }
        }
	}


	/**
	 * Listens for search results event (fired by the search command
	 * worker when finished). These events should occur on the EDT
	 * but I check to make sure.
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof SearchResultEvent){
			SearchResultEvent searchResultEvent = (SearchResultEvent)event;
			final GoogleSearchResult results = searchResultEvent.getSearchResult();
			if(SwingUtilities.isEventDispatchThread()){
				updateTreeModel(results);
			}
			else{
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						updateTreeModel(results);
					}
				});
			}
		}
	}

	protected JComponent createControl() {
		if(treeModel == null){
			treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		}
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(treeCellRenderer);
		tree.addTreeSelectionListener(this);
		tree.addMouseListener(doubleClickListener);
		tree.addMouseListener(new DynamicPopupMenuMouseListener());
		ToolTipManager.sharedInstance().registerComponent(tree);
		return new JScrollPane(tree);
	}

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register(CLEAR_RESULTS_ID, clearCommandExecutor);
    	context.register(NEXT_PAGE_ID, nextPageCommandExecutor);
    	context.register(PREVIOUS_PAGE_ID, previousPageCommandExecutor);
    }
    
	private void updateTreeModel(GoogleSearchResult results){

		this.searchResults = results;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
		root.removeAllChildren();
		if(results != null){
			for(int i=0;i<results.getResultElements().length;i++){
				GoogleSearchResultElement element = results.getResultElements()[i];
				root.add(new DefaultMutableTreeNode(element));
			}
			updateCommands();
		}
		treeModel.reload();
	}
	
	/*
	 * This tree cell rendering uses a specific bullet defined in the
	 * images file and forces the underlying JLabel to interperate the
	 * text as html as the google search result element title is formattted
	 * in html
	 */
    private DefaultTreeCellRenderer treeCellRenderer = new FocusableTreeCellRenderer() {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
        	
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if(node.getUserObject() instanceof GoogleSearchResultElement){
            	GoogleSearchResultElement result = (GoogleSearchResultElement)node.getUserObject();
            	String text = "<em>"+Integer.toString(searchResults.getStartIndex() + row)
            		+ "</em> " + result.getTitle();
	            this.setText("<html>"+text);
	            //this.setIcon(getIconSource().getIcon("searchResultItem.bullet"));
	            this.setIcon(null);
	            this.setToolTipText("<html>"+result.getSnippet());
            }
            return this;
        }
    };

    private void openWebBrowserEditor(){
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
		/*
		 * Method one, don't use the event mechanism
		 */
		//Object userObject = node.getUserObject();
		//getActiveWindow().getPage().openEditor(userObject);
		/*
		 * Method two, use event mechanism which allows other views to react,
		 * for example the status bar.
		 */
		OpenEditorEvent event = new OpenEditorEvent(node.getUserObject());
		getApplicationContext().publishEvent(event);
    }
    
    private class OpenBrowserCommand extends ApplicationWindowAwareCommand {

    	private static final String ID = "openBrowserCommand";
    	
    	public OpenBrowserCommand(){
    		super(ID);
    	}
    	
		public void doExecuteCommand() {
			openWebBrowserEditor();
        }
    }

    private class DoubleClickListener extends MouseAdapter{

		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2){
				openWebBrowserEditor();
			}
		}
    	
    }
    
    private class DynamicPopupMenuMouseListener extends PopupMenuMouseListener{
    	
    	public DynamicPopupMenuMouseListener(){
    		
    	}
    	
    	protected JPopupMenu getPopupMenu(MouseEvent e) {

			CommandGroup group = 
	    		CommandGroup.createCommandGroup("searchResultsCommandGroup",
	                new Object[] { 
	    				openBrowserCommand});
	        JPopupMenu menu = group.createPopupMenu();     
	        return menu;
    	}
    }
    
    private void updateCommands(){
    	if(treeModel == null){
        	clearCommandExecutor.setEnabled(false);
        	nextPageCommandExecutor.setEnabled(false);
        	previousPageCommandExecutor.setEnabled(false);
    	}
    	else{
    		int children = ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
    		clearCommandExecutor.setEnabled(children > 0);
    		nextPageCommandExecutor.setEnabled(children > 0);
    		previousPageCommandExecutor.setEnabled((children >0) &&
    				searchResults.getStartIndex() > 1);
    	}
    	
    }
    
    private class ClearCommandExecutor extends AbstractActionCommandExecutor {

		public void execute() {
			if(treeModel != null){
				((DefaultMutableTreeNode)treeModel.getRoot()).removeAllChildren();
		    	treeModel.reload();
			}
			updateCommands();
        }
    }
    
    /*
     * These are primative implementations of page forward and back that simply
     * does another search. In reality the results could be cached on a page basis
     * to avoid using up extra searchs, and improve speed
     */
    private class NextPageCommandExecutor extends AbstractActionCommandExecutor{
    	public void execute(){
    		String query = searchResults.getSearchQuery();
    		int startResults = searchResults.getStartIndex() + PAGE_DELTA -1;
    		SearchWorker worker = new SearchWorker(query, startResults);
    		worker.start();
    	}
    }
    
    private class PreviousPageCommandExecutor extends AbstractActionCommandExecutor{
    	public void execute(){
    		String query = searchResults.getSearchQuery();
    		int startResults = searchResults.getStartIndex() - PAGE_DELTA -1;
    		if(startResults < 0){
    			startResults = 0;
    		}
    		SearchWorker worker = new SearchWorker(query, startResults);
    		worker.start();
    	}
    }
}
