package org.springframework.richclient.context.support;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.richclient.core.LabelConfigurable;
import org.springframework.richclient.core.LabelInfo;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.util.StringUtils;

public class LabelConfigurableBeanPostProcessor implements BeanPostProcessor {
	/** The key fragment used to retrieve the label for a given object. */
	public static final String LABEL_KEY = "label";

	private MessageSource messageSource;

	public LabelConfigurableBeanPostProcessor(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		if (bean instanceof LabelConfigurable) {
			LabelConfigurable configurable = (LabelConfigurable) bean;

			try {
				String label = messageSource.getMessage(new DefaultMessageSourceResolvable(name + "." + LABEL_KEY),
						Locale.getDefault());

				if (StringUtils.hasText(label)) {
					configurable.setLabelInfo(LabelInfo.valueOf(label));
				}
			}
			catch (NoSuchMessageException e) {
				throw new BeanInitializationException("Unable to initialize bean " + name, e);
			}
		}

		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
		return bean;
	}
}
