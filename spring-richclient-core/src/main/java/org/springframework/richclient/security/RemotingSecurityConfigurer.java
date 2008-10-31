package org.springframework.richclient.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.remoting.caucho.BurlapProxyFactoryBean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.jaxrpc.JaxRpcPortProxyFactoryBean;
import org.springframework.richclient.application.Application;
import org.springframework.security.Authentication;

/**
 * Correctly configures the username and password on Spring's remoting proxy
 * factory beans.
 * 
 * <P>
 * This bean works with "Spring Remoting Proxy Factories" defined in the
 * application context. Presently this includes the following Spring classes:
 * {@link HessianProxyFactoryBean},{@link BurlapProxyFactoryBean}and {@link
 * JaxRpcPortProxyFactoryBean}.
 * </p>
 * 
 * <P>
 * This bean listens for any <code>ClientSecurityEvent</code> and responds as
 * follows:
 * </p>
 * 
 * <P>
 * Upon receipt of a {@link LoginEvent}, any Spring Remoting Proxy Factories
 * will be located. Each located bean will have its username and password
 * methods set to the <code>LoginEvent</code>'s principal and credentials
 * respectively.
 * </p>
 * 
 * <P>
 * Upon receipt of a {@link LogoutEvent}, any Spring Remoting Proxy Factories
 * will be located. Each located bean will have its username and password
 * methods set to <code>null</code>.
 * </p>
 * 
 * @author Ben Alex
 */
public class RemotingSecurityConfigurer implements ApplicationListener {
    //~ Static fields/initializers
    // =============================================

    protected static final Log logger = LogFactory.getLog(RemotingSecurityConfigurer.class);

    //~ Methods
    // ================================================================

    public void onApplicationEvent(ApplicationEvent event) {
        if (logger.isDebugEnabled() && event instanceof ClientSecurityEvent) {
            logger.debug("Processing event: " + event.toString());
        }

        if (event instanceof LoginEvent) {
            Authentication authentication = (Authentication)event.getSource();
            updateExporters(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
        }
        else if (event instanceof LogoutEvent) {
            updateExporters(null, null);
        }
    }

    /**
     * Get the list of proxy factory beans that need to be updated.
     * @return Array of beans to update
     */
    private Object[] getExporters() {
        ApplicationContext appCtx = Application.instance().getApplicationContext();
        List list = new Vector();

        Class[] types = new Class[] {
                HessianProxyFactoryBean.class,
                BurlapProxyFactoryBean.class,
                JaxRpcPortProxyFactoryBean.class
        };
        
        for( int i = 0; i < types.length; i++ ) {
            Map map = appCtx.getBeansOfType(types[i], false, true);
            Iterator iter = map.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                String beanName = (String)entry.getKey();
                if( beanName.startsWith("&") ) {
                    list.add(entry.getValue());
                }
            }
        }

        return list.toArray();
    }

    private void updateExporters(String username, String password) {
        Object[] factories = getExporters();

        for (int i = 0; i < factories.length; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("Updating " + factories[i].toString() + " to username: " + username
                        + "; password: [PROTECTED]");
            }

            try {
                Method method = factories[i].getClass().getMethod("setUsername", new Class[] { String.class });
                method.invoke(factories[i], new Object[] { username });
            }
            catch (NoSuchMethodException ignored) {
                logger.error("Could not call setter", ignored);
            }
            catch (IllegalAccessException ignored) {
                logger.error("Could not call setter", ignored);
            }
            catch (InvocationTargetException ignored) {
                logger.error("Could not call setter", ignored);
            }

            try {
                Method method = factories[i].getClass().getMethod("setPassword", new Class[] { String.class });
                method.invoke(factories[i], new Object[] { password });
            }
            catch (NoSuchMethodException ignored) {
                logger.error("Could not call setter", ignored);
            }
            catch (IllegalAccessException ignored) {
                logger.error("Could not call setter", ignored);
            }
            catch (InvocationTargetException ignored) {
                logger.error("Could not call setter", ignored);
            }
        }
    }
}