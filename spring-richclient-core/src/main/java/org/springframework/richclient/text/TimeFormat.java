/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.text;

/**
 * A text formatter that formats time periods (durations.)
 * 
 * @author Keith Donald
 */
public abstract class TimeFormat {
    private static final TimeFormat daysInstance = new TimeFormat() {
        public String format(long time) {
            int totalSeconds = (int)time / 1000;
            int days = totalSeconds / 86400;
            int dSecs = days * 86400;

            int hours = (totalSeconds - dSecs) / 3600;
            int hSecs = hours * 3600;

            int minutes = (totalSeconds - dSecs - hSecs) / 60;
            int mSecs = minutes * 60;

            int seconds = totalSeconds - dSecs - hSecs - mSecs;

            StringBuffer buf = new StringBuffer();
            if (days > 0) {
                buf.append(days);
                buf.append("d ");
            }
            buf.append(hours);
            buf.append("h ");
            buf.append(minutes);
            buf.append("m ");
            buf.append(seconds);
            buf.append("s");
            return buf.toString();
        }
    };

    private static final TimeFormat millisecondsInstance = new TimeFormat() {
        public String format(long time) {
            int totalSeconds = (int)time / 1000;
            int days = totalSeconds / 86400;
            int dSecs = days * 86400;

            int hours = (totalSeconds - dSecs) / 3600;
            int hSecs = hours * 3600;

            int minutes = (totalSeconds - dSecs - hSecs) / 60;
            int mSecs = minutes * 60;

            int seconds = totalSeconds - dSecs - hSecs - mSecs;

            long milliseconds = time - dSecs * 1000 - hSecs * 1000 - mSecs * 1000 - seconds * 1000;

            StringBuffer buf = new StringBuffer();
            if (days > 0) {
                buf.append(days);
                buf.append("d ");
            }
            if (hours > 0) {
                buf.append(hours);
                buf.append("h ");
            }
            if (minutes > 0) {
                buf.append(minutes);
                buf.append("m ");
            }
            if (seconds > 0) {
                buf.append(seconds);
                buf.append("s ");
            }
            if (milliseconds > 0) {
                buf.append(milliseconds);
                buf.append("ms");
            }
            if (buf.length() == 0)
                return "0";

            return buf.toString();
        }
    };

    /**
     * Returns a standard TimeFormat that formats using days, hours, minutes,
     * and seconds.
     * <p>
     * The format looks like: 5d 23h 12m 52s.
     * 
     * @return The daysInstance TimeFormatter.
     */
    public static synchronized TimeFormat getDaysInstance() {
        return daysInstance;
    }

    /**
     * Returns a TimeFormat that formats using days, hours, minutes, seconds,
     * and milliseconds.
     * <p>
     * The format looks like: 5d 23h 12m 52s 10ms.
     * 
     * @return The millisecondsInstance TimeFormatter.
     */
    public static synchronized TimeFormat getMillisecondsInstance() {
        return millisecondsInstance;
    }

    /**
     * Returns a formatted time string for the specified time period.
     * 
     * @param time
     *            the time period.
     * @return The formatted time string.
     */
    public abstract String format(long time);

}