/**
 * 
 */
package org.springframework.richclient.core;

/**
 * Interface implemented by objects that can be controlled by a security controller. Any
 * object that implements this interface and is processed by
 * {@link org.springframework.richclient.application.config.ApplicationObjectConfigurer#configure(Object, String)}
 * will be linked to the security controller with the id returned by
 * {@link #getSecurityControllerId()}
 * 
 * @author Larry Streepy
 * @see org.springframework.richclient.security.SecurityController
 * @see org.springframework.richclient.application.config.ApplicationObjectConfigurer#configure(Object, String)
 */
public interface SecurityControllable extends Authorizable {

    /**
     * Set the Id of the security controller that should manage this object.
     * @param controllerId Id (bean name) of the security controller
     */
    public void setSecurityControllerId(String controllerId);

    /**
     * Get the id (bean name) of the security controller that should manage this object.
     * @return controller id
     */
    public String getSecurityControllerId();
}
