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
package org.springframework.richclient.samples.showcase.view;

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.InputApplicationDialog;

/**
 * Asks the user for input, then shows the {@link ViewWithInput}, passing the input from the user.
 * 
 * @author Peter De Bruycker
 */
public class ShowViewWithInputCommand extends ApplicationWindowAwareCommand {

    public ShowViewWithInputCommand() {
        super("showViewWithInputCommand");
    }

    @Override
    protected void doExecuteCommand() {
        new InputApplicationDialog("Input", getApplicationWindow().getControl()) {
            @Override
            protected void onFinish(Object inputValue) {
                getApplicationWindow().getPage().showView("viewWithInput", inputValue);
            }
        }.showDialog();
    }

}
