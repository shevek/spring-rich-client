package org.springframework.richclient.application.session;

import java.util.Map;
import java.util.List;


public class ApplicationSessionInitializer
{

    /**
     * Extra user attributes to be added to the ApplicationSession after login
     */
    private Map<String, Object> userAttributes;

    /**
     * Extra session attributes to be added to the ApplicationSession after login
     */
    private Map<String, Object> sessionAttributes;

    /**
     * List of command ids to be executed before startup of the application window
     */
    private List<String> preStartupCommandIds;

     /**
     * List of command ids to be executed after startup of the application window
     */
    private List<String> postStartupCommandIds;

    /**
     * Sets extra user attributes to be added to the ApplicationSession after login
     */
    public void setUserAttributes(Map<String, Object> attributes)
    {
        this.userAttributes = attributes;
    }

    /**
     * @return extra user attributes to be added to the ApplicationSession after login
     */
    public Map<String, Object> getUserAttributes()
    {
        return userAttributes;
    }

    /**
     * Sets extra session attributes to be added to the ApplicationSession after login
     */
    public void setSessionAttributes(Map<String, Object> attributes)
    {
        this.sessionAttributes = attributes;
    }

    /**
     * @return extra session attributes to be added to the ApplicationSession after login
     */
    public Map<String, Object> getSessionAttributes()
    {
        return sessionAttributes;
    }

     /**
     * Sets the list of command ids to be executed before startup of the application window
     */
    public void setPreStartupCommandIds(List<String> commandIds)
    {
        this.preStartupCommandIds = commandIds;
    }

    /**
     * @return the list of command ids to be executed before startup of the application window
     */
    public List<String> getPreStartupCommandIds()
    {
        return preStartupCommandIds;
    }

    /**
     * Sets the list of command ids to be executed after startup of the application window
     */
    public void setPostStartupCommandIds(List<String> commandIds)
    {
        this.postStartupCommandIds = commandIds;
    }

     /**
     * @return the list of command ids to be executed after startup of the application window
     */
    public List<String> getPostStartupCommandIds()
    {
        return postStartupCommandIds;
    }

    /**
     * Hook that is called before the session attributes are retrieved. Here you can
     * set session attributes in code.
     */
    public void initializeSession()
    {
    }

    /**
     * Hook that is called before the user attributes are retrieved. Here you can
     * set user attributes in code.
     */
    public void initializeUser()
    {
    }
}
