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
package org.springframework.richclient.application;

import java.awt.EventQueue;

/**
 * Closes the splash screen in the event dispatching (GUI) thread.
 * 
 * @author Keith Donald
 * @see SplashScreen
 */
public class SplashScreenCloser {

    /**
     * Closes the currently-displayed, non-null splash screen.
     * 
     * @param splashScreen
     */
    public SplashScreenCloser(final SplashScreen splashScreen) {

        /*
         * Removes the splash screen.
         * 
         * Invoke this <code> Runnable </code> using <code>
         * EventQueue.invokeLater </code> , in order to remove the splash screen
         * in a thread-safe manner.
         */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                splashScreen.dispose();
            }
        });
    }
}