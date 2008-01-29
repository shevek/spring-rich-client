/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.application.docking.vldocking;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageListener;

import com.vlsolutions.swing.docking.DockingContext;

/**
 * @author Rogan Dawes
 */
public class VLDockingApplicationPageFactory implements ApplicationPageFactory {

    private static final Log logger = LogFactory.getLog(VLDockingApplicationPageFactory.class);
    
	/* (non-Javadoc)
	 * @see org.springframework.richclient.application.ApplicationPageFactory#createApplicationPage(org.springframework.richclient.application.ApplicationWindow, org.springframework.richclient.application.PageDescriptor)
	 */
	public ApplicationPage createApplicationPage(ApplicationWindow window, PageDescriptor descriptor) {
		VLDockingApplicationPage page = new VLDockingApplicationPage(window, descriptor);
		
        window.addPageListener(new PageListener() {
            public void pageOpened(ApplicationPage page) {
                // nothing to do here
            }
            public void pageClosed(ApplicationPage page) {
                VLDockingApplicationPage vlDockingApplicationPage = (VLDockingApplicationPage) page;
                saveDockingContext(vlDockingApplicationPage);
            }
        });
		
        return page;
	}
	
	/**
	 * Saves the docking layout of a docking page fo the application
	 * 
	 * @param appWindow The application window
	 *      (needed to hook in for the docking context)
	 */
	private void saveDockingContext(VLDockingApplicationPage dockingPage) {
	    DockingContext dockingContext = dockingPage.getDockingContext();
	 
	    // Page descriptor needed for config path
	    VLDockingPageDescriptor vlDockingPageDescriptor = (VLDockingPageDescriptor) 
	        Application.instance().getApplicationContext().getBean(dockingPage.getId());
	 
	    // Write docking context to file
	    BufferedOutputStream buffOs = null;
	    try {
	        File desktopLayoutFile = vlDockingPageDescriptor.getInitialLayout().getFile();
	        checkForConfigPath(desktopLayoutFile);
	 
	        buffOs = new BufferedOutputStream(new FileOutputStream(desktopLayoutFile));
	        dockingContext.writeXML(buffOs);
	        buffOs.close();
	        logger.debug("Wrote docking context to config file " + desktopLayoutFile);
	 
	    } catch (IOException e) {
	        logger.warn("Error writing VLDocking config", e);
	    } finally {
	        try { buffOs.close(); } catch (Exception e) { }
	    }
	}
	 
	/**
	 * Creates the config directory, if it doesn't exist already
	 * 
	 * @param configFile The file for which to create the path
	 */
	private void checkForConfigPath(File configFile) {
	    String desktopLayoutFilePath = configFile.getAbsolutePath();
	    String configDirPath = desktopLayoutFilePath.substring(
	            0, desktopLayoutFilePath.lastIndexOf(System.getProperty("file.separator")));
	    File configDir = new File(configDirPath);
	 
	    // create config dir if it does not exist
	    if (!configDir.exists()) {
	        configDir.mkdirs();
	        logger.debug("Newly created config directory");
	    }
	}

}
