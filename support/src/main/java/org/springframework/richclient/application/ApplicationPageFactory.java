package org.springframework.richclient.application;

/**
 * Service interface for creating <code>ApplicationPage</code>s.
 * 
 * @author Peter De Bruycker
 */
public interface ApplicationPageFactory {
    /**
     * Create a new <code>ApplicationPage</code>.
     * 
     * @return the window
     */
    ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor );
}
