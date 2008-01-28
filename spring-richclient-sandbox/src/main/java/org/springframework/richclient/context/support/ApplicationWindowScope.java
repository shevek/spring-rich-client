package org.springframework.richclient.context.support;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ApplicationWindowScope implements Scope {

	public ApplicationWindowScope() {
		System.out.println("ttt");
	}
	
    public Object get(String name, ObjectFactory objectFactory) {
        return objectFactory.getObject();
    }

    public String getConversationId() {
        return "ttt";
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        
    }

    public Object remove(String name) {
        return null;
    }

}
