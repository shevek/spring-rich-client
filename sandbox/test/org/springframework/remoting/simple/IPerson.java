package org.springframework.remoting.simple;


/**
 * @author  oliverh
 */
public interface IPerson {
    public void setName(String name);
    
    public String getName();
    
    public void setAge(int age);

    public int getAge();
}