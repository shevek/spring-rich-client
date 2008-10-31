/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.binding.value.support;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.rules.closure.Closure;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * Testcase for <code>RefreshableValueHolder</code>
 * 
 * @author Peter De Bruycker
 */
public class RefreshableValueHolderTests extends TestCase {
    public void testWithLazyInitTrueAndAlwaysRefreshFalse() {
        Object returnValue = new Object();

        Closure refreshFunction = (Closure) EasyMock.createMock(Closure.class);
        EasyMock.expect(refreshFunction.call(null)).andReturn(returnValue);
        EasyMock.replay(refreshFunction);

        RefreshableValueHolder valueHolder = new RefreshableValueHolder(refreshFunction);
        // lazy init
        assertNull(valueHolder.getValue());
        valueHolder.refresh();
        assertSame(returnValue, valueHolder.getValue());

        EasyMock.verify(refreshFunction);
    }

    public void testWithLazyInitFalseAndAlwaysRefreshFalse() {
        Object returnValue = new Object();

        Closure refreshFunction = (Closure) EasyMock.createMock(Closure.class);
        EasyMock.expect(refreshFunction.call(null)).andReturn(returnValue);
        EasyMock.replay(refreshFunction);

        RefreshableValueHolder valueHolder = new RefreshableValueHolder(refreshFunction, false, false);
        assertSame(returnValue, valueHolder.getValue());

        EasyMock.verify(refreshFunction);
    }

    public void testWithLazyInitFalseAndAlwaysRefreshTrue() {
        Object returnValue1 = new Object();
        Object returnValue2 = new Object();
        Object returnValue3 = new Object();

        Closure refreshFunction = (Closure) EasyMock.createMock(Closure.class);
        EasyMock.expect(refreshFunction.call(null)).andReturn(returnValue1);
        EasyMock.expect(refreshFunction.call(null)).andReturn(returnValue2);
        EasyMock.expect(refreshFunction.call(null)).andReturn(returnValue3);
        EasyMock.replay(refreshFunction);

        RefreshableValueHolder valueHolder = new RefreshableValueHolder(refreshFunction, true, true);
        assertSame(returnValue1, valueHolder.getValue());
        assertSame(returnValue2, valueHolder.getValue());
        assertSame(returnValue3, valueHolder.getValue());

        EasyMock.verify(refreshFunction);
    }

    protected void setUp() throws Exception {
        ApplicationServices services = new ApplicationServices() {

            public Object getService(Class serviceType) {
                return new DefaultValueChangeDetector();
            }

            public boolean containsService(Class serviceType) {
                return ValueChangeDetector.class.equals(serviceType);
            }

        };
        ApplicationServicesLocator.load(new ApplicationServicesLocator(services));
    }
}
