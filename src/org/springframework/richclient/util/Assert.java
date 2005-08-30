package org.springframework.richclient.util;


public class Assert extends org.springframework.util.Assert {

    /**
     * Assert that an object required; that is, it is not null.
     * <pre>
     * required(clazz, "class");</pre>
     * @param object the object to check
     * @param name the name of the object being checked
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void required(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " is required; it cannot be null");
        }
    }

}
