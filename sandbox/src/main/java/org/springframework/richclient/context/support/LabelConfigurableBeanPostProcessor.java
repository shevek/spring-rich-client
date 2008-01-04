package org.springframework.richclient.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.MessageSource;
import org.springframework.richclient.core.LabelConfigurable;
import org.springframework.richclient.core.LabelInfo;
import org.springframework.util.StringUtils;

public class LabelConfigurableBeanPostProcessor extends AbstractConfigurableBeanPostProcessor {

    /** The key fragment used to retrieve the label for a given object. */
    public static final String LABEL_KEY = "label";

    protected LabelConfigurableBeanPostProcessor(MessageSource messageSource) {
        super(messageSource);
    }

    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof LabelConfigurable) {
            LabelConfigurable configurable = (LabelConfigurable) bean;

            String labelStr = loadMessage(name + "." + LABEL_KEY);

            if (StringUtils.hasText(labelStr)) {
                LabelInfo labelInfo = LabelInfo.valueOf(labelStr);
                configurable.setLabelInfo(labelInfo);
            }
        }

        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

}
