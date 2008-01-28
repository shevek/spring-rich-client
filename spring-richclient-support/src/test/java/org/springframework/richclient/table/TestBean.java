/**
 * 
 */
package org.springframework.richclient.table;

public class TestBean {
    private String property;

    public TestBean(String p) {
        property = p;
    }

    public String toString() {
        return property;
    }
}