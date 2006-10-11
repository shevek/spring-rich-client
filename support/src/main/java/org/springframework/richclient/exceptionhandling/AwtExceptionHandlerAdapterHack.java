package org.springframework.richclient.exceptionhandling;

import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * It's impossible to set an exception handler for the event thread in jdk 1.4 (and 1.5).
 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4714232
 * So this effectively only works in Sun's JDK.
 *
 * @author Geoffrey De Smet
 * @since 0.3
 */
public class AwtExceptionHandlerAdapterHack {

    private static final String SUN_AWT_EXCEPTION_HANDLER_KEY = "sun.awt.exception.handler";

    /**
     * Since Sun's JDK constructs the instance, its impossible to inject dependencies into it,
     * except by a static reference like this.
     */
    private static RegisterableExceptionHandler registerableUncaughtExceptionHandler = null;

    public static void registerExceptionHandler(RegisterableExceptionHandler registerableUncaughtExceptionHandler) {
        if (AwtExceptionHandlerAdapterHack.registerableUncaughtExceptionHandler != null) {
            throw new IllegalStateException("There is already an uncaughtExceptionHandler set.");
        }
        AwtExceptionHandlerAdapterHack.registerableUncaughtExceptionHandler = registerableUncaughtExceptionHandler;
        // Registers this class with the system properties so Sun's JDK can pick it up.
        Properties systemProperties = System.getProperties();
        if (systemProperties.get(SUN_AWT_EXCEPTION_HANDLER_KEY) != null) {
            throw new IllegalStateException(
                    "The exception handler is already set with " + SUN_AWT_EXCEPTION_HANDLER_KEY);
        }
        systemProperties.put(SUN_AWT_EXCEPTION_HANDLER_KEY, AwtExceptionHandlerAdapterHack.class.getName());
    }


    /**
     * No-arg constructor required so Sun's JDK can construct the instance.
     */
    public AwtExceptionHandlerAdapterHack() {
    }


    public void handle(Throwable throwable) {
        if (registerableUncaughtExceptionHandler == null) {
            LogFactory.getLog(getClass()).error("No uncaughtExceptionHandler set while handling throwable.", throwable);
        }
        registerableUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
    }

}
