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

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import org.springframework.remoting.RemoteAccessException;

/**
 * @author  oliverh
 */
public class SimpleRemotingException extends RemoteAccessException {

    public static final Recoverable YES = new Recoverable("Yes");

    public static final Recoverable NO = new Recoverable("No");

    public static final Recoverable MAYBE = new Recoverable("Maybe");

    private Recoverable recoverable;

    public SimpleRemotingException(String message) {
        this(NO, message, null);
    }

    public SimpleRemotingException(String message, Throwable cause) {
        this(NO, message, cause);
    }

    public SimpleRemotingException(Recoverable recoverable, String message) {
        this(recoverable, message, null);
    }

    public SimpleRemotingException(Recoverable recoverable, String message, Throwable cause) {
        super(message, cause);
        this.recoverable = recoverable;
    }

    public Recoverable isRecoverable() {
        return recoverable;
    }

    public static class Recoverable implements Serializable {
        private String value;

        private Recoverable(String value) {
            this.value = value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            else if (o instanceof Recoverable) {
                return this.value.equals(((Recoverable)o).value);
            }
            return false;
        }

        public int hashCode() {
            return value.hashCode();
        }

        public String toString() {
            return value;
        }

        public Object readResolve() throws ObjectStreamException {
            if (YES.value.equals(value)) {
                return YES;
            }
            else if (NO.value.equals(value)) {
                return NO;
            }
            else if (MAYBE.value.equals(value)) {
                return MAYBE;
            }
            else {
                throw new InvalidClassException("Unable to resolve singlton.");
            }
        }
    }
}