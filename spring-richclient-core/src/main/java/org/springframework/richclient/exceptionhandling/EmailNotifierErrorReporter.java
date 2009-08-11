package org.springframework.richclient.exceptionhandling;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.jdic.desktop.Message;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * <p>
 * This email reporter can be added as {@link ErrorReporter} to the
 * {@link JXErrorDialogExceptionHandler}. The email reporter uses the JDIC (
 * {@link https://jdic.dev.java.net/}) library to access your mail client. To
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
 *
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
 */
public class EmailNotifierErrorReporter implements ErrorReporter, BeanNameAware, InitializingBean {

    private MessageSourceAccessor messageSourceAccessor;

    private String id;

    public void afterPropertiesSet() {
        if (messageSourceAccessor == null) {
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);
        }
        if (getId() == null) {
            setId(StringUtils.uncapitalize(getClass().getSimpleName()));
        }
    }

    public void reportError(ErrorInfo info) throws NullPointerException {
        Message mail = new Message();

        Object params[] = new Object[] { info.getBasicErrorMessage(), info.getDetailedErrorMessage() };
        if (info.getErrorException() != null) {
            params = new Object[] { info.getErrorException(), getStackTraceString(info.getErrorException()) };
        }

        String body = messageSourceAccessor.getMessage(getId() + ".body", params, "");
        String title = messageSourceAccessor.getMessage(getId() + ".title", "");

        String adresses = messageSourceAccessor.getMessage(getId() + ".mailTo", "");
        if (!StringUtils.isEmpty(adresses)) {
            mail.setToAddrs(Arrays.<String> asList(adresses.split(";")));
        }

        mail.setSubject(title);
        mail.setBody(body);

        try {
            Desktop.mail(mail);
        }
        catch (DesktopException e) {
            String mailExceptionMessage = messageSourceAccessor.getMessage(getId() + ".mailException", "");
            throw new RuntimeException(mailExceptionMessage, e);
        }

    }

    protected String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBeanName(String name) {
        if (getId() == null) {
            setId(name);
        }
    }
}