/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.remoting.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.simple.protocol.Request;
import org.springframework.remoting.simple.transport.Authentication;
import org.springframework.remoting.simple.transport.HttpTransport;
import org.springframework.remoting.simple.transport.RetryDecisionManager;

/**
 * @author Oliver Hutchison
 */
public class SimpleRemotingTestSuite extends TestCase {

    public void testSimpleProxyFactoryBeanWithAccessError() throws Exception {
        SimpleProxyFactoryBean factory = new SimpleProxyFactoryBean();
        try {
            factory.setServiceInterface(Person.class);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }
        factory.setServiceInterface(IPerson.class);
        factory.setServiceUrl("http://localhosta/RemoteBean2");
        factory.setUsername("test");
        factory.setPassword("bean");
        factory.afterPropertiesSet();
        assertTrue("Correct singleton value", factory.isSingleton());
        assertTrue(factory.getObject() instanceof IPerson);
        IPerson bean = (IPerson)factory.getObject();
        try {
            bean.setName("test");
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException ex) {
            // expected
        }

        assertTrue(bean.equals(bean));
        assertTrue(!bean.equals(new Person()));
    }

    public void testServiceExporterWithAccessError() throws Exception {
        SimpleServiceExporter serviceExporter = new SimpleServiceExporter();

        try {
            serviceExporter.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        serviceExporter.setService(new Person());
        try {
            serviceExporter.setServiceInterface(Person.class);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }
        serviceExporter.setServiceInterface(IPerson.class);
        serviceExporter.afterPropertiesSet();

        serviceExporter.setService(new Person());
        try {
            serviceExporter.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        serviceExporter.setService(null);
        serviceExporter.setServiceInterface(IPerson.class);
        try {
            serviceExporter.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        serviceExporter = new SimpleServiceExporter();
        Map services = new HashMap();
        serviceExporter.setServices(services);
        try {
            serviceExporter.afterPropertiesSet();
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        services.put(new Person(), "org.springframework.remoting.simple.IPerson");
        services.put(new RemoteTestBean(), "org.springframework.remoting.simple.IRemoteTestBean");

        serviceExporter.setServices(services);
        serviceExporter.afterPropertiesSet();

        services.clear();
        services.put(new RemoteTestBean(), "org.springframework.remoting.simple.IPerson");
        try {
            serviceExporter.setServices(services);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        services.clear();
        services.put(null, "org.springframework.remoting.simple.IPerson");
        try {
            serviceExporter.setServices(services);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        services.clear();
        services.put(new Person(), "org.springframework.remoting.simple.IRemoteTestBean");
        try {
            serviceExporter.setServices(services);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }

        services.clear();
        services.put(new Person(), "");
        try {
            serviceExporter.setServices(services);
            fail("Should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testServiceExporterWithSimpleProxyFactoryBean() throws Exception {
        RemoteTestBean serviceBean1 = new RemoteTestBean();
        Person serviceBean2 = new Person();
        TestSimpleServiceExporter service = createService(serviceBean1, IRemoteTestBean.class, serviceBean2,
                IPerson.class);

        MockHttpUrlConnection conn = new MockHttpUrlConnection(service);
        SimpleProxyFactoryBean factory1 = createClientFactory(conn, IRemoteTestBean.class);
        SimpleProxyFactoryBean factory2 = createClientFactory(conn, IPerson.class);

        IRemoteTestBean remoteBean1 = (IRemoteTestBean)factory1.getObject();
        IPerson remoteBean2 = (IPerson)factory2.getObject();

        remoteBean1.hashCode();
        assertEquals(remoteBean1, remoteBean1);
        assertFalse(remoteBean1.equals(remoteBean2));

        remoteBean1.setInt(5);
        assertEquals(serviceBean1.getInt(), 5);
        assertEquals(remoteBean1.getInt(), 5);

        remoteBean1.setString("s1");
        assertEquals(serviceBean1.getString(), "s1");
        assertEquals(remoteBean1.getString(), "s1");

        remoteBean1.setString(null);
        assertEquals(serviceBean1.getString(), null);
        assertEquals(remoteBean1.getString(), null);

        remoteBean2.setName("name1");
        assertEquals(serviceBean2.getName(), "name1");
        assertEquals(remoteBean2.getName(), "name1");

        RemoteTestBean child = new RemoteTestBean();
        child.setString("s2");
        remoteBean1.setChild(child);
        child = (RemoteTestBean)serviceBean1.getChild();
        assertEquals(child.getString(), "s2");
        child = (RemoteTestBean)remoteBean1.getChild();
        assertEquals(child.getString(), "s2");

        List list = new ArrayList();
        list.add("0");
        list.add("1");
        remoteBean1.setCollection(list);
        list = (List)serviceBean1.getCollection();
        assertEquals(list.size(), 2);
        assertEquals(list.get(1), "1");

        list = (List)remoteBean1.getCollection();
        assertEquals(list.size(), 2);
        assertEquals(list.get(1), "1");

        try {
            remoteBean1.bounce(new NullPointerException("m1"));
            fail("Expecteding NullPointerException");
        }
        catch (NullPointerException e) {
            assertEquals(e.getMessage(), "m1");
        }
        NullPointerException lastBounce = (NullPointerException)serviceBean1.getLastBounce();
        assertEquals(lastBounce.getMessage(), "m1");

        try {
            remoteBean1.setChild(new Object());
            fail("should have thrown SimpleRemotingException");
        }
        catch (SimpleRemotingException e) {
            // expected
        }
    }

    public void testBadConnection() throws Exception {
        TestSimpleServiceExporter service = createService(new RemoteTestBean(), IRemoteTestBean.class);

        MockHttpUrlConnection conn = new MockHttpUrlConnection(service);
        SimpleProxyFactoryBean factory = createClientFactory(conn, IRemoteTestBean.class);

        IRemoteTestBean remoteBean = (IRemoteTestBean)factory.getObject();

        conn.reset(MockHttpUrlConnection.HTTP_INTERNAL_ERROR, false, false);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, false, true);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, true, false);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, true, true);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
        }
    }

    public void testRetry() throws Exception {
        TestSimpleServiceExporter service = createService(new RemoteTestBean(), IRemoteTestBean.class);
        MockHttpUrlConnection conn = new MockHttpUrlConnection(service);
        TestRetryDecisionManager retry = new TestRetryDecisionManager();
        SimpleProxyFactoryBean factory = createClientFactory(conn, IRemoteTestBean.class, retry);

        IRemoteTestBean remoteBean = (IRemoteTestBean)factory.getObject();

        conn.reset(MockHttpUrlConnection.HTTP_INTERNAL_ERROR, false, false);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
            assertEquals(retry.lastException(), e);
            assertTrue(retry.wasInvoked());
            assertTrue(retry.lastException().isRecoverable() == SimpleRemotingException.MAYBE);
            retry.reset();
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, false, true);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
            assertEquals(retry.lastException(), e);
            assertTrue(retry.wasInvoked());
            assertTrue(retry.lastException().isRecoverable() == SimpleRemotingException.MAYBE);
            retry.reset();
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, true, false);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
            assertTrue(retry.wasInvoked());
            assertEquals(retry.lastException(), e);
            assertTrue(retry.lastException().isRecoverable() == SimpleRemotingException.YES);
            retry.reset();
        }

        conn.reset(MockHttpUrlConnection.HTTP_OK, true, true);
        try {
            remoteBean.setInt(55);
            fail("Should have thrown RemoteAccessException");
        }
        catch (RemoteAccessException e) {
            // expected
        }
    }

    public void testIncompatibleClientService() throws Exception {
        TestSimpleServiceExporter service = createService(new RemoteTestBean(), IRemoteTestBean.class);

        MockHttpUrlConnection conn = new MockHttpUrlConnection(service);
        SimpleProxyFactoryBean factory = createClientFactory(conn, IPerson.class);

        IPerson remoteBean = (IPerson)factory.getObject();

        try {
            remoteBean.setAge(1);
            fail("Should have thrown RemoteAccessException");
        }
        catch (SimpleRemotingException e) {
            // expected
            assertEquals(e.isRecoverable(), SimpleRemotingException.NO);
        }
    }

    private SimpleProxyFactoryBean createClientFactory(MockHttpUrlConnection conn, Class serviceInterface)
            throws Exception {
        return createClientFactory(conn, serviceInterface, null);
    }

    private SimpleProxyFactoryBean createClientFactory(MockHttpUrlConnection conn, Class serviceInterface,
            TestRetryDecisionManager retry) throws Exception {
        SimpleProxyFactoryBean factory = new SimpleProxyFactoryBean();
        factory.setServiceInterface(serviceInterface);
        factory.setServiceUrl("http://whatever");
        TestHttpTransport transport = new TestHttpTransport(conn);
        transport.setRetryDecisionManager(retry);
        factory.setTransport(transport);
        factory.afterPropertiesSet();
        return factory;
    }

    private TestSimpleServiceExporter createService(Object serviceBean, Class serviceInterface) throws Exception {
        TestSimpleServiceExporter service = new TestSimpleServiceExporter();
        service.setService(serviceBean);
        service.setServiceInterface(serviceInterface);
        service.afterPropertiesSet();
        return service;
    }

    private TestSimpleServiceExporter createService(Object serviceBean1, Class class1, Object serviceBean2, Class class2)
            throws Exception {
        TestSimpleServiceExporter service = new TestSimpleServiceExporter();
        Map services = new HashMap();
        services.put(serviceBean1, class1.getName());
        services.put(serviceBean2, class2.getName());
        service.setServices(services);
        service.afterPropertiesSet();
        return service;
    }

    public class TestRetryDecisionManager implements RetryDecisionManager {

        private int count;

        private SimpleRemotingException lastException;

        public void reset() {
            count = 0;
            lastException = null;
        }

        public boolean wasInvoked() {
            return count > 0;
        }

        public SimpleRemotingException lastException() {
            return lastException;
        }

        public boolean shouldRetry(Request request, int retryNumber, SimpleRemotingException ex) {
            count++;
            lastException = ex;
            return false;
        }

    }

    public class TestHttpTransport extends HttpTransport {
        private MockHttpUrlConnection conn;

        public TestHttpTransport(MockHttpUrlConnection conn) throws Exception {
            super(new URL("http://whatever"));
            this.conn = conn;
        }

        protected HttpURLConnection openConnection(Request request, Authentication authentication) throws IOException {
            if (!conn.isReset()) {
                conn.reset();
            }
            return conn;
        }

    }

    public class TestSimpleServiceExporter extends SimpleServiceExporter {
        public byte[] invoke(byte[] requestContent) throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest(null);
            request.setContent(requestContent);
            MockHttpServletResponse response = new MockHttpServletResponse();
            handleRequest(request, response);
            return response.getContentAsByteArray();
        }
    }

    public class MockHttpUrlConnection extends HttpURLConnection {
        private int responseCode;

        private ByteArrayOutputStream os;

        private TestSimpleServiceExporter service;

        private boolean needsConnect;

        private boolean needsReset;

        private boolean corruptRequest;

        private boolean corruptResponse;

        public MockHttpUrlConnection(TestSimpleServiceExporter service) throws MalformedURLException {
            super(new URL("http://whatever"));
            this.service = service;
            this.needsReset = true;
        }

        public boolean isReset() {
            return needsReset == false;
        }

        public void reset() {
            reset(HttpURLConnection.HTTP_OK, false, false);
        }

        public void reset(int responseCode, boolean corruptRequest, boolean corruptResponse) {
            this.needsReset = false;
            this.needsConnect = true;
            this.corruptRequest = corruptRequest;
            this.corruptResponse = corruptResponse;
            this.responseCode = responseCode;
            this.os = new ByteArrayOutputStream();
        }

        public OutputStream getOutputStream() throws IOException {
            if (!isReset()) {
                throw new UnsupportedOperationException("mock needs reset");
            }
            needsReset = true;
            return os;
        }

        public InputStream getInputStream() throws IOException {
            try {
                byte[] requestContent = os.toByteArray();
                //                System.out.println("Request size = " +
                // requestContent.length);
                if (corruptRequest) {
                    corrupt(requestContent);
                }
                byte[] responseContent = service.invoke(requestContent);
                //                System.out.println("Response size = " +
                // responseContent.length);
                if (corruptResponse) {
                    corrupt(responseContent);
                }
                os = null;
                return new ByteArrayInputStream(responseContent);
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void corrupt(byte[] requestContent) {
            for (int i = 0; i < requestContent.length / 4; i++) {
                requestContent[i] = (byte)(requestContent[i] + 1);
            }
        }

        public int getResponseCode() throws IOException {
            return responseCode;
        }

        public String getResponseMessage() throws IOException {
            return "a message";
        }

        public void connect() {
        }

        public void disconnect() {
        }

        public boolean usingProxy() {
            return false;
        }
    }
}

