/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.support;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.support.HelpContents;

/**
 * @author Keith Donald
 */
public class HelpContentsCommand extends ApplicationWindowAwareCommand {
    private static final String ID = "helpContentsCommand";

    private HelpContents helpContents = new HelpContents();

    public HelpContentsCommand() {
        super(ID);
    }

    public void setHelpSetPath(Resource path) {
        this.helpContents.setHelpSetPath(path);
    }

    protected void doExecuteCommand() {
        helpContents.display(getApplicationWindow().getControl());
    }

}