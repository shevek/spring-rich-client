/**
 * Integrates
 * <A HREF="http://static.springframework.org/spring-security/site/index.html">Spring Security System </A>
 * into RCP.
 *
 * <h1>Overview</h1>
 *
 * <P>Spring Security is a comprehensive open-source security system that
 * delivers fully-featured security to Spring applications. To learn more
 * about Spring Security or access detailed documentation, please visit the
 * project home page at <A HREF="http://static.springframework.org/spring-security/site/index.html">http://static.springframework.org/spring-security/site/index.html</A>.</p>
 *
 * <P>It is envisaged that many RCP clients will be connecting with a
 * remote Spring-powered server. In such deployments, security becomes of
 * paramount importance. Whilst transport-layer security (such as HTTPS,
 * port filtering and firewalls) are essential to almost all production
 * applications, this package delivers comprehensive application-layer
 * security to RCP clients by hooking into the Spring Security project.</P>
 *
 * <h2>Background Knowledge</h2>
 *
 * <P>Whilst you should really read the Spring Security System
 * reference documentation to fully understand the architecture, the most
 * important details you need to understand in order to utilize this RCP
 * package is the summarized below.</P>
 *
 * <P>RCP uses the following key Spring Security classes and interfaces:</P>
 *
 * <P>ContextHolder, which simply uses a ThreadLocal to store a
 * SecureContext implementation.</P>
 *
 * <P>Authentication, which stores the details of a principal, credentials
 * and its granted authorities. RCP uses Spring Security's
 * UsernamePasswordAuthenticationToken, which simply represents a username
 * and password for the principal and credentials respectively.</P>
 *
 * <P>AuthenticationManager, which is able to accept a <em>request</em>
 * Authentication object (containing only the principal and credentials
 * details), process its validity, and return a populated Authentication
 * object (also containing the GrantedAuthorty[]s).</P>
 *
 * <h2>Per-thread vs. per-Application authentication</h2>
 *
 * <p>One thing to keep in mind is that Spring Security maintains authentication
 * credentials on a per-thread basis (ContextHolder described above). This
 * makes sense when considering server side implementations where different
 * threads are working on behalf of potentially different principals. In a
 * rich application, however, it is rare to have anything but a "global"
 * notion of the logged in user (and associated credentials). In fact,
 * having a per-thread credential store is problematic for a rich
 * application where operations may take place on different threads (a main
 * thread, UI event dispatch thread, worker threads, etc.). Further, it
 * would be difficult to propogate credentials to all new threads created,
 * or to update existing threads when a user changes credentials (such as
 * loggin out or logging in as a different user).</p>
 *
 * <p>For these reasons, the application security model provided within RCP
 * uses a global store for credentials. See the ApplicationSecurityManager
 * below.</p>
 *
 * <h2>Major Players - Objects, Interfaces, and Events</h2>
 *
 * <h3>ApplicationSecurityManager and DefaultApplicationSecurityManager</h3>
 *
 * <p>Instances of ApplicationSecurityManager are responsible for
 * performing the security operations of user login and logout and
 * maintaining the global authentication token for the user. A default
 * implementation is provided in DefaultApplicationSecurityManager.</p>
 *
 * <p>An instance of the ApplicationSecurityManager is available from
 * ApplicationServices like this:
 * <code>Application.services().getApplicationSecurityManager()</code>.
 * Application code can access the current authentication token by calling the
 * <code>getAuthentication()</code> method (or it can implement the notification
 * interfaces defined below).</p>
 *
 * <p>As the ApplicationSecurityManager handles login/logout requests, it fires
 * a set of events to inform the application of the security lifecycle.  The
 * table below shows the events that are fired in response to various login and logout
 * processing.</p>
 *
 * <table border="1" cellpadding="3">
 * <thead><tr><th>Action</th><th>Events Fired</th></tr></thead>
 * <tr><td>Successful login</td><td>AuthenticationEvent, LoginEvent</td></tr>
 * <tr><td>Failed login</td><td>AuthenticationFailedEvent</td></tr>
 * <tr><td>Logout</td><td>AuthenticationEvent, LogoutEvent</td></tr>
 * </table>
 *
 * <p>In order to perform authentication operations, the ApplicationSecurityManager
 * must have an AuthenticationManager configured.  This can be done in the
 * application context, like this:</p>
 *
 * <pre>
 * <code>
 *    &lt;bean id="applicationSecurityManager"
 *          class="org.springframework.richclient.security.support.DefaultApplicationSecurityManager"&gt;
 *          &lt;property name="authenticationManager" ref="authenticationManager"/&gt;
 *    &lt;/bean&gt;
 * </code>
 * </pre>
 *
 * <h3>LoginCommand and LogoutCommand</h3>
 *
 * <p><strong><code>LoginCommand</code></strong> is a simple implementation of an
 * ActionCommand that shows a dialog for collecting a user name and
 * password and then handing them off to the ApplicationSecurityManager to
 * perform the actual login processing (see below for more details).
 * LoginCommand makes some very simple assumptions on the vaildation constraints
 * for the username and password fields, so you might need to subclass it and
 * provide your own implementation of the login form.</p>
 *
 * <p><code>LoginCommand</code> implements a simplistic login failure
 * handling scheme - it lets the user keep trying as long as they want.  Again,
 * you'll probably want to subclass to provide something more clever.  Future
 * work will hopefully include a pluggable login failure handling strategy. One
 * final configurable element on the LoginCommand is how the login dialog should
 * respond to the user pressing the Cancel button.  This handling is controlled
 * by the <code>closeOnCancel</code> property on LoginCommand, which defaults to
 * true.  If <code>closeOnCancel</code> is true and the user cancels the dialog,
 * then the applicaiton will be closed, by calling <code>getApplication().close()</code>
 * </p>
 *
 * <p><strong><code>LogoutCommand</code></strong> is an implementation of ActionCommand
 * that simply invokes the logout processing in the ApplicationSecurityManager.</p>
 *
 * <h3>AuthenticationAware</h3>
 *
 * <p><strong><code>AuthenticationAware</code></strong> is a tag interface that marks
 * beans in the application context.  Any bean that implements this interface
 * will be initially notified of the current authentication token (during bean
 * post-processing) and subsequently notified whenever the authentication token
 * changes.  See SecurityAwareConfigurer for more details.</p>
 *
 * <h3>LoginAware</h3>
 *
 * <p><strong><code>LoginAware</code></strong> is a tag interface that marks beans in
 * the application context.  Any bean that implements this interface will be
 * notified of two major security events: login and logout.
 * See SecurityAwareConfigurer for more details.</p>
 *
 * <h3>SecurityAwareConfigurer</h3>
 * <p><strong><code>SecurityAwareConfigurer</code></strong> is a key player in the security
 * architecture.  It is both a BeanPostProcessor and an ApplicationListener.  Its
 * job is to handle beans that implement AuthenticationAware and LoginAware and
 * configure them with authenticaiton information and notify them of key security
 * events.</p>
 * <p>As a bean post-processor, SecurityAwareConfigurer handles any bean that
 * implements AuthenticationAware and configures them with the current authentication
 * token.</p>
 * <p>As an ApplicationListener, SecurityAwareConfigurer watches for ClientSecurityEvents
 * and turns them into method notifications on the AuthenticationAware and LoginAware
 * interfaces.</p>
 *
 * <p>AuthenticationAware is handled in a "stateful" manner - meaning that the
 * current authentication token is handed to every new bean that is created.  Whereas
 * the LoginAware interface is handled in an "event" manner - meaning that beans that
 * implement the interface are only updated when an event of the proper type occurs.</p>
 *
 * <p>AuthenticationAware notification always takes place prior to LoginAware
 * notifications. So, if you need to perform some operation that requires another
 * bean to have its authentiation state updated, then you should do it in LoginAware
 * (or watch for LoginEvent and LogoutEvent instances directly) as these are always
 * delivered after the AuthenticationAware notifications.  See below in the remoting
 * section for a real example of why this matters.</p>
 *
 * <p>Each security event is translated to a notification, as shown below.</p>
 * <table border="1" cellpadding="3">
 * <thead><tr><th>Event</th><th>Notification Made</th></tr></thead>
 * <tr><td>AuthenticationEvent</td><td>AuthenticationAware.setAuthenticationToken</td></tr>
 * <tr><td>AuthenticationFailedEvent</td><td>no notifications made</td></tr>
 * <tr><td>LoginEvent</td><td>LoginAware.userLogin</td></tr>
 * <tr><td>LogoutEvent</td><td>LoginAware.userLogout</td></tr>
 * </table>
 * <p>Note that for any of this to happen, the SecurityAwareConfigurer must be properly
 * configured in the application context.  Here is an example of that configuration:</p>
 * <pre>
 * <code>
 *    &lt;bean id=&quot;securityAwareConfigurer&quot;
 *          class=&quot;org.springframework.richclient.security.SecurityAwareConfigurer&quot;
 *          lazy-init=&quot;false&quot;/&gt;
 * </code>
 * </pre>
 *
 * <h3>ClientSecurityEvent</h3>
 *
 * <p>The ApplicationSecurityManager is responsible for firing events that correspond
 * to important security lifecycle events (authentication, login, logout, etc.).
 * Specific subtypes represent each important event:</p>
 *
 * <table border="1" cellpadding="5">
 * <thead><tr><th>Event</th><th>Description</th></tr></thead>
 * <tr><td>AuthenticationEvent</td><td>Event fired when the user's authentication changes.  This happens on both a
 * successful login and a logout.</td></tr>
 * <tr><td>AuthenticationFailedEvent</td><td>Event fired when an authentication attempt fails.  This happens when a login is
 * attempted and the authentication manager denies the authentication attempt.</td></tr>
 * <tr><td>LoginEvent</td><td>Event fired when a new user logs in.  This happens when a user successfully
 * logs in, and after the AuthenticationEvent.</td></tr>
 * <tr><td>LogoutEvent</td><td>Event fired when a user logs out.  This happends when a user logs out, and
 * after the AuthenticationEvent.</td></tr>
 * </table>
 *
 * <p>Any bean interested in these events should implement ApplicationListener and
 * then watch for events that extend ClientSecurityEvent.  If you want a "callback"
 * mechanism instead of watching events, then a bean can implement AuthenticationAware
 * and/or LoginAware.</p>
 *
 * <h1>How Login and Logout Works</h1>
 *
 * <P>This package provides two key RCP commands: LoginCommand and
 * LogoutCommand. To use these commands, simply add them to your
 * commands-context.xml, like this:</P>
 *
 * <pre>
 * <code>
 *     &lt;bean id="loginCommand"
 * 	  class="org.springframework.richclient.security.LoginCommand"/&gt;
 *
 *     &lt;bean id="logoutCommand"
 * 	  class="org.springframework.richclient.security.LogoutCommand"/&gt;
 * </code>
 * </pre>
 *
 * <P>Both commands accept an optional property, displaySuccess, which
 * defaults to true. This simply results in an information dialog being
 * displayed after login or logout. You can switch this off by setting the
 * property to false in the application context.</P>
 *
 * <P>LoginCommand basically displays a dialog requesting the username and
 * password. Upon these being entered, a <em>request</em> Authentication
 * object is created (as mentioned in the <em>Background Knowledge</em>
 * section above) and presented for authentication by calling
 * <code>ApplicationSecurityManager.doLogin</code>. If the authentication
 * succeeds, a populated Authentication object is stored as the "global"
 * authentication toke (see above) and is returned to the caller
 * (the LoginCommand). The returned token is also placed into the thread-specific
 * ContextHolder (for a little bit of backward compatibility). As described above,
 * the ApplicationSecurityManager publishes events so other interested classes
 * know a login has taken place.</P>
 *
 * <P>LogoutCommand is far simpler.  It calls
 * <code>ApplicationSecurityManager.doLogout</code>, which fires appropriate events,
 * and then it updates the thread-specific ContextHolder so
 * its Authentication object is null.</P>
 *
 * <h1>Which AuthenticationManager?</h1>
 *
 * <P>As mentioned above, an AuthenticationManager is configured against
 * the LoginCommand. This is just like any other Spring Security use of
 * AuthenticationManager, so you can use any of the standard Spring Security
 * authentication providers with the RCP package (such as
 * <code>DaoAuthenticationProvider</code>).</P>
 *
 * <P>More typically, a rich client will need to use a remote server for authentication.
 * In this case you need the client to ensure a username and password is valid
 * against the remote server, and also obtain the list of
 * GrantedAuthority[]s (so the populated Authentication object can be
 * constructed). To achieve this, on the client you'll need to use the
 * <code>RemoteAuthenticationProvider</code>. On the server you'll need
 * to use the <code>RemoteAuthenticationManagerImpl</code>. On the client
 * you'll use your preferred remoting proxy factory to access the server-side
 * RemoteAuthenticationManagerImpl. You can find these classes in Spring
 * Security's <code>org.springframework.security.providers.rcp</code> package.
 * An example using the HTTP proxy would be configured like this:</P>
 *
 * <pre>
 * <code>
 * 	&lt;bean id="applicationSecurityManager"
 * 		class="org.springframework.richclient.security.support.DefaultApplicationSecurityManager"&gt;
 * 		&lt;property name="authenticationManager" ref="authenticationManager"/&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;!-- Remote authentication manager configuration --&gt;
 * 	&lt;bean id="authenticationManager"
 *         class="org.springframework.security.providers.ProviderManager"&gt;
 * 		&lt;property name="providers"&gt;
 * 			&lt;list&gt;
 * 				&lt;ref bean="remoteAuthenticationProvider" /&gt;
 * 			&lt;/list&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="remoteAuthenticationProvider"
 *         class="org.springframework.security.providers.rcp.RemoteAuthenticationProvider"&gt;
 * 		&lt;property name="remoteAuthenticationManager" ref="remoteAuthenticationManager" /&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="remoteAuthenticationManager"
 * 		class="org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean"&gt;
 * 		&lt;property name="serviceUrl"&gt;
 * 			&lt;value&gt;http://localhost:8080/myserver/context/RemoteAuthenticationManager&lt;/value&gt;
 * 		&lt;/property&gt;
 * 		&lt;property name="serviceInterface"&gt;
 *             &lt;value&gt;org.springframework.security.providers.rcp.RemoteAuthenticationManager&lt;/value&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 * </code>
 * </pre>
 *
 * <h1>Remoting Integration</h1>
 *
 * <h2>HTTP proxies and Basic Authentication</h2>
 *
 * <p>Using HTTP invocation for remoting is one of the simplest mechanisms to configure.
 * Two classes are provided to make using HTTP BASIC authentication on top of the
 * simple HTTP remoting protocol.  See the code sample above on how one might
 * configure the use of the HTTP proxy factory.</p>
 *
 * <p><code>org.springframework.richclient.security.remoting.BasicAuthHttpInvokerProxyFactoryBean</code>
 * and
 * <code>org.springframework.richclient.security.remoting.BasicAuthHttpInvokerRequestExecutor</code>.
 *
 * <p><code>BasicAuthHttpInvokerProxyFactoryBean</code> is an extension of
 * <code>HttpInvokerProxyFactoryBean</code> that supports the use of
 * BASIC authentication on each HTTP request.  This factory takes care of instantiating
 * the proper invocation executor, an <code>BasicAuthHttpInvokerRequestExecutor</code>,
 * and keeping it up to date with the latest user credentials.</p>
 * <p>
 * <code>BasicAuthHttpInvokerProxyFactoryBean</code> implements
 * <code>AuthenticationAware</code> in order to get notifications of changes in
 * the user's credentials. Please see the class documentation for
 * <code>AuthenticationAware</code> above to see how to properly configure the
 * application context so that authentication changes are broadcast properly.
 *
 * <h2>Hession and Burlap proxies</h2>
 *
 * <P>If your application uses either the Hessian or Burlap remoting classes to
 * access your business objects on the server, you will want to register
 * <code>RemotingSecurityConfigurer</code> in your application context.</P>
 *
 * <P>RemotingSecurityConfigurer listens for login and logout events and
 * updates the usernames and passwords associated with any of your registered
 * remoting proxy factories. This causes BASIC authentication to be used in the
 * header of the remoting requests.</P>
 *
 * <h2>Server Side Configuration</h2>
 *
 * <P>On the server side you will need to register Spring Security's
 * BasicProcessingFilter so BASIC authentication headers can be processed.
 * You'd need to do this if you're using Spring Security with any form
 * of BASIC authentication (it is not an RCP-specific requirement).  Here is
 * an example of how you might configure this in the application context of
 * your server:</P>
 *
 * <pre>
 * <code>
 * 	&lt;bean id="basicProcessingFilter"
 *         class="org.springframework.security.ui.basicauth.BasicProcessingFilter"&gt;
 * 		&lt;property name="authenticationManager"&gt;
 * 			&lt;ref bean="authenticationManager" /&gt;
 * 		&lt;/property&gt;
 * 		&lt;property name="authenticationEntryPoint"&gt;
 * 			&lt;ref bean="basicProcessingFilterEntryPoint" /&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="basicProcessingFilterEntryPoint"
 *         class="org.springframework.security.ui.basicauth.BasicProcessingFilterEntryPoint"&gt;
 * 		&lt;property name="realmName"&gt;
 * 			&lt;value&gt;My Realm&lt;/value&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="httpSessionContextIntegrationFilter"
 *         class="org.springframework.security.context.HttpSessionContextIntegrationFilter"&gt;
 * 		&lt;property name="allowSessionCreation"&gt;
 * 			&lt;value&gt;false&lt;/value&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;!-- Allows remote clients to check if a username/password is valid --&gt;
 * 	&lt;bean id="remoteAuthenticationManager"
 *         class="org.springframework.security.providers.rcp.RemoteAuthenticationManagerImpl"&gt;
 * 		&lt;property name="authenticationManager"&gt;
 * 			&lt;ref bean="authenticationManager" /&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="authenticationManager"
 *         class="org.springframework.security.providers.ProviderManager"&gt;
 * 		&lt;property name="providers"&gt;
 * 			&lt;list&gt;
 * 				&lt;ref bean="daoAuthenticationProvider" /&gt;
 * 			&lt;/list&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;bean id="daoAuthenticationProvider"
 *         class="org.springframework.security.providers.dao.DaoAuthenticationProvider"&gt;
 * 		&lt;property name="authenticationDao"&gt;
 * 			&lt;ref bean="authenticationDao" /&gt;
 * 		&lt;/property&gt;
 * 	&lt;/bean&gt;
 *
 * 	&lt;!--  Special implementation to get authentication data  --&gt;
 * 	&lt;bean id="authenticationDao"
 * 		class="com.myco..security.MyAuthenticationDao"&gt;
 * 	&lt;/bean&gt;
 * </code>
 * </pre>
 * <p>And in the <strong>web.xml</strong> you might install it like this:</p>
 * <pre>
 * <code>
 *     &lt;!-- Security configuration --&gt;
 *     &lt;filter&gt;
 *         &lt;filter-name&gt;Spring Security HTTP Session Integration&lt;/filter-name&gt;
 *         &lt;filter-class&gt;org.springframework.security.util.FilterToBeanProxy&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;targetClass&lt;/param-name&gt;
 *             &lt;param-value&gt;org.springframework.security.context.HttpSessionContextIntegrationFilter&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 *
 *     &lt;filter-mapping&gt;
 *       &lt;filter-name&gt;Spring Security HTTP Session Integration&lt;/filter-name&gt;
 *       &lt;url-pattern&gt;/context/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 *
 *     &lt;filter&gt;
 *         &lt;filter-name&gt;Spring Security HTTP BASIC Authorization Filter&lt;/filter-name&gt;
 *         &lt;filter-class&gt;org.springframework.security.util.FilterToBeanProxy&lt;/filter-class&gt;
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;targetClass&lt;/param-name&gt;
 *             &lt;param-value&gt;org.springframework.security.ui.basicauth.BasicProcessingFilter&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     &lt;/filter&gt;
 *
 *     &lt;filter-mapping&gt;
 *       &lt;filter-name&gt;Spring Security HTTP BASIC Authorization Filter&lt;/filter-name&gt;
 *       &lt;url-pattern&gt;/context/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 * </code>
 * </pre>
 *
 * <h1>Action Control</h1>
 *
 * <P>Coming soon... The general idea will be CommandActions listen for
 * events and update their visibility and enable/disabled status based on
 * delegation to a security manager. The security manager will indicate the
 * expected state based on granted authorities held.</p>
 */
package org.springframework.richclient.security;