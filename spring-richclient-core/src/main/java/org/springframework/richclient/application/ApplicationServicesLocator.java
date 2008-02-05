package org.springframework.richclient.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * <p>
 * This class provides a singleton model for accessing the configured
 * ApplicationServices object. Create your {@link ApplicationServices} and load
 * them into the locator within your Spring context to make the services available
 * throughout the application.
 * </p>
 *
 * <pre>
 *  &lt;bean id=&quot;serviceLocator&quot; class=&quot;org.springframework.richclient.application.ApplicationServicesLocator&quot;&gt;
 *    &lt;property name=&quot;applicationServices&quot; ref=&quot;applicationServices&quot;/&gt;
 *  &lt;/bean&gt;
 * </pre>
 *
 * or by using construtor arguments:
 *
 * <pre>
 *  &lt;bean id=&quot;serviceLocator&quot; class=&quot;org.springframework.richclient.application.ApplicationServicesLocator&quot;&gt;
 *    &lt;constructor-arg index=&quot;0&quot; ref=&quot;applicationServices&quot;/&gt;
 *  &lt;/bean&gt;
 * </pre>
 *
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
	 * Convenience constructor to add ApplicationServices at construction time.
	 */
	public ApplicationServicesLocator(ApplicationServices applicationServices) {
		setApplicationServices(applicationServices);
		load(this);
	}

	/**
	 * Return the single ApplicationServicesLocator instance.
	 */
	public static ApplicationServicesLocator instance() {
		Assert.state(INSTANCE != null, "The application services locator instance has not yet been initialized.");
		return INSTANCE;
	}

	/**
	 * Check if an instance is available.
	 *
	 * @return <code>true</code> if an ApplicationServicesLocator is loaded.
	 */
	public static boolean isLoaded() {
		return INSTANCE != null;
	}

	/**
	 * Load the single ApplicationServicesLocator instance.
	 */
	public static void load(ApplicationServicesLocator instance) {
		if (INSTANCE != null) {
			logger.info("Replacing existing ApplicationServicesLocator instance with: " + instance);
		}
		INSTANCE = instance;
	}

	/**
	 * Convenience method to get the ApplicationServices by querying the
	 * currently loaded ApplicationServicesLocator.
	 */
	public static ApplicationServices services() {
		return instance().getApplicationServices();
	}

	/**
	 * Set the ApplicationServices instance.
	 */
	public void setApplicationServices(ApplicationServices applicationServices) {
		this.applicationServices = applicationServices;
	}

	/**
	 * Return the ApplicationServices instance.
	 */
	public ApplicationServices getApplicationServices() {
		return applicationServices;
	}

}