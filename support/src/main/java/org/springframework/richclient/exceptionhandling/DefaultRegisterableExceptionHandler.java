package org.springframework.richclient.exceptionhandling;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.Severity;
import org.springframework.util.StringUtils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @TODO extends AbstractRegisterableExceptionHandler as soon as spring-richclient is minimum 1.5
 * @author Geoffrey De Smet
 */
public class DefaultRegisterableExceptionHandler implements RegisterableExceptionHandler {

    /**
     * Currently on registers for the event thread, not for other threads.
     * @TODO remove as soon as this class extends AbstractRegisterableExceptionHandler
     */
    public void registerExceptionHandler() {
        AwtExceptionHandlerAdapterHack.registerExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        LogFactory.getLog(ApplicationLifecycleAdvisor.class).error(throwable.getMessage(), throwable);
        String exceptionMessage;
        if (throwable instanceof MessageSourceResolvable) {
            exceptionMessage = ((MessageSourceAccessor) ApplicationServicesLocator.services()
                    .getService(MessageSourceAccessor.class))
                    .getMessage((MessageSourceResolvable) throwable);
        } else {
            exceptionMessage = throwable.getLocalizedMessage();
        }
        if (!StringUtils.hasText(exceptionMessage)) {
            String defaultMessage = "An application exception occurred.\nPlease contact your administrator.";
            exceptionMessage = ((MessageSourceAccessor) ApplicationServicesLocator.services()
                    .getService(MessageSourceAccessor.class))
                    .getMessage("applicationDialog.defaultException", defaultMessage);
        }

        Message message = new DefaultMessage(exceptionMessage, Severity.ERROR);
        ApplicationWindow activeWindow = Application.instance().getActiveWindow();
        JFrame parentFrame = (activeWindow == null) ? null : activeWindow.getControl();
        JOptionPane.showMessageDialog(parentFrame, message.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

}
