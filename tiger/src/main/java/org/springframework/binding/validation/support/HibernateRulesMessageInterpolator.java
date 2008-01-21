package org.springframework.binding.validation.support;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.MessageInterpolator;
import org.hibernate.validator.Validator;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * Custom interpolator which requires a "messageSource" bean, which is a
 * MessageSource implementation. This way the validation messages from Hibernate
 * can be overwritten through the i18n facilities of Spring RCP.
 *
 * The different keys of the validators can be found in the Hibernate Reference,
 * or you can look in the DefaultValidationMessage.properties file inside the
 * hibernate-validator jar.
 *
 * @author Lieven Doclo
 *
 */
public class HibernateRulesMessageInterpolator implements MessageInterpolator {

	private MessageSourceAccessor messageSourceAccessor;

	private static Log log = LogFactory.getLog(HibernateRulesMessageInterpolator.class);

	private String annotationMessage;

	private String interpolateMessage;

	/**
	 * Initialize the MessageSourceAccessor by finding it inside the application
	 * Spring context.
	 */
	private void initializeMessageSourceAccessor() {
		this.messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
				MessageSourceAccessor.class);
	}

	/**
	 * Retrieve the message for the validator.
	 */
	public String interpolate(String message, Validator validator, MessageInterpolator defaultInterpolator) {
		if (annotationMessage != null && annotationMessage.equals(message)) {
			// short cut
			return interpolateMessage;
		}
		else {
			message = message.replaceAll("[\\{\\}]", "");
			String string = null;
			string = messageSourceAccessor != null ? messageSourceAccessor.getMessage(message, new Object[0], Locale
					.getDefault()) : null;
			if (StringUtils.isEmpty(string)) {
				log.info("Message not found in messageSourceAccessor (it may not exist), "
						+ "trying Hibernate default messages");
				return defaultInterpolator.interpolate(message, validator, defaultInterpolator);
			}
			return string;
		}
	}

	/**
	 * Create a new instance of the interpolator.
	 */
	public HibernateRulesMessageInterpolator() {
		initializeMessageSourceAccessor();
	}
}
