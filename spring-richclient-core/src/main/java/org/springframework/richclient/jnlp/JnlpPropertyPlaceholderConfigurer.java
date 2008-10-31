package org.springframework.richclient.jnlp;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import java.util.Properties;

/**
 * Subclass of PropertyPlaceholderConfigurer that resolves the following properties as placeholders:
 * <ul>
 * <li><code>jnlp.webAppContextUrl</code> the webAppContextUrl, for example
 * http://domain.com/petclinic/</li>
 * </ul>
 * <p/>
 * <p>Can be combined with "locations" and/or "properties" values.
 * <p/>
 * <p>If a placeholder could not be resolved against the provided local
 * properties within the application, this configurer will fall back to
 * the JNLP properties. Can also be configured to let jnlp properties
 * override local properties (contextOverride=true).
 * <p/>
 * <p>If not running within a JNLP context (or any other context that
 * is able to satisfy the BasicService.lookup call), this class will
 * use the fallBackWebAppContextUrl. This allows for keeping
 * JnlpPropertyPlaceholderConfigurer definitions in test suites.
 * <p/>
 * <p> A typical usage would be:
 * <pre>
 * &lt;bean id="jnlpPropertyPlaceholderConfigurer"
 *      class="be.kahosl.thot.swingui.util.JnlpPropertyPlaceholderConfigurer"&gt;
 *      &lt;property name="fallBackWebAppContextUrl" value="http://localhost:8080/mywebapp/"/&gt;
 *      &lt;property name="jnlpRelativeDirectoryPathFromWebAppContext" value="/jnlp/"/&gt;
 *  &lt;/bean&gt;
 *  </pre>
 * <p/>
 * Use this in combination with Sun's JnlpDownloadServlet to not have to hardcode your server URL:
 * http://java.sun.com/j2se/1.5.0/docs/guide/javaws/developersguide/downloadservletguide.html
 *
 * @author Geoffrey De Smet
 * @see #setLocations
 * @see #setProperties
 * @see #setSystemPropertiesModeName
 * @see #setContextOverride
 * @see javax.jnlp.BasicService#getCodeBase()
 */
public class JnlpPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /**
     * The placeholder for getWebAppContextUrl()
     */
    public static final String WEB_APP_CONTEXT_URL_PLACEHOLDER = "jnlp.webAppContextUrl";

    private boolean contextOverride = false;
    private String fallBackWebAppContextUrl = "http://localhost:8080/";
    private String jnlpRelativeDirectoryPathFromWebAppContext = "/";

    /**
     * Set whether the JNLP properties should override local properties within the application.
     * Default is "false": JNLP properties settings serve as fallback.
     * <p>Note that system properties will still override JNLP properties,
     * if the system properties mode is set to "SYSTEM_PROPERTIES_MODE_OVERRIDE".
     *
     * @see #setSystemPropertiesModeName
     * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
     */
    public void setContextOverride(boolean contextOverride) {
        this.contextOverride = contextOverride;
    }

    /**
     * Set the webAppContextUrl to use when no javax.jnlp.BasicService is available.
     * This is usefull for testing outside JNLP (Java webstart).
     * This defaults to <code>http://localhost:8080/</code>.
     * Ussually you 'll want to set this to <code>http://localhost:8080/mywebapp/</code>.
     *
     * @param fallBackWebAppContextUrl the webAppContextUrl to fall back on ending with a slash
     */
    public void setFallBackWebAppContextUrl(String fallBackWebAppContextUrl) {
        this.fallBackWebAppContextUrl = fallBackWebAppContextUrl;
    }

    /**
     * Sets the relative directory path of the JNLP file relative to the WebAppContext.
     * Default this is <code>/</code>, which means that the JNLP file is in the root of your webapp.
     * If your JNLP file isn't in the root of you webapp, change it.
     * For example for <code>http://localhost:8080/mywebapp/dist/jnlp/myswingapp.jnlp</code>
     * you would set this to <code>/dist/jnlp/</code>.
     *
     * @param jnlpRelativeDirectoryPathFromWebAppContext
     *         the relative directory path starting and ending with a slash
     */
    public void setJnlpRelativeDirectoryPathFromWebAppContext(String jnlpRelativeDirectoryPathFromWebAppContext) {
        this.jnlpRelativeDirectoryPathFromWebAppContext = jnlpRelativeDirectoryPathFromWebAppContext;
    }

    protected String resolvePlaceholder(String placeholder, Properties props) {
        String value = null;
        if (this.contextOverride) {
            value = resolvePlaceholder(placeholder);
        }
        if (value == null) {
            value = super.resolvePlaceholder(placeholder, props);
        }
        if (value == null) {
            value = resolvePlaceholder(placeholder);
        }
        return value;
    }

    /**
     * Resolves the given placeholder using the jnlp properties.
     *
     * @param placeholder the placeholder to resolve
     * @return the resolved value, of null if none
     */
    protected String resolvePlaceholder(String placeholder) {
        String value = null;
        if (placeholder.equals(WEB_APP_CONTEXT_URL_PLACEHOLDER)) {
            value = getWebAppContextUrl();
        }
        return value;
    }

    /**
     * Uses the jnlp API to determine the webapp context.
     * If used outside of webstart, <code>fallBackWebAppContextUrl</code> is returned.
     * For example this could return <code>http://localhost:8080/mywebapp/</code>.
     *
     * @return the url to the webapp ending with a slash
     */
    public String getWebAppContextUrl() {
        String webAppContextUrl;
        try {
            BasicService basicService = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
            String codeBase = basicService.getCodeBase().toExternalForm();
            if (!codeBase.endsWith("/")) {
                codeBase += "/";
            }
            int webAppContextUrlLength = codeBase.lastIndexOf(jnlpRelativeDirectoryPathFromWebAppContext);
            webAppContextUrl = codeBase.substring(0, webAppContextUrlLength + 1);
        } catch (UnavailableServiceException e) {
            // TODO logging
            webAppContextUrl = fallBackWebAppContextUrl;
        }
        return webAppContextUrl;
    }

}
