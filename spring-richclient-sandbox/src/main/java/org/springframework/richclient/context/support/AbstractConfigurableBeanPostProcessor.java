package org.springframework.richclient.context.support;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.richclient.util.Assert;

public abstract class AbstractConfigurableBeanPostProcessor implements BeanPostProcessor {

    private static final Log logger = LogFactory.getLog(AbstractConfigurableBeanPostProcessor.class);

    private MessageSource messageSource;

    protected AbstractConfigurableBeanPostProcessor(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Attempts to load the message corresponding to the given message code using this instance's {@link MessageSource}
     * and locale.
     * 
     * @param messageCode
     *            The message code that will be used to retrieve the message. Must not be null.
     * @return The message for the given code, or null if the message code could not be found.
     * 
     * @throws IllegalArgumentException
     *             if {@code messageCode} is null.
     */
    protected String loadMessage(String messageCode) {

        Assert.required(messageCode, "messageCode");

        if (logger.isDebugEnabled()) {
            logger.debug("Resolving label with code '" + messageCode + "'");
        }

        try {
            return messageSource.getMessage(messageCode, null, Locale.getDefault());
        }
        catch (NoSuchMessageException e) {

            if (logger.isInfoEnabled()) {
                logger.info("The message source is unable to find message code [" + messageCode
                        + "]. Ignoring and returning null.");
            }

            return null;
        }

    }
}
