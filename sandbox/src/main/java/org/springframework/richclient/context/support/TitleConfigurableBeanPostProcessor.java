package org.springframework.richclient.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.MessageSource;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.util.StringUtils;

public class TitleConfigurableBeanPostProcessor extends AbstractConfigurableBeanPostProcessor {
    /** The key fragment used to retrieve the title for a given object. */
    public static final String TITLE_KEY = "title";

    public TitleConfigurableBeanPostProcessor(MessageSource messageSource) {
        super(messageSource);
    }
    
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof TitleConfigurable) {
            TitleConfigurable configurable = (TitleConfigurable) bean;

            String title = loadMessage(name + "." + TITLE_KEY);

            if (StringUtils.hasText(title)) {
                configurable.setTitle(title);
            }
        }

        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

}
