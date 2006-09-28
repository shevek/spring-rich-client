/*
 * Copyright 2004-2005 the original author or authors.
 */
package org.springframework.binding.value.support;

import java.util.Map;

import org.springframework.binding.value.ValueModel;

/**
 * 
 * @author HP
 */
public class MapKeyAdapter extends AbstractValueModel {

    private ValueModel mapValueModel;

    private Object key;

    public MapKeyAdapter(ValueModel valueModel, Object key) {
        super();
        this.mapValueModel = valueModel;
        setKey(key);
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        Map map = (Map)mapValueModel.getValue();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public void setValue(Object value) {
        Map map = (Map)mapValueModel.getValue();
        if (map == null) {
            return;
        }
        map.put(key, value);
    }

}