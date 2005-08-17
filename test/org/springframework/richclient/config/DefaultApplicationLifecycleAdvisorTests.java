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
package org.springframework.richclient.config;

import junit.framework.TestCase;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;

/**
 * Test cases for {@link DefaultApplicationLifecycleAdvisor}
 */
public class DefaultApplicationLifecycleAdvisorTests extends TestCase {

    public void testApplicationEventNotification() throws Exception {
        TestAdvisor advisor = new TestAdvisor();
        advisor.afterPropertiesSet();
        Application.load(null);
        Application application = new Application(advisor);
        Application.load(application);

        advisor.setWindowCommandBarDefinitions(new ClassPathResource(
                "org/springframework/richclient/config/app-lifecycle-test-ctx.xml"));

        // Fire up test.....
        advisor.testInit();
        advisor.onApplicationEvent(new ApplicationEvent(this) {
        });

        TestCommand cmd = (TestCommand)advisor.getBeanFactory().getBean("testCommand");
        assertEquals("Command must be notified", 1, cmd.getCount());
    }

    // ===============================================================================
    // Helper classes:

    public static class TestAdvisor extends DefaultApplicationLifecycleAdvisor {
        public TestAdvisor() {
            setStartingPageId("whatever");
        }
        

        public void testInit() {
            initNewWindowCommandBarFactory();
        }

        public BeanFactory getBeanFactory() {
            return getCommandBarFactory();
        }
    }

    public static class TestCommand implements ApplicationListener {

        int count = 0;

        public void onApplicationEvent(ApplicationEvent event) {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}
