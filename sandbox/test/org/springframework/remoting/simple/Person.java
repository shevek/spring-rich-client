package org.springframework.remoting.simple;

import java.io.Serializable;

/**
 * @author  oliverh
 */
public class Person implements IPerson, Serializable {

    private String name;

    private int age;

    public Person() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}