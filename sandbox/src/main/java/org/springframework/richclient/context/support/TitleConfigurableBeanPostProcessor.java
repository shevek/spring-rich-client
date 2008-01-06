package org.springframework.richclient.context.support;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.util.StringUtils;

public class TitleConfigurableBeanPostProcessor implements BeanPostProcessor {
	/** The key fragment used to retrieve the title for a given object. */
	public static final String TITLE_KEY = "title";

	private MessageSource messageSource;

	public TitleConfigurableBeanPostProcessor(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		if (bean instanceof TitleConfigurable) {
			TitleConfigurable configurable = (TitleConfigurable) bean;

			try {
				String title = messageSource.getMessage(new DefaultMessageSourceResolvable(name + "." + TITLE_KEY),
						Locale.getDefault());

				if (StringUtils.hasText(title)) {
					configurable.setTitle(title);
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
