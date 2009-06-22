/*
 * Copyright 2005 the original author or authors.
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

import org.springframework.richclient.application.ApplicationLauncher;

/**
 * Entry point to the JIDE Spring RCP integration demo
 *
 * @author Jonny Wray
 */
public class JideApplication {

  private static final String CONTEXT_ROOT = "ctx";
  private static final String APPLICATION_CONTEXT = CONTEXT_ROOT + "/jideApplicationContext.xml";
  private static final String PAGE_CONTEXT = CONTEXT_ROOT + "/pagesApplicationContext.xml";
  private static final String STARTUP_CONTEXT = CONTEXT_ROOT + "/richclient-startup-context.xml";

  public static void main( String[] args ) {
    try {
      new ApplicationLauncher( STARTUP_CONTEXT, new String[]{APPLICATION_CONTEXT, PAGE_CONTEXT} );
    } catch ( Exception e ) {
      e.printStackTrace();
      System.exit( 1 );
    }
  }
}
