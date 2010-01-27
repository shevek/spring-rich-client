package org.springframework.richclient.util;

import javax.swing.*;

/**
 * Helper class for EDT related methods
 */
public class SwingUtilitiesHelper {
    /**
     * Executes a runnable piece of code, altering GUI components, which should be done on the EDT. If
     * the current thread is the EDT, the code is executed normally, otherwise it is wrapped in a
     * SwingUtilities.invokeLater(...).
     * @param runnable The runnable to be executed on the EDT
     */
    public static void executeWithEDTCheck(Runnable runnable)
    {
        if(SwingUtilities.isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
