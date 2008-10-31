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
import java.io.IOException;

import org.springframework.rules.closure.Closure;
import org.springframework.rules.constraint.Constraint;
import org.springframework.core.io.Resource;
import org.springframework.rules.factory.Constraints;

/**
 * @author Keith Donald
 */
public class FileChecks {
    private static FileExists exists = new FileExists();

    private static FileIsFile file = new FileIsFile();

    private static FileIsReadable readable = new FileIsReadable();

    private static FileConverter fileConverter = new FileConverter();

    private FileChecks() {

    }

    public static Constraint readableFileCheck() {
        Constraints c = Constraints.instance();
        Constraint checks = c.all(new Constraint[] { exists, file, readable });
        return c.testResultOf(fileConverter, checks);
    }

    public static class FileExists implements Constraint {
        public boolean test(Object argument) {
            File f = (File)argument;
            return f != null && f.exists();
        }
    }

    public static class FileIsFile implements Constraint {
        public boolean test(Object argument) {
            File f = (File)argument;
            return f != null && !f.isDirectory();
        }
    }

    public static class FileIsReadable implements Constraint {
        public boolean test(Object argument) {
            File f = (File)argument;
            return f != null && f.canRead();
        }
    }

    public static class FileConverter implements Closure {
        public Object call(Object argument) {
            File f;
            if (argument == null) {
                return null;
            }
            if (argument instanceof File) {
                return argument;
            }
            if (argument instanceof String) {
                f = new File((String)argument);
            }
            else if (argument instanceof Resource) {
                try {
                    f = ((Resource)argument).getFile();
                }
                catch (IOException e) {
                    return null;
                }
            }
            else {
                f = (File)argument;
            }
            return f;
        }
    }

}