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
package org.springframework.remoting.simple.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.simple.SimpleRemotingException;
import org.springframework.remoting.simple.protocol.Protocol;
import org.springframework.remoting.simple.protocol.Reply;
import org.springframework.remoting.simple.protocol.Request;
import org.springframework.remoting.simple.protocol.DefaultProtocol;

/**
 * @author oliverh
 */
public abstract class AbstractTransport
extends AbstractInvoker
implements Transport {

    protected final Log logger = LogFactory.getLog(getClass());

    private Protocol protocol;

    private RetryDecisionManager retryDecisionManager;

    private AuthenticationCallback authenticationCallback;

    private List progressListeners;

    public AbstractTransport() {
        this(new DefaultProtocol());
    }

    public AbstractTransport(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol must not be null");
        }
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setRetryDecisionManager(
            RetryDecisionManager retryDecisionManager) {
        this.retryDecisionManager = retryDecisionManager;
    }

    public RetryDecisionManager getRetryDecisionManager() {
        return retryDecisionManager;
    }

    public void setAuthenticationCallback(
            AuthenticationCallback authenticationCallback) {
        this.authenticationCallback = authenticationCallback;
    }

    public AuthenticationCallback getAuthenticationCallback() {
        return authenticationCallback;
    }

    public void addProgressListener(ProgressListener progressListener) {
        if (progressListeners == null) {
            progressListeners = new ArrayList();
        }
        progressListeners.add(progressListener);

    }

    public void removeProgressListener(ProgressListener progressListener) {
        if (progressListeners == null) {
            return;
        }
        progressListeners.remove(progressListener);
    }

    public Object invokeRemoteMethod(Class serviceInterface, Method method,
            Object[] args) throws Throwable {
        Request request = new Request(serviceInterface, method, args);

        Authentication authentication = authenticationCallback == null ? null
                : authenticationCallback.authenticate(request);
        firePreInvocation(request);
        Object result = null;
        ProgressTracker tracker = new ProgressTracker(request);
        try {
            int tries = 0;
            do {
                ++tries;
                try {
                    result = invokeInternal(request, authentication, tracker);
                } catch (SimpleRemotingException e) {
                    if (!shouldRetry(request, tries, e)) {
                        throw e;
                    }
                }
            } while (result == null);
            if (result instanceof SimpleRemotingException) {
                SimpleRemotingException serverEx = (SimpleRemotingException) result;
                result = new SimpleRemotingException(serverEx.isRecoverable(),
                        "Received exception from server", serverEx);
            }
        } catch (Throwable t) {
            result = t;
        }
        firePostInvocation(request, result);
        if (result instanceof Throwable) {
            throw (Throwable) result;
        }
        if (result instanceof Reply) {
            return ((Reply) result).getResult();
        } else {
            throw new SimpleRemotingException("Result is of unexpected type ["
                    + result == null ? "null" : result.getClass() + "]");
        }
    }

    protected abstract Reply invokeInternal(Request request,
            Authentication authentication, ProgressTracker tracker);

    /**
     * Subclasses my overide this method to provide additional retry options if
     * the retryDecisionManager do not suit their needs.
     */
    protected boolean shouldRetry(Request request, int tries,
            SimpleRemotingException e) {
        if (retryDecisionManager == null) {
            return false;
        }
        return retryDecisionManager.shouldRetry(request, tries, e);
    }

    public class ProgressTracker extends Progress {
        private Request request;

        private long startTime;

        private long finishTime;

        private long sendStart;

        private long sendEnd;

        private long receiveStart;

        private long receiveEnd;

        public ProgressTracker(Request request) {
            super(false, 0, 0, 0, 0, 0, 0, 0);
        }

        public void start() {
            startTime = System.currentTimeMillis();
        }

        public void startSending(long bytesToSend) {
            sendStart = System.currentTimeMillis();
            this.bytesToSend = bytesToSend;
        }

        public void finishSending() {
            sendEnd = System.currentTimeMillis();
            fireUpdateProgress();
        }

        public void startReceiving(long bytesToReceive) {
            receiveStart = System.currentTimeMillis();
            this.bytesToReceive = bytesToReceive;
        }

        public void finishReceiving() {
            receiveEnd = System.currentTimeMillis();
            fireUpdateProgress();
        }

        public void finish() {
            complete = true;
            finishTime = System.currentTimeMillis();
            fireUpdateProgress();
        }

        public long getTimeSpentSending() {
            if (sendStart == 0) {
                return 0;
            }
            long endTime = (sendEnd == 0) ? System.currentTimeMillis()
                    : sendEnd;
            return endTime - sendStart;
        }

        public long getTimeSpentReceiving() {
            if (receiveStart == 0) {
                return 0;
            }
            long endTime = (receiveEnd == 0) ? System.currentTimeMillis()
                    : receiveEnd;
            return endTime - receiveStart;
        }

        public long getTotalTimeSpent() {
            long endTime = (finishTime == 0) ? System.currentTimeMillis()
                    : finishTime;
            return endTime - startTime;
        }

        protected OutputStream getProgressOutputStream(final OutputStream os) {
            return new OutputStream() {

                public void close() throws IOException {
                    os.close();
                }

                public boolean equals(Object arg0) {
                    return os.equals(arg0);
                }

                public void flush() throws IOException {
                    os.flush();
                }

                public int hashCode() {
                    return os.hashCode();
                }

                public String toString() {
                    return os.toString();
                }

                public void write(byte[] b) throws IOException {
                    bytesSent += b.length;
                    os.write(b);
                }

                public void write(byte[] b, int off, int len)
                        throws IOException {
                    bytesSent += len;
                    os.write(b, off, len);
                }

                public void write(int b) throws IOException {
                    bytesSent++;
                    os.write(b);
                }
            };
        }

        protected InputStream getProgressInputStream(final InputStream is) {
            return new InputStream() {

                public int available() throws IOException {
                    return is.available();
                }

                public void close() throws IOException {
                    is.close();
                }

                public boolean equals(Object arg0) {
                    return is.equals(arg0);
                }

                public int hashCode() {
                    return is.hashCode();
                }

                public void mark(int arg0) {
                    is.mark(arg0);
                }

                public boolean markSupported() {
                    return is.markSupported();
                }

                public int read() throws IOException {
                    bytesReceived++;
                    return is.read();
                }

                public int read(byte[] b) throws IOException {
                    bytesReceived += b.length;
                    return is.read(b);
                }

                public int read(byte[] b, int off, int len) throws IOException {
                    bytesReceived += len;
                    return is.read(b, off, len);
                }

                public void reset() throws IOException {
                    is.reset();
                }

                public long skip(long len) throws IOException {
                    return is.skip(len);
                }

                public String toString() {
                    return is.toString();
                }
            };
        }

        protected void fireUpdateProgress() {
            if (progressListeners == null) {
                return;
            }
            for (Iterator i = progressListeners.iterator(); i.hasNext();) {
                ((ProgressListener) i.next()).updateProgress(request, this);
            }
        }
    }

}

