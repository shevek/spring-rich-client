package org.springframework.richclient.exceptionhandling;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.jdic.desktop.Message;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * <p>
 * This email reporter can be added as {@link ErrorReporter} to the
 * {@link JXErrorDialogExceptionHandler}. The email reporter uses the
 * <a href="https://jdic.dev.java.net/">JDIC</a> library to access your mail client. To
 * use and deploy this correctly, you need to have the correct native libraries
 * for your platform and have them added to your VM startup
 * (-Djava.library.path).
 * </p>
 * <p>
 * The following libs are needed:
 * </p>
 * <ul>
 * <li><em>jdic-shared</em>: this one is always needed, shared across
 * platforms.</li>
 * <li><em>jdic-stub-{linux/windows}</em>: platform specific java classes.</li>
 * <li><em>jdic-native-{linux/windows}</em>: platform specific native
 * libraries.</li>
 * </ul>
 * <p>
 * During development, maven can add the correct jars to your classpath by using
 * a profile that is os specific (see pom.xml of spring-richclient-jdk5). Note
 * that you still have to add the native libraries to your environment. You can
 * do this by unpacking the jdic-native-* file and setting the
 * -Djava.library.path to that directory.
 * </p>
 * <p>
 * In production the same setup should be used. A webstart app should use os
 * specific and native dependencies to have the correct jars downloaded and the
 * native library unpacked and added to the environment.
 * </p>
 *
 * @author Jan Hoskens
 * @author Geoffrey De Smet
 */
public class JdicEmailNotifierErrorReporter implements ErrorReporter, BeanNameAware, InitializingBean {

    private MessageSourceAccessor messageSourceAccessor;

    private boolean outlookWorkaroundEnabled = true;
    
    private String id = null;

    public void setOutlookWorkaroundEnabled(boolean outlookWorkaroundEnabled) {
        this.outlookWorkaroundEnabled = outlookWorkaroundEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBeanName(String beanName) {
        if (getId() == null) {
            setId(beanName);
        }
    }

    public void afterPropertiesSet() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);
        }
    }

    public void reportError(ErrorInfo info) {
        Message mail = new Message();

        String mailTo = getMessageByKeySuffix(".mailTo");
        if (!StringUtils.isEmpty(mailTo)) {
            boolean doOutlookWorkaround = false;
            if (outlookWorkaroundEnabled) {
                boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
                if (isWindows) {
                    doOutlookWorkaround = JOptionPane.showConfirmDialog(null,
                            getMessageByKeySuffix(".isOutlook.message"),
                            getMessageByKeySuffix(".isOutlook.title"),
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                            == JOptionPane.YES_OPTION;
                }
            }
            String[] mailToTokens = mailTo.split(";");
            List<String> toAddrs = new ArrayList<String>(mailToTokens.length);
            for (String mailToToken : mailToTokens)
            {
                String trimmedMailToToken = mailToToken.trim();
                if (!StringUtils.isEmpty(trimmedMailToToken)) {
                    if (doOutlookWorkaround) {
                        // The standard is no prefix SMTP
                        // Outlook Express supposidly works with or without prefix SMTP
                        // Outlook (like in Office) works only with prefix SMTP
                        // Thunderbird works always without prefix SMTP.
                        // Thunderbirds works sometimes with prefix SMTP: it even differs from Vista to Vista
                        trimmedMailToToken = "SMTP:" + trimmedMailToToken;
                    }
                    toAddrs.add(trimmedMailToToken);
                }
            }
            mail.setToAddrs(toAddrs);
        }

        Throwable errorException = info.getErrorException();
        Object[] messageParams;
        if (errorException != null) {
            messageParams = new Object[] {
                errorException,
                getStackTraceString(errorException)
            };
        } else {
            messageParams = new Object[] {
                info.getBasicErrorMessage(),
                info.getDetailedErrorMessage()
            };
        }

        String subject = getMessageByKeySuffix(".subject", messageParams);
        mail.setSubject(subject);

        String body = getMessageByKeySuffix(".body", messageParams);
        mail.setBody(body);

        try {
            Desktop.mail(mail);
        } catch (LinkageError e) {
            // Thrown by JDIC 0.9.3 on linux (and probably windows too) when native jars are not used properly
            String message = getMessageByKeySuffix(".noNativeJdic");
            throw new IllegalStateException(message, e);
        } catch (NullPointerException e) {
            String message = getMessageByKeySuffix(".noDefaultMailClient");
            throw new IllegalStateException(message, e);
        } catch (DesktopException e) {
            String message = getMessageByKeySuffix(".mailException");
            throw new IllegalStateException(message, e);
        }
    }

    protected String getMessageByKeySuffix(String keySuffix) {
        return getMessageByKeySuffix(keySuffix, null);
    }

    protected String getMessageByKeySuffix(String keySuffix, Object[] params) {
        List<String> messageKeyList = new ArrayList<String>();
        if (getId() != null) {
            messageKeyList.add(getId() + keySuffix);
        }
        messageKeyList.add("jdicEmailNotifierErrorReporter" + keySuffix);
        messageKeyList.add("emailNotifierErrorReporter" + keySuffix);
        String[] messagesKeys = messageKeyList.toArray(new String[messageKeyList.size()]);
        return messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                messagesKeys, params, messagesKeys[0]));
    }

    protected String getStackTraceString(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        t.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }

}