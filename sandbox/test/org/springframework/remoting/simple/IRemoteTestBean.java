package org.springframework.remoting.simple;

import java.util.Collection;

/**
 * @author  oliverh
 */
public interface IRemoteTestBean {
    public void setBoolean(boolean b);

    public boolean getBoolean();

    public void setInt(int i);

    public int getInt();

    public void setDouble(double d);

    public double getDouble();

    public void setString(String s);

    public String getString();

    public void setCollection(Collection c);

    public Collection getCollection();

    public void setChild(Object child);

    public Object getChild();

    public int bounce(Exception t) throws Exception;
}