package org.springframework.richclient.exceptionhandling;

import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * An exception handler that selects an appropriate exception handler from a list
 * based on the class of the thrown exception and delegates the handling of the exception to it.
 *
 * This class works very similar to catch statements:
 * the first delegate which can handle the exception will handle it.
 * For example, consider 3 delegates for the following classes in this order:
 * NullPointerException (1), RuntimeException (2), IllegalArgumentException (3).
 * A thrown IllegalArgumentException will be handled by the (2) handler. The (3) handler is useless.
 *
 * @author Geoffrey De Smet
 */
public class DelegatingExceptionHandler implements Thread.UncaughtExceptionHandler, InitializingBean {

    private final transient Log logger = LogFactory.getLog(getClass());

    private List<DelegatingExceptionHandlerDelegate> delegateList;

    /**
     * Sets the list of delegates.
     * This is not a map because the order is important
     * and delegate selection is not a simple key based selector.
     * @param delegateList a list of DelegatingExceptionHandlerDelegate
     */
    public void setDelegateList(List<DelegatingExceptionHandlerDelegate> delegateList) {
        this.delegateList = delegateList;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(delegateList, "The delegate list must contains at least one entry.");
    }

    /**
     * Delegates the throwable to the appropriate delegate exception handler.
     * @param thread the thread in which the throwable occurred
     * @param throwable the thrown throwable
     */
    public void uncaughtException(Thread thread, Throwable throwable) {
        for (DelegatingExceptionHandlerDelegate delegate : delegateList) {
            if (delegate.hasAppropriateHandler(throwable)) {
                Thread.UncaughtExceptionHandler exceptionHandler = delegate.getExceptionHandler();
                exceptionHandler.uncaughtException(thread, throwable);
                return;
            }
        }
        // A silent exception handler should be configured if it needs to be silent
        logger.error("No exception handler found for throwable", throwable);
    }

}
