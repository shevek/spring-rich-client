package org.springframework.richclient.exceptionhandling.delegation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Geoffrey De Smet
 */
public class DefaultExceptionPurgerTests extends TestCase {

    // Chain A1->B1->C1->B2->D1
    private DException d1 = new DException();
    private BException b2 = new BException(d1);
    private CException c1 = new CException(b2);
    private BException b1 = new BException(c1);
    private AException a1 = new AException(b1);

    /**
     * {A} returns A1;
     * {B} returns B1;
     * {D} returns D1;
     * {Z) returns A1;
     * {C, Z} returns C1;
     * {B, D} returns B1;
     * {D, B} return B1;
     */
    public void testIncludedThrowableClassList() {
        assertEquals(a1, checkIncluded(AException.class));
        assertEquals(b1, checkIncluded(BException.class));
        assertEquals(d1, checkIncluded(DException.class));
        assertEquals(a1, checkIncluded(ZException.class));
        assertEquals(c1, checkIncluded(CException.class, ZException.class));
        assertEquals(b1, checkIncluded(BException.class, DException.class));
        assertEquals(b1, checkIncluded(DException.class, BException.class));
    }
    
    public Throwable checkIncluded(Class ... includedThrowableClasses) {
        return new DefaultExceptionPurger(Arrays.asList(includedThrowableClasses), null).purge(a1);
    }

    /**
     * {A} returns B1;
     * {B} returns D1;
     * {D} returns D1;
     * {Z) returns A1;
     * {C, Z} returns B2;
     * {C, D} returns D1;
     * {D, C} return D1;
     */
    public void testExcludedThrowableClassList() {
        assertEquals(b1, checkExcluded(AException.class));
        assertEquals(d1, checkExcluded(BException.class));
        assertEquals(d1, checkExcluded(DException.class));
        assertEquals(a1, checkExcluded(ZException.class));
        assertEquals(b2, checkExcluded(CException.class, ZException.class));
        assertEquals(d1, checkExcluded(CException.class, DException.class));
        assertEquals(d1, checkExcluded(DException.class, CException.class));
    }
    
    public Throwable checkExcluded(Class ... excludedThrowableClasses) {
        return new DefaultExceptionPurger(null, Arrays.asList(excludedThrowableClasses)).purge(a1);
    }
    
    private class AException extends RuntimeException {
        private AException(Throwable cause) {
            super(cause);
        }
    }
    private class BException extends RuntimeException {
        private BException(Throwable cause) {
            super(cause);
        }
    }
    private class CException extends RuntimeException {
        private CException(Throwable cause) {
            super(cause);
        }
    }
    private class DException extends RuntimeException {
    }
    private class ZException extends RuntimeException {
    }
    
}
