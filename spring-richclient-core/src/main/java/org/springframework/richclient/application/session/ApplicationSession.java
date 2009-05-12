package org.springframework.richclient.application.session;

import org.springframework.binding.value.support.PropertyChangeSupport;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.security.LoginEvent;
import org.springframework.richclient.security.LogoutEvent;
import org.springframework.security.Authentication;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * This object is a singleton that will: act as a storage for session and
 * user variables,  registers itself on  ApplicationEvents and can be used
 * to monitor changes on the contained variables.
 *
 * @author Jan Hoskens
 *
 */
public final class ApplicationSession implements ApplicationListener
{

    /** Holds the singleton instance. */
    private static final ApplicationSession INSTANCE = new ApplicationSession();

    /** Custom initialising code/variables will be passed by an initialiser. */
    private ApplicationSessionInitializer applicationSessionInitializer;

    /** Handler managing listeners registered on properties. */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /** Property that can be monitored. */
    public static final String USER = "user";

    /** Property that can be monitored. */
    public static final String USER_ATTRIBUTES = "userAttributes";

    /** Property that can be monitored. */
    public static final String SESSION_ATTRIBUTES = "sessionAttributes";

    /** User attributes map. */
    private Map<String, Object> userAttributes = new HashMap<String, Object>();

    /** Session attributes map. */
    private Map<String, Object> sessionAttributes = new HashMap<String, Object>();


    /**
     * Private constructor: Singleton Pattern.
     */
    private ApplicationSession()
    {
    }

    /**
     * Get the instance: Singleton Pattern.
     *
     * @return the ApplicationSession.
     */
    public static ApplicationSession getSession()
    {
        return INSTANCE;
    }

    /**
     * Handle events that influence the session/user context.
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if ((event instanceof LoginEvent)
                && (event.getSource() != LoginEvent.NO_AUTHENTICATION))
            handleLoginEvent((LoginEvent) event);
        else if ((event instanceof LogoutEvent))
            handleLogoutEvent((LogoutEvent) event);
    }

    /**
     * When a correct login occurs, read all relevant userinformation into
     * session.
     *
     * @param event
     *            the loginEvent that triggered this handler.
     */
    protected void handleLoginEvent(LoginEvent event)
    {
        ApplicationSessionInitializer asi = getApplicationSessionInitializer();
        if (asi != null)
        {
            asi.initializeUser();
            Map<String, Object> userAttributes = asi.getUserAttributes();
            if (userAttributes != null)
            {
                setUserAttributes(userAttributes);
            }
        }
        Authentication auth = (Authentication) event.getSource();
        propertyChangeSupport.firePropertyChange(USER, null, auth);
    }

    /**
     * When a logout occurs, remove all user related information from the
     * session.
     *
     * @param event
     *            the logoutEvent that triggered this handler.
     */
    protected void handleLogoutEvent(LogoutEvent event)
    {
        clearUser();
        Authentication auth = (Authentication) event.getSource();
        propertyChangeSupport.firePropertyChange(USER, auth, null);
    }

    /**
     * Set an initializer object containing vars/commands and custom init code.
     *
     * @param applicationSessionInitializer The application session initializer
     */
    public void setApplicationSessionInitializer(ApplicationSessionInitializer applicationSessionInitializer)
    {
        this.applicationSessionInitializer = applicationSessionInitializer;
    }

    /**
     * @return the applicationSessionInitializer.
     */
    public ApplicationSessionInitializer getApplicationSessionInitializer()
    {
        if (applicationSessionInitializer == null)
        {
            applicationSessionInitializer = Application.instance()
                    .getLifecycleAdvisor().getApplicationSessionInitializer();
        }
        return applicationSessionInitializer;
    }

    /**
     * Register a listener on the specified property.
     *
     * @param property
     *            Property to monitor.
     * @param listener
     *            PropertyChangeListener to add.
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    /**
     * Unregister a listener from the specified property.
     *
     * @param property
     *            Property that currently is being monitored.
     * @param listener
     *            PropertyChangeListener to remove.
     */
    public void removePropertyChangeListener(String property, PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(property, listener);
    }

    /**
     * Initialize the session attributes.
     */
    public void initializeSession()
    {
        ApplicationSessionInitializer asi = getApplicationSessionInitializer();
        if (asi != null)
        {
            asi.initializeSession();
            Map<String, Object> sessionAttributes = asi.getSessionAttributes();
            if (sessionAttributes != null)
            {
                    setSessionAttributes(sessionAttributes);
            }
            propertyChangeSupport.firePropertyChange(SESSION_ATTRIBUTES, null, sessionAttributes);
        }
    }

    /**
     * gets a named attribute of the user associated to this context
     *
     * @param key
     *            name of the attribute
     * @return the attribute value (attribute values are strings)
     */
    public Object getUserAttribute(String key)
    {
        return this.getUserAttribute(key, null);
    }

    /**
     * Get a value from the user attributes map.
     *
     * @param key
     *            name of the attribute
     * @param defaultValue
     *            a default value to return if no value is found.
     * @return the attribute value
     */
    public Object getUserAttribute(String key, Object defaultValue)
    {
        Object attributeValue = this.userAttributes.get(key);
        if (attributeValue == null)
            attributeValue = defaultValue;
        return attributeValue;
    }

    /**
     * Add a key/value pair to the user attributes map.
     *
     * @param key
     *            a unique string code.
     * @param newValue
     *            the associated value.
     */
    public void setUserAttribute(String key, Object newValue)
    {
        Object oldValue = userAttributes.put(key, newValue);
        propertyChangeSupport.firePropertyChange(key, oldValue, newValue);
        propertyChangeSupport.firePropertyChange(USER_ATTRIBUTES, null, userAttributes);
    }

    /**
     * Add the given key/value pairs to the user attributes.
     *
     * @param attributes
     *            a map of key/value pairs.
     */
    public void setUserAttributes(Map<String, Object> attributes)
    {
        userAttributes.putAll(attributes);
        propertyChangeSupport.firePropertyChange(USER_ATTRIBUTES, null, userAttributes);
    }

    /**
     * Clear all user attributes.
     */
    public void clearUser()
    {
        this.userAttributes.clear();
        propertyChangeSupport.firePropertyChange(USER_ATTRIBUTES, null, null);
    }

    /**
     * Get a value from the session attributes map.
     *
     * @param key
     *            name of the attribute
     * @return the attribute value
     */
    public Object getSessionAttribute(String key)
    {
        return this.getSessionAttribute(key, null);
    }

    /**
     * Get a value from the session attributes map.
     *
     * @param key a unique string code
     * @param defaultValue the default value if not found
     * @return The session attibute or the default value if not found
     */
    public Object getSessionAttribute(String key, Object defaultValue)
    {
        Object attributeValue = this.sessionAttributes.get(key);
        if (attributeValue == null)
            attributeValue = defaultValue;
        return attributeValue;
    }

    /**
     * Add a key/value pair to the session attributes map.
     *
     * @param key
     *            a unique string code.
     * @param newValue
     *            the associated value.
     */
    public void setSessionAttribute(String key, Object newValue)
    {
        Object oldValue = sessionAttributes.put(key, newValue);
        propertyChangeSupport.firePropertyChange(key, oldValue, newValue);
        propertyChangeSupport.firePropertyChange(SESSION_ATTRIBUTES, null, sessionAttributes);
    }

    /**
     * Add the given key/value pairs to the session attributes.
     *
     * @param attributes
     *            a map of key/value pairs.
     */
    public void setSessionAttributes(Map<String, Object> attributes)
    {
        this.sessionAttributes.putAll(attributes);
        propertyChangeSupport.firePropertyChange(SESSION_ATTRIBUTES, null, sessionAttributes);
    }

    /**
     * Clear all session attributes.
     */
    public void clearSession()
    {
        this.sessionAttributes.clear();
        propertyChangeSupport.firePropertyChange(SESSION_ATTRIBUTES, null, sessionAttributes);
    }

    /**
     * Get a value from the user OR session attributes map.
     *
     * @param key
     *            name of the attribute
     * @return the attribute value
     */
    public Object getAttribute(String key)
    {
        return getAttribute(key, null);
    }

    /**
     * Get a value from the user OR session attributes map.
     *
     * @param key
     *            name of the attribute
     * @param defaultValue
     *            a default value to return if no value is found.
     * @return the attribute value
     */
    public Object getAttribute(String key, Object defaultValue)
    {
        Object attributeValue = getUserAttribute(key, null);
        if (attributeValue != null)
            return attributeValue;
        attributeValue = getSessionAttribute(key, null);
        if (attributeValue != null)
            return attributeValue;
        return defaultValue;
    }
}
