package org.springframework.richclient.exceptionhandling;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;

import javax.swing.*;

/**
 * Logs a throwable and shows a dialog about it to the user.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public abstract class AbstractDialogExceptionHandler extends AbstractLoggingExceptionHandler
        implements InitializingBean {

    private static final String DIALOG_EXCEPTION_HANDLER_KEY = "dialogExceptionHandler";

    protected MessageSourceAccessor messageSourceAccessor;

    protected boolean modalDialog = true;
    protected ShutdownPolicy shutdownPolicy = ShutdownPolicy.ASK;

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * Where or not the shown dialog should be modal (see JDialog API).
     * The default is true;
     * @param modalDialog
     */
    public void setModalDialog(boolean modalDialog) {
        this.modalDialog = modalDialog;
    }

    /**
     * Wheter or not the user should be asked or obligated to shutdown the application.
     * The default is ASK.
     * @param shutdownPolicy
     */
    public void setShutdownPolicy(ShutdownPolicy shutdownPolicy) {
        this.shutdownPolicy = shutdownPolicy;
    }

    public void afterPropertiesSet() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor)
                    ApplicationServicesLocator.services().getService(MessageSourceAccessor.class);
        }
    }


    public void notifyUserAboutException(Thread thread, Throwable throwable) {
        Object[] options;
        // TODO implement ability to deal with mnemonics (for example "&Shutdown" in messages.properties)
        switch (shutdownPolicy) {
            case NONE:
                options = new String[]{
                        messageSourceAccessor.getMessage(DIALOG_EXCEPTION_HANDLER_KEY + ".none.ok")};
                break;
            case ASK:
                options = new String[]{
                        messageSourceAccessor.getMessage(DIALOG_EXCEPTION_HANDLER_KEY + ".ask.shutdown"),
                        messageSourceAccessor.getMessage(DIALOG_EXCEPTION_HANDLER_KEY + ".ask.continue")};
                break;
            case OBLIGATE:
                options = new String[]{
                        messageSourceAccessor.getMessage(DIALOG_EXCEPTION_HANDLER_KEY + ".obligate.shutdown")};
                break;
            default:
                // Can not occur and if it does it will crash the event thread
                throw new IllegalStateException("Unrecognized shutdownPolicy: " + shutdownPolicy);
        }
        int result = JOptionPane.showOptionDialog(
                resolveParentFrame(),
                createExceptionContent(throwable),
                resolveExceptionCaption(throwable),
                JOptionPane.DEFAULT_OPTION,
                resolveMessageType(), null,
                options, options[0]);
        if ((shutdownPolicy == ShutdownPolicy.ASK && result == 0)
                || shutdownPolicy == ShutdownPolicy.OBLIGATE) {
            logger.info("Shutting down due to uncaught exception.");
            try {
                if (Application.isLoaded()) {
                    Application.instance().close(true, 1);
                }
            } finally {
                // In case the instance() method throws an exception and an exit didn't occur
                System.exit(2);
            }
        }
    }

    protected JFrame resolveParentFrame() {
        ApplicationWindow activeWindow = Application.isLoaded() ? Application.instance().getActiveWindow() : null;
        return (activeWindow == null) ? null : activeWindow.getControl();
    }

    public abstract Object createExceptionContent(Throwable throwable);

    public abstract String resolveExceptionCaption(Throwable throwable);

    private int resolveMessageType() {
        switch (logLevel) {
            case TRACE:
            case DEBUG:
            case INFO:
                return JOptionPane.INFORMATION_MESSAGE;
            case WARN:
                return JOptionPane.WARNING_MESSAGE;
            case ERROR:
            case FATAL:
            default:
                return JOptionPane.ERROR_MESSAGE;
        }
    }

}
