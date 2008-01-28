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
package org.springframework.richclient.filechooser;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * Utility functions for file choosers. Designed to be used by a single event
 * dispatching thread.
 * 
 * @author Keith Donald
 */
public class FileChooserUtils {
    private static JFileChooser fileChooser;

    private FileChooserUtils() {
    }

    public static File showFileChooser(Component parent, String defaultExtension, String approveButtonName,
            String fileTypeDescription) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        fileChooser.resetChoosableFileFilters();
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension(defaultExtension);
        filter.setDescription(fileTypeDescription);
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showDialog(parent, approveButtonName);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();

        return null;
    }
}