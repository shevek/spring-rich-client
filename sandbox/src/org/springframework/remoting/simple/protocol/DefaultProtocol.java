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

package org.springframework.remoting.simple.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.springframework.remoting.simple.SimpleRemotingException;
import org.springframework.remoting.simple.SimpleRemotingException.Recoverable;

/**
 * @author oliverh
 */
public class DefaultProtocol implements Protocol {

    private static final ProtocolVersion version = new ProtocolVersion(
            "serialization", (byte) 1, (byte) 0);

    public void writeRequest(OutputStream os, Request request)
            throws IOException {
        ProtocolVersion.write(getProtocolVersion(), os);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(request);
    }

    public Request readRequest(InputStream is, Recoverable recoverable) {
        return (Request) read(is, Request.class, recoverable);
    }

    public void writeException(OutputStream os,
            SimpleRemotingException exception) throws IOException {
        write(os, exception);
    }

    public void writeReply(OutputStream os, Reply reply) throws IOException {
        write(os, reply);
    }

    public void writeReply(OutputStream os, Object reply) throws IOException {
        ProtocolVersion.write(getProtocolVersion(), os);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(reply);
    }

    public Reply readReply(InputStream is, Recoverable recoverable) {
        Object reply = read(is, recoverable);
        if (reply instanceof Reply) {
            return (Reply) reply;
        } else if (reply instanceof SimpleRemotingException) {
            throw (SimpleRemotingException) reply;
        } else {
            throw new SimpleRemotingException(
                    "Received unexpected reply type [" + reply == null ? "null"
                            : reply.getClass() + "]");
        }
    }

    private void write(OutputStream os, Object value) throws IOException {
        ProtocolVersion.write(getProtocolVersion(), os);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(value);
    }

    private Object read(InputStream is, Recoverable recoverable) {
        try {
            checkProtocolVersion(ProtocolVersion.read(is, recoverable),
                    recoverable);
            ObjectInputStream ois = new ObjectInputStream(is);
            Object in = ois.readObject();

            return in;
        } catch (ClassNotFoundException e) {
            throw new SimpleRemotingException(recoverable,
                    "Unable deserialize input stream", e);
        } catch (IOException e) {
            throw new SimpleRemotingException(recoverable,
                    "Unable deserialize input stream", e);
        }
    }

    private Object read(InputStream is, Class expectedClass,
            Recoverable recoverable) {
        Object in = read(is, recoverable);
        if (in == null) {
            throw new SimpleRemotingException(recoverable,
                    "Received null expecting " + expectedClass, null);
        } else if (expectedClass.isInstance(in.getClass())) {
            throw new SimpleRemotingException(recoverable, "Received "
                    + in.getClass().getName() + " expecting " + expectedClass,
                    null);
        }
        return in;
    }

    protected ProtocolVersion getProtocolVersion() {
        return version;
    }

    private void checkProtocolVersion(ProtocolVersion wireVersion,
            Recoverable recoverable) {
        if (!wireVersion.getProtocol().equals(
                getProtocolVersion().getProtocol())) {
            throw new SimpleRemotingException(recoverable, "Protocol version ["
                    + wireVersion.toString() + "] is not supported");
        }
    }

}