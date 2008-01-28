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

import org.springframework.richclient.application.ApplicationLauncher;

/**
 * Main driver that starts the pet clinic rich client sample application.
 */
public class PetClinicClientServer {

    public static void main(String[] args) {
        try {
            String rootContextDirectoryClassPath = "/org/springframework/richclient/samples/petclinic/ctx";

            String startupContextPath = rootContextDirectoryClassPath + "/common/richclient-startup-context.xml";

            String richclientApplicationContextPath = rootContextDirectoryClassPath
                    + "/common/richclient-application-context.xml";

            String businessLayerClientContextPath = rootContextDirectoryClassPath + "/clientserver/client-context.xml";

            String securityContextPath = rootContextDirectoryClassPath + "/clientserver/security-context-client.xml";

            new ApplicationLauncher(startupContextPath, new String[] { richclientApplicationContextPath,
                    businessLayerClientContextPath, securityContextPath });
        } catch (Exception e) {
            System.exit(1);
        }
    }
}