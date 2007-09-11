package org.springframework.richclient.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * This class provides a singleton model for accessing the configured ApplicationServices
 * object.
 * 
 * @author Larry Streepy
 * 
 */
public class ApplicationServicesLocator {

    private static final Log logger = LogFactory.getLog(ApplicationServicesLocator.class);

    /** The singleton instance. */
    private static ApplicationServicesLocator INSTANCE;

    /** The configured ApplicationServices. */
    private ApplicationServices applicationServices;

    /**
     * Default Constructor.
     */
    public ApplicationServicesLocator() {
        load(this);
    }

    /**
     * Constructor.
     * 
     * @param applicationServices instance to use
     */
    public ApplicationServicesLocator( ApplicationServices applicationServices ) {
        setApplicationServices(applicationServices);
        load(this);
    }

    /**
     * Return the single ApplicationServicesLocator instance.
     * 
     * @return The instance
     */
    public static ApplicationServicesLocator instance() {
        Assert.state(INSTANCE != null, "The application services locator instance has not yet been initialized.");
        return INSTANCE;
    }

    public static boolean isLoaded() {
        return INSTANCE != null;
    }

    /**
     * Load the single ApplicationServicesLocator instance.
     * 
     * @param instance The ApplicationServicesLocator
     */
    public static void load( ApplicationServicesLocator instance ) {
        if( INSTANCE != null ) {
            logger.info("Replacing existing ApplicationServicesLocator instance with: " + instance);
        }
        INSTANCE = instance;
    }

    /**
     * Return a global service locator for application services.
     * 
     * @return The application services locator.
     */
    public static ApplicationServices services() {
        return instance().getApplicationServices();
    }

    /**
     * Set the ApplicationServices instance to use
     * 
     * @param applicationServices
     */
    public void setApplicationServices( ApplicationServices applicationServices ) {
        this.applicationServices = applicationServices;
    }

    /**
     * Get the ApplicationServices to use
     * 
     * @return ApplicationServices instance
     */
    public ApplicationServices getApplicationServices() {
        return applicationServices;
    }

}
