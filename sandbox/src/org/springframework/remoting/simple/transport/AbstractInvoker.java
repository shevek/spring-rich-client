/*
 * Copyright 2004 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.remoting.simple.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.remoting.simple.protocol.Request;

/**
 * @author  oliverh
 * @version $Revision$ $Date$
 */
public class AbstractInvoker {

    private List invocationListeners;

    public void addInvocationListener(InvocationListener invocationListener) {
        if (invocationListeners == null) {
            invocationListeners = new ArrayList();
        }
        invocationListeners.add(invocationListener);
    }

    public void removeInvocationListener(InvocationListener invocationListener) {
        if (invocationListeners == null) {
            return;
        }
        invocationListeners.remove(invocationListener);
    }

    protected void firePreInvocation(Request request) {
        if (invocationListeners == null) {
            return;
        }
        for (Iterator i = invocationListeners.iterator(); i.hasNext();) {
            ((InvocationListener)i.next()).invocationStarting(request);
        }
    }

    protected void firePostInvocation(Request request, Object result) {
        if (invocationListeners == null) {
            return;
        }
        for (Iterator i = invocationListeners.iterator(); i.hasNext();) {
            ((InvocationListener)i.next()).invocationComplete(request, result);
        }
    }

}