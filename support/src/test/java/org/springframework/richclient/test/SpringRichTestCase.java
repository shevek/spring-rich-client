/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.richclient.test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.support.DefaultValueChangeDetector;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.StaticApplicationServices;

/**
 * Convenient base implementation for Spring Rich test cases; automatically configures the
 * application services singleton and provides hooks for common test case setup
 * requirements.
 * 
 * @author Oliver Hutchison
 */
public abstract class SpringRichTestCase extends TestCase {

    /**
     * Logger available to subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    protected StaticApplicationServices applicationServices;

    protected final void setUp() throws Exception {
        try {
            applicationServices = new StaticApplicationServices();
            new ApplicationServicesLocator(applicationServices);

            new Application(new DefaultApplicationLifecycleAdvisor());
            StaticApplicationContext applicationContext = new StaticApplicationContext();
            Application.instance().setApplicationContext(applicationContext);

            registerBasicServices(applicationServices);
            registerAdditionalServices(applicationServices);

            applicationContext.refresh();
            doSetUp();
        } catch( Exception e ) {
            Application.load(null);
            throw e;
        }
    }

    /**
     * Register the application services needed for our tests.
     */
    protected void registerBasicServices( StaticApplicationServices applicationServices ) {
        applicationServices.registerService(new StaticMessageSource(), MessageSource.class);
        applicationServices.registerService(new DefaultValueChangeDetector(), ValueChangeDetector.class);
    }

    /**
     * May be implemented in subclasses that need to register services with the global
     * application services instance.
     */
    protected void registerAdditionalServices( StaticApplicationServices applicationServices ) {
    }

    /**
     * Get the application services instance.
     */
    protected StaticApplicationServices getApplicationServices() {
        return applicationServices;
    }

    /**
     * Tear down method invoked by JUnit framework. Derived classes should put their work
     * in {@link #doTearDown()}.
     */
    protected final void tearDown() throws Exception {
        try {
            doTearDown();
        } finally {
            Application.load(null);
        }
    }

    /**
     * Should be implemented in subclasses as an alternative to the final method
     * {@link #setUp()}
     * 
     * @throws Exception
     */
    protected void doSetUp() throws Exception {
    }

    /**
     * Should be implemented in subclasses as an alternative to the final method
     * {@link #tearDown()}
     */
    protected void doTearDown() throws Exception {
    }
}
