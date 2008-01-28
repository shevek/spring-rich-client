package org.springframework.richclient.exceptionhandling.delegation;

/**
 * Purges a throwable, ussually by looking into it's chain.
 * Usefull for unwrapping WrapEverythingException etc.
 * 
 * @see DefaultExceptionPurger
 * @author Geoffrey De Smet
 * @since 0.3.0
 */
public interface ExceptionPurger {

    /**
     * Purges the throwable to unwrap it to find the most suitable throwable to evaluate or handle.
     * 
     * @param e the root exception or error
     * @return e or a chained Throwable which is part of e's chain
     */
    Throwable purge(Throwable e);
    
}
