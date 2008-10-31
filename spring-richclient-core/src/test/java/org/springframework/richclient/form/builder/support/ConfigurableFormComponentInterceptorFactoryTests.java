/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.form.builder.support;

import java.util.Arrays;

import junit.framework.TestCase;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.binding.value.swing.TestableFormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptor;

/**
 * Tests for <code>ConfigurableFormComponentInterceptorFactory</code>.
 * 
 * @author Peter De Bruycker
 */
public class ConfigurableFormComponentInterceptorFactoryTests extends TestCase {
    public void testSettingBothIncludedAndExcludedFormModelIdsMustFail() throws Exception {
        TestableConfigurableFormComponentInterceptorFactory factory = new TestableConfigurableFormComponentInterceptorFactory();

        factory.setIncludedFormModelIds( new String[] { "included-0", "included-1" } );
        factory.setExcludedFormModelIds( new String[] { "excluded-0", "excluded-1" } );

        try {
            factory.afterPropertiesSet();
            fail( "Should throw IllegalStateException" );
        } catch( IllegalStateException e ) {
            // test passes;
        }
    }

    public void testSetIncludedFormModelIds() throws Exception {
        TestableConfigurableFormComponentInterceptorFactory factory = new TestableConfigurableFormComponentInterceptorFactory();
        factory.setCreateThis( new TestableFormComponentInterceptor() );

        factory.setIncludedFormModelIds( new String[] { "included-0", "included-1" } );
        factory.afterPropertiesSet();

        assertTrue( Arrays.equals( new String[] { "included-0", "included-1" }, factory.getIncludedFormModelIds() ) );

        DefaultFormModel included = new DefaultFormModel();
        included.setId( "included-0" );
        DefaultFormModel excluded = new DefaultFormModel();
        excluded.setId( "excluded-0" );

        assertNotNull( "FormModel should be included", factory.getInterceptor( included ) );
        assertNull( "FormModel is not included", factory.getInterceptor( excluded ) );
    }

    public void testSetExcludedFormModelIds() throws Exception {
        TestableConfigurableFormComponentInterceptorFactory factory = new TestableConfigurableFormComponentInterceptorFactory();
        factory.setCreateThis( new TestableFormComponentInterceptor() );

        factory.setExcludedFormModelIds( new String[] { "excluded-0", "excluded-1" } );
        factory.afterPropertiesSet();

        assertTrue( Arrays.equals( new String[] { "excluded-0", "excluded-1" }, factory.getExcludedFormModelIds() ) );

        DefaultFormModel included = new DefaultFormModel();
        included.setId( "included-0" );
        DefaultFormModel excluded = new DefaultFormModel();
        excluded.setId( "excluded-0" );

        assertNotNull( "FormModel should be included", factory.getInterceptor( included ) );
        assertNull( "FormModel is not included", factory.getInterceptor( excluded ) );
    }
    
    private class TestableConfigurableFormComponentInterceptorFactory extends ConfigurableFormComponentInterceptorFactory {
        private FormComponentInterceptor createThis;
        private FormModel lastFormModel;

        protected FormComponentInterceptor createInterceptor( FormModel formModel ) {
            lastFormModel = formModel;
            return createThis;
        }

        public void reset() {
            createThis = null;
            lastFormModel = null;
        }

        public void setCreateThis( FormComponentInterceptor createThis ) {
            this.createThis = createThis;
        }

        public void setLastFormModel( FormModel lastFormModel ) {
            this.lastFormModel = lastFormModel;
        }

        public FormModel getLastFormModel() {
            return lastFormModel;
        }
    }
}
