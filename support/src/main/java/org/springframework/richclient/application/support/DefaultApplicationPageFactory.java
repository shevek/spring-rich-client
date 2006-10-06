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
package org.springframework.richclient.application.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

/**
 * <code>ApplicationPageFactory</code> implementation for
 * <code>DefaultApplicationPage</code>.
 * 
 * @author Peter De Bruycker
 * 
 */
public class DefaultApplicationPageFactory implements ApplicationPageFactory{
    private static final Log logger = LogFactory.getLog( DefaultApplicationPageFactory.class );

    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
        logger.info( "Creating new DefaultApplicationPage" );

        DefaultApplicationPage page = new DefaultApplicationPage();
        page.setApplicationWindow( window );
        page.setDescriptor( descriptor );
        
        return page;
    }
}
