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
package org.springframework.richclient.list;

import java.util.List;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.command.ActionCommand;

public class ListUtils {

    private ListUtils() {

    }

    public static ActionCommand createRemoveRowCommand(final List list, final ValueModel selectionIndexHolder) {
        ActionCommand removeCommand = new ActionCommand("removeCommand") {
            protected void doExecuteCommand() {
                int selectedRowIndex = ((Integer)selectionIndexHolder.getValue()).intValue();
                list.remove(selectedRowIndex);
            }
        };
        new SingleListSelectionGuard(selectionIndexHolder, removeCommand);
        return removeCommand;
    }

}