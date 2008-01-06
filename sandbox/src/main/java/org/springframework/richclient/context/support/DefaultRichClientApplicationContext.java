package org.springframework.richclient.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.context.RichClientApplicationContext;

public class DefaultRichClientApplicationContext extends ClassPathXmlApplicationContext implements RichClientApplicationContext {

	public DefaultRichClientApplicationContext(String path, Class clazz) throws BeansException {
        super(path, clazz);
        
    }

    public DefaultRichClientApplicationContext(String configLocation) throws BeansException {
        super(configLocation);
        
    }

    public DefaultRichClientApplicationContext(String[] configLocations, ApplicationContext parent)
            throws BeansException {
        super(configLocations, parent);
        
    }

    public DefaultRichClientApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
            throws BeansException {
        super(configLocations, refresh, parent);
        
    }

    public DefaultRichClientApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        super(configLocations, refresh);
        
    }

    public DefaultRichClientApplicationContext(String[] arg0, Class arg1, ApplicationContext arg2)
            throws BeansException {
        super(arg0, arg1, arg2);
        
    }

    public DefaultRichClientApplicationContext(String[] paths, Class clazz) throws BeansException {
        super(paths, clazz);
        
    }

    public DefaultRichClientApplicationContext(String[] configLocations) throws BeansException {
        super(configLocations);
        
    }
    
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor(new TitleConfigurableBeanPostProcessor(this));
        beanFactory.addBeanPostProcessor(new LabelConfigurableBeanPostProcessor(this));
        
        beanFactory.registerScope("window", new ApplicationWindowScope());
    }
}
