/*
 * Copyright 2004 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.remoting.simple;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author  oliverh
 * @version $Revision$ $Date$
 */
public class RemoteTestBean implements IRemoteTestBean, Serializable {
    private boolean b;

    private int i;

    private double d;

    private String s;

    private Collection c;

    private Object child;

    private Exception ex;

    public RemoteTestBean() {
    }

    public void setBoolean(boolean b) {
        this.b = b;
    }

    public boolean getBoolean() {
        return b;
    }

    public void setInt(int i) {
        this.i = i;
    }

    public int getInt() {
        return i;
    }

    public void setDouble(double d) {
        this.d = d;
    }

    public double getDouble() {
        return d;
    }

    public void setString(String s) {
        this.s = s;
    }

    public String getString() {
        return s;
    }

    public void setCollection(Collection c) {
        this.c = c;
    }

    public Collection getCollection() {
        return c;
    }

    public void setChild(Object child) {
        this.child = child;
    }

    public Object getChild() {
        return child;
    }

    public int bounce(Exception t) throws Exception {
        ex = t;
        throw t;
    }

    public Exception getLastBounce() {
        Exception lastBounce = ex;
        ex = null;
        return lastBounce;
    }
}