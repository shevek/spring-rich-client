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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

/**
 * Basic implementation of a FileFilter that provides a list of valid file
 * extensions to JFileChooser.
 * <p>
 * Configurable descriptions for each file extension may also be configured.
 * 
 * @see javax.swing.filechooser.FileFilter
 */
public class DefaultFileFilter extends FileFilter {
    private Map filters = new HashMap();

    private String description;

    private boolean useExtensionsInDescription = true;

    /**
     * Creates a file filter. If no filters are added, then all files are
     * accepted.
     * 
     * @see #addExtension
     */
    public DefaultFileFilter() {
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new DefaultFileFilter("jpg");
     * 
     * @see #addExtension
     */
    public DefaultFileFilter(String extension) {
        this(extension, null);
    }

    /**
     * Creates a file filter that accepts the given file type. Example: new
     * DefaultFileFilter("jpg", "JPEG Image Images");
     * 
     * Note that the "." before the extension is not needed. If provided, it
     * will be ignored.
     * 
     * @see #addExtension
     */
    public DefaultFileFilter(String extension, String description) {
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter from the given string array. Example: new
     * DefaultFileFilter(String {"gif", "jpg"});
     * 
     * Note that the "." before the extension is not needed and will be ignored.
     * 
     * @see #addExtension
     */
    public DefaultFileFilter(String[] extensions) {
        this(extensions, null);
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new DefaultFileFilter(String {"gif", "jpg"}, "Gif and JPG
     * Images");
     * 
     * Note that the "." before the extension is not needed and will be ignored.
     * 
     * @see #addExtension
     */
    public DefaultFileFilter(String[] extensions, String description) {
        for (int i = 0; i < extensions.length; i++) {
            addExtension(extensions[i]);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Return true if this file should be shown in the directory pane, false if
     * it shouldn't.
     * 
     * Files that begin with "." are ignored.
     * 
     * @see FileFilter#accept
     */
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(extension) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the extension portion of the file's name .
     * 
     * @see FileFilter#accept
     */
    private String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * 
     * For example: the following code will create a filter that filters out all
     * files except those that end in ".jpg" and ".tif":
     * 
     * DefaultFileFilter filter = new DefaultFileFilter();
     * filter.addExtension("jpg"); filter.addExtension("tif");
     * 
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension) {
        filters.put(extension.toLowerCase(), extension);
    }

    /**
     * Returns the human readable description of this filter. For example: "JPEG
     * and GIF Image Files (*.jpg, *.gif)"
     * 
     * @return a description of the file extensions permitted by this filter.
     */
    public String getDescription() {
        if (isExtensionListInDescription() && filters.size() > 0) {
            StringBuffer extensions = new StringBuffer("(");
            Iterator i = filters.keySet().iterator();
            extensions.append(".").append((String)i.next());
            while (i.hasNext()) {
                extensions.append(", .").append((String)i.next());
            }
            extensions.append(")");
            if (description != null) {
                return description + " " + extensions.toString();
            }
            else {
                return extensions.toString();
            }
        }
        else {
            return description;
        }
    }

    /**
     * Sets the human readable description of this filter. For example:
     * filter.setDescription("Gif and JPG Images");
     * 
     * @param desc
     *            the description.
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should show up in
     * the human readable description.
     * 
     * Only relevent if a description was provided in the constructor or using
     * setDescription();
     * 
     * @param b
     *            true or false
     */
    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        description = null;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should show up in
     * the human readable description.
     * 
     * Only relevent if a description was provided in the constructor or using
     * setDescription();
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[DefaultFileFilter description=").append(description).append(", extensions=").append(filters);
        return buf.toString();
    }
}