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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.springframework.remoting.simple.SimpleRemotingException;
import org.springframework.remoting.simple.SimpleRemotingException.Recoverable;

/**
 * @author oliverh
 */
public class ProtocolVersion {

    private static final String PROTOCOL_MAGIC = "simple";

    private String protocol;

    private int majorVersion;

    private int minorVersion;

    public ProtocolVersion(String protocol, int majorVersion, int minorVersion) {
        this.protocol = protocol;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String toString() {
        return protocol + " v" + majorVersion + "." + minorVersion;
    }

    public static void write(ProtocolVersion version, DataOutputStream dos) throws IOException {
        dos.writeUTF(PROTOCOL_MAGIC);
        dos.writeUTF(version.getProtocol());
        dos.writeInt(version.getMajorVersion());
        dos.writeInt(version.getMinorVersion());
    }

    public static ProtocolVersion read(DataInputStream dis, Recoverable recoverable) {
        try {
            if (!dis.readUTF().equals(PROTOCOL_MAGIC)) {
                throw new SimpleRemotingException(recoverable, "InputStream returned incorrect magic number. "
                        + "Check Protocol is configured correctly.");
            }
        }
        catch (IOException e) {
            throw new SimpleRemotingException(recoverable, "Unable to read magic number. "
                    + "Check Protocol is configured correctly.", e);
        }

        try {
            String protocol = dis.readUTF();
            int majorVersion = dis.readInt();
            int minorVersion = dis.readInt();

            return new ProtocolVersion(protocol, majorVersion, minorVersion);
        }
        catch (IOException e) {
            throw new SimpleRemotingException(recoverable, "Unable to read protocol version.", e);
        }
    }
}