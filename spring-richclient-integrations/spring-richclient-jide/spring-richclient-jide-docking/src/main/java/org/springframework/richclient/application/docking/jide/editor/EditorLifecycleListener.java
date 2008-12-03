package org.springframework.richclient.application.docking.jide.editor;

import com.jidesoft.document.DocumentComponentAdapter;
import com.jidesoft.document.DocumentComponentEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.docking.jide.JideApplicationPage;

/**
 * Listener class that ensures Spring RCP focus events and
 * page component lifestyle events are called from the
 * JIDE document events.
 * 
 * @author Jonny Wray
 *
 */
public class EditorLifecycleListener extends DocumentComponentAdapter{

    protected final Log logger = LogFactory.getLog(getClass());

	private PageComponent pageComponent;
	private WorkspaceView workspace;
	
	public EditorLifecycleListener(WorkspaceView workspace, PageComponent pageComponent){
		this.pageComponent = pageComponent;
		this.workspace = workspace;
	}
	
	public void documentComponentDeactivated(DocumentComponentEvent event) {
		if(logger.isDebugEnabled()){
			logger.debug("Page component "+pageComponent.getId()+" deactivated");
		}
		((JideApplicationPage)workspace.getContext().getPage()).fireFocusLost(pageComponent);
	}

	/**
	 * Default closed implementation removes the given page component from the
	 * workspace view, and so the document from the document pane.
	 */
	public void documentComponentClosed(DocumentComponentEvent event) {
		if(logger.isDebugEnabled()){
			logger.debug("Page component "+pageComponent.getId()+" closed");
		}
		workspace.remove(pageComponent);
		((JideApplicationPage)workspace.getContext().getPage()).fireClosed(pageComponent);
	}
	
	public void documentComponentOpened(DocumentComponentEvent event) {
		if(logger.isDebugEnabled()){
			logger.debug("Page component "+pageComponent.getId()+" opened");
		}
		((JideApplicationPage)workspace.getContext().getPage()).fireOpened(pageComponent);
	}

	public void documentComponentActivated(DocumentComponentEvent event){
		if(logger.isDebugEnabled()){
			logger.debug("Page component "+pageComponent.getId()+" activated");
		}
		((JideApplicationPage)workspace.getContext().getPage()).fireFocusGained(pageComponent);
	}

	public void documentComponentClosing(DocumentComponentEvent event){
		if(logger.isDebugEnabled()){
			logger.debug("Page component "+pageComponent.getId()+" closing");
		}
		((JideApplicationPage)workspace.getContext().getPage()).fireFocusLost(pageComponent);
	}
}
