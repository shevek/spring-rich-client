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

import org.springframework.remoting.simple.protocol.Request;

/**
 * @author oliverh
 */
public interface InvocationListener {

    /**
     * Called just before an invocation commences.
     * 
     * @param request
     *            the request
     */
    public void invocationStarting(Request request);

    /**
     * Called when an invocation has completed.
     * 
     * @param request
     *            the request
     * @param result
     *            the result of the request. In the case of a sucsessfull
     *            invocation this will be an object of type Reply otherwise it
     *            will be the exception that caused the invocation to fail.
     */
    public void invocationComplete(Request request, Object result);
}