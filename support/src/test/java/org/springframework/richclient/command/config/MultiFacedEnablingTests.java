/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.springframework.richclient.command.config;

import javax.swing.AbstractButton;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * MultiFacedEnablingTests was built to check up on the issue reported as <a
 * href="http://opensource.atlassian.com/projects/spring/browse/RCP-73">RCP-73 </a>.
 */
public class MultiFacedEnablingTests extends SpringRichTestCase
{
    private static final String ALTERNATE_ID = "otherId";
    private static final String MAIN_ID = "someid";

    /**
     * Big idea of the test:
     * <ol>
     * <li>create silly command</li>
     * <li>register additional command face to it, and create a button that uses that.</li>
     * <li>disable/enable the command --> check if all buttons follow up on the changes</li>
     * </ol>
     */
    public void testMultifacedCommandDisabling()
    {
        ActionCommand command = new ActionCommand(MAIN_ID)
        {

            protected void doExecuteCommand()
            {
                // does nothing during this test anyway
            }
        };

        AbstractButton standardButton = command.createButton();

        // test this dude's enabling
        command.setEnabled(false);
        assertFalse("standard face button didn't follow up on the command's disabling", standardButton
                .isEnabled());
        command.setEnabled(true);
        assertTrue("standard face button didn't follow up on the command's enabling", standardButton
                .isEnabled());

        // register an alternative face to this command
        CommandFaceDescriptor face = new CommandFaceDescriptor();
        command.setFaceDescriptor(ALTERNATE_ID, face);

        // and get us a button with that face
        AbstractButton otherFacedButton = command.createButton(ALTERNATE_ID);

        // test this newly faced dude
        command.setEnabled(false);
        assertFalse("alternative face button didn't follow up on the command's disabling", otherFacedButton
                .isEnabled());
        command.setEnabled(true);
        assertTrue("alternative face button didn't follow up on the command's enabling", otherFacedButton
                .isEnabled());

    }
}
