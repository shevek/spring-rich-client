package org.springframework.richclient.exceptionhandling;

/**
 * A paramater value to determines if the user should or should not be asked or forced
 * to shutdown the application when an exception occurs.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public enum ShutdownPolicy {

    NONE,
    ASK,
    OBLIGATE
    
}
