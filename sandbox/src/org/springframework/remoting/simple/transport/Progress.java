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

/**
 * @author oliverh
 */
public class Progress {

    protected boolean complete;

    protected long bytesSent;

    protected long bytesToSend;

    protected long bytesReceived;

    protected long bytesToReceive;

    protected long timeSpentSending;

    protected long timeSpentReceiving;

    protected long totalTimeSpent;

    public Progress(boolean complete, long bytesSent, long bytesToSend, long bytesReceived, long bytesToReceive,
            long timeSpentSending, long timeSpentReceiving, long totalTimeSpent) {

        this.complete = complete;
        this.bytesSent = bytesSent;
        this.bytesToSend = bytesToSend;
        this.bytesReceived = bytesReceived;
        this.bytesToReceive = bytesToReceive;
        this.timeSpentSending = timeSpentSending;
        this.timeSpentReceiving = timeSpentReceiving;
        this.totalTimeSpent = totalTimeSpent;
    }

    public boolean isComplete() {
        return complete;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getBytesToSend() {
        return bytesToSend;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public long getBytesToReceive() {
        return bytesToReceive;
    }

    public long getTimeSpentSending() {
        return timeSpentSending;
    }

    public long getTimeSpentReceiving() {
        return timeSpentReceiving;
    }

    public long getTotalTimeSpent() {
        return totalTimeSpent;
    }
}