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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.remoting.simple.SimpleRemotingException;
import org.springframework.remoting.simple.protocol.Reply;
import org.springframework.remoting.simple.protocol.Request;

/**
 * @author oliverh
 */
public class HttpTransport extends AbstractTransport {

    private URL serviceUrl;

    private boolean useAuthenticator;

    public HttpTransport(URL serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setUseAuthenticator(boolean useAuthenticator) {
        this.useAuthenticator = useAuthenticator;
    }

    protected HttpURLConnection openConnection(Request request,
            Authentication authentication) throws IOException {
        URLConnection conn = serviceUrl.openConnection();
        if (!(conn instanceof HttpURLConnection)) {
            throw new UnsupportedOperationException("URL [" + serviceUrl
                    + "] is not a vaild HTTP URL.");
        }
        conn.setDoOutput(true);
        authenticate(conn, authentication);
        return (HttpURLConnection) conn;
    }

    /*
     * Generate a BASIC authentication header. Borrowed from code in Hessian,
     * thanks to Scott Ferguson.
     */
    protected void authenticate(URLConnection conn,
            Authentication authentication) {
        if (authentication != null) {
            String userName = String.valueOf(authentication.getPrincipal());
            String password = String.valueOf(authentication.getCredentials());
            String basicAuth = "Basic " + base64(userName + ":" + password);
            conn.setRequestProperty("Authorization", basicAuth);
        }
    }

    protected Reply invokeInternal(Request request,
            Authentication authentication, ProgressTracker tracker) {
        try {
            tracker.start();
            HttpURLConnection conn = doRequest(request, authentication, tracker);
            return doResponse(conn, tracker);
        } finally {
            tracker.finish();
        }
    }

    private HttpURLConnection doRequest(Request request,
            Authentication authentication, ProgressTracker tracker) {
        OutputStream os = null;
        try {
            tracker.startSending(-1);
            HttpURLConnection conn = openConnection(request, authentication);
            conn.setRequestProperty("Content-Type",
                    "application/x-java-serialized-object");
            os = tracker.getProgressOutputStream(conn.getOutputStream());
            getProtocol().writeRequest(os, request);
            os.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new SimpleRemotingException(SimpleRemotingException.MAYBE,
                        "Error contecting to server. HTTP response code ["
                                + responseCode + "], response message ["
                                + conn.getResponseMessage() + "].");
            }

            return conn;
        } catch (IOException e) {
            throw new SimpleRemotingException(SimpleRemotingException.MAYBE,
                    "Error attempting to connect to server", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.warn(
                            "Ignoring exception when closing output stream", e);
                }
            }
            tracker.finishSending();
        }
    }

    private Reply doResponse(URLConnection conn, ProgressTracker tracker) {
        InputStream is = null;
        try {
            tracker.startReceiving(-1);
            is = tracker.getProgressInputStream(conn.getInputStream());
            return getProtocol().readReply(is, SimpleRemotingException.MAYBE);
        } catch (IOException e) {
            throw new SimpleRemotingException(SimpleRemotingException.MAYBE,
                    "Error reading response from server", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.warn("Ignoring exception when closing input stream",
                            e);
                }
            }
            tracker.finishReceiving();
        }
    }

    /*
     * Encode a value as base64. Borrowed from code in Hessian, thanks to Scott
     * Ferguson.
     */
    private String base64(String value) {
        StringBuffer cb = new StringBuffer();

        int i = 0;
        for (i = 0; i + 2 < value.length(); i += 3) {
            long chunk = (int) value.charAt(i);
            chunk = (chunk << 8) + (int) value.charAt(i + 1);
            chunk = (chunk << 8) + (int) value.charAt(i + 2);

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append(encode(chunk >> 6));
            cb.append(encode(chunk));
        }

        if (i + 1 < value.length()) {
            long chunk = (int) value.charAt(i);
            chunk = (chunk << 8) + (int) value.charAt(i + 1);
            chunk <<= 8;

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append(encode(chunk >> 6));
            cb.append('=');
        } else if (i < value.length()) {
            long chunk = (int) value.charAt(i);
            chunk <<= 16;

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append('=');
            cb.append('=');
        }

        return cb.toString();
    }

    public char encode(long d) {
        d &= 0x3f;
        if (d < 26) {
            return (char) (d + 'A');
        } else if (d < 52) {
            return (char) (d + 'a' - 26);
        } else if (d < 62) {
            return (char) (d + '0' - 52);
        } else if (d == 62) {
            return '+';
        } else {
            return '/';
        }
    }
}