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
package org.springframework.richclient.samples.petclinic;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.BeanFactoryApplicationAdvisor;
import org.springframework.richclient.command.ActionCommand;

/**
 * Custom application lifecycle implementation that configures the petclinic app
 * at well defined points within its lifecycle.
 * 
 * @author Keith Donald
 */
public class PetClinicApplicationAdvisor extends BeanFactoryApplicationAdvisor {

    public void onPreWindowOpen(ApplicationWindowConfigurer configurer) {
        super.onPreWindowOpen(configurer);
        // comment out to hide the menubar, toolbar, or reduce window size...
        //configurer.setShowMenuBar(false);
        //configurer.setShowToolBar(false);
        //configurer.setInitialSize(new Dimension(640, 480));
    }

    public void onCommandsCreated(ApplicationWindow window) {
        ActionCommand command = window.getCommandManager().getActionCommand("loginCommand");
        command.execute();
    }

}