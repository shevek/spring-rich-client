/*
 * Copyright Tui Software 2006
 * 
 * $Id$
 */
package org.springframework.richclient.util;

import junit.framework.TestCase;


/**
 * Unit tests for the {@link Assert} class.
 *
 * @author Kevin Stembridge
 * @since 0.3
 *
 */
public class AssertTests extends TestCase {

    /**
     * Test method for {@link Assert#required(java.lang.Object, java.lang.String)}.
     */
    public final void testRequired() {
        
        Assert.required(new Object(), "object");
        Assert.required(new Object(), null);
        
        try {
            Assert.required(null, "bogus");
            fail("Should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            //do nothing, test succeeded
        }
        
    }

    /**
     * Test method for {@link Assert#noElementsNull(java.lang.Object[])}.
     */
    public final void testNoElementsNull() {
        
        Object[] array = new Object[0];
        
        Assert.noElementsNull(array, null);
        Assert.noElementsNull(array, "bogusArray");
        
        try {
            Assert.noElementsNull(null, "bogusArray");
            fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //do nothing, test succeeded
        }
        
        array = new Object[1];
        
        try {
            Assert.noElementsNull(null, "bogusArray");
            fail("Should have thrown an IllegalArgumentException for a non-null array with a null element");
        }
        catch (IllegalArgumentException e) {
            //do nothing, test succeeded
        }
        
        array = new Object[] {"bogus", null};
        
        try {
            Assert.noElementsNull(null, "bogusArray");
            fail("Should have thrown an IllegalArgumentException for a non-null array with a null element");
        }
        catch (IllegalArgumentException e) {
            //do nothing, test succeeded
        }
        
    }

}
