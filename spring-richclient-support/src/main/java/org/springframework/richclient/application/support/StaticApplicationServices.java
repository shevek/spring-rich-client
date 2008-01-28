/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.application.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.util.Assert;

public class StaticApplicationServices implements ApplicationServices {

    private final Map services = Collections.synchronizedMap(new HashMap());

    public Object getService(Class serviceType) {
        Assert.required(serviceType, "serviceType");
        Object service = services.get(serviceType);
        if (service == null) {
            throw new UnsupportedOperationException("No service of type '" + serviceType + "' found.");
        }
        return service;
    }

    public boolean containsService(Class serviceType) {
        Assert.required(serviceType, "serviceType");
        return services.containsKey(serviceType);
    }

    public void registerService(Object service, Class serviceInterface) {
        Assert.required(service, "service");
        Assert.required(serviceInterface, "serviceInterface");
        services.put(serviceInterface, service);
    }
}