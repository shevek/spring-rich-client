package org.springframework.richclient.context.support;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ApplicationWindowScope implements Scope {

    public Object get(String name, ObjectFactory objectFactory) {
        return null;
    }

    public String getConversationId() {
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        
    }

    public Object remove(String name) {
        return null;
    }

}
