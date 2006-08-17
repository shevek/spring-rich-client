package org.springframework.richclient.application.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

/**
 * <code>ApplicationPageFactory</code> implementation for
 * <code>DefaultApplicationPage</code>.
 * 
 * @author Peter De Bruycker
 * 
 */
public class DefaultApplicationPageFactory implements ApplicationPageFactory{
    private static final Log logger = LogFactory.getLog( DefaultApplicationWindowFactory.class );

    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
        logger.info( "Creating new DefaultApplicationPage" );

        DefaultApplicationPage page = new DefaultApplicationPage();
        page.setApplicationWindow( window );
        page.setDescriptor( descriptor );
        
        return page;
    }
}
