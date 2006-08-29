package org.springframework.richclient.exception;

//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

/**
 * Register it like this:
 * &lt;bean id="uncaughtExceptionHandler" lazy-init="false"
 *          class="org.springframework.richclient.exception.I18nUncaughtExceptionHandler" /&gt;
 *
 * And add this to your messages.properties:
 * java.lang.RuntimeException.caption = Unexpected problem
 * java.lang.RuntimeException.description = \
 *   The system experienced an unexpected problem.\n\
 *   \n\
 *   The exact problem is:\n\
 *   {0}
 *
 * java.lang.Error.caption = Serious problem
 * java.lang.Error.description = Serious problem:\n\
 *   {0}
 *
 * org.acegisecurity.AuthenticationCredentialsNotFoundException.caption = Unauthorized action
 * org.acegisecurity.AuthenticationCredentialsNotFoundException.description = \
 *   You don't have permission to do that.
 *
 * @TODO clean up this draft and merge other ppl's improvements in it.
 * @author Geoffrey De Smet
 */
public class I18nUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler, InitializingBean {

    public static final String REQUIRED_BEAN_NAME = "uncaughtExceptionHandler";

    protected final transient Log logger = LogFactory.getLog(getClass());

    private boolean registerAsDefaultOnInitialization = true;
    private boolean registerForEventThreadOnInitialization = true;
    private MessageSourceAccessor messageSourceAccessor;
    private int wrapLength = 120;
    private int identLength = 2;

    public boolean isRegisterAsDefaultOnInitialization() {
        return registerAsDefaultOnInitialization;
    }

    public void setRegisterAsDefaultOnInitialization(boolean registerAsDefaultOnInitialization) {
        this.registerAsDefaultOnInitialization = registerAsDefaultOnInitialization;
    }

    public MessageSourceAccessor getMessageSourceAccessor() {
        return messageSourceAccessor;
    }

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public void setWrapLength(int wrapLength) {
        this.wrapLength = wrapLength;
    }

    public void setIdentLength(int identLength) {
        this.identLength = identLength;
    }

    public void afterPropertiesSet() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
        }
        if (registerAsDefaultOnInitialization) {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
        if (registerForEventThreadOnInitialization) {
            // HACK because java 1.5 doesn't have an API to register an exception handler
            System.setProperty("sun.awt.exception.handler", ExceptionHandlerAdapterHack.class.getName());
        }
    }

    public static class ExceptionHandlerAdapterHack {
        public void handle(Throwable e) {
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
                    = (Thread.UncaughtExceptionHandler) Application.instance().getApplicationContext()
                    .getBean(REQUIRED_BEAN_NAME);
            if (uncaughtExceptionHandler == null) {
                throw new RuntimeException("No bean found with name " + REQUIRED_BEAN_NAME);
            }
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
        }
    }


    public void uncaughtException(Thread t, Throwable e) {
        logger.error("Uncaught throwable from thread " + t.getName(), e);
        List<String> messageCaptionKeyList = new ArrayList<String>();
        List<String> messageDescriptionKeyList = new ArrayList<String>();
        Class clazz = e.getClass();
        while (clazz != Object.class) {
            messageCaptionKeyList.add(clazz.getName() + ".caption");
            messageDescriptionKeyList.add(clazz.getName() + ".description");
            clazz = clazz.getSuperclass();
        }
        String[] parameters = new String[]{formatMessage(e.getMessage())};
        String caption = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                messageCaptionKeyList.toArray(new String[messageCaptionKeyList.size()]),
                parameters, messageCaptionKeyList.get(0)));
        String description = messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                messageDescriptionKeyList.toArray(new String[messageDescriptionKeyList.size()]),
                parameters, messageDescriptionKeyList.get(0)));
        showExceptionToUser(e, caption, description);
    }

    private String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        // TODO enable message formatting again (requires commons lang)
//        String formattedMessage = WordUtils.wrap(message, wrapLength);
//        if (identLength > 0) {
//            String identString = StringUtils.leftPad("", identLength);
//            formattedMessage = identString + formattedMessage.replace("\n", "\n" + identString);
//        }
//        return formattedMessage;
        return message;
    }

    public void showExceptionToUser(Throwable e, String caption, String description) {
        JOptionPane.showMessageDialog(null, description, caption, JOptionPane.ERROR_MESSAGE);
    }

}

