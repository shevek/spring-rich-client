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
package org.springframework.richclient.application;

/**
 * A registry for {@link PageDescriptor} definitions.
 *
 * @author Keith Donald
 * @author Rogan Dawes
 *
 */
public interface PageDescriptorRegistry {

    /**
     * Returns an array of all the page descriptors in the registry.
     *
     * @return An array of all the page descriptors in the registry. The array may be empty but
     * will never be null.
     */
    public PageDescriptor[] getPageDescriptors();

    /**
     * Returns the page descriptor with the given identifier, or null if no such descriptor
     * exists in the registry.
     *
     * @param pageDescriptorId The id of the page descriptor to be returned.
     * @return The page descriptor with the given id, or null.
     *
     * @throws IllegalArgumentException if {@code pageDescriptorId} is null.
     */
    public PageDescriptor getPageDescriptor(String pageDescriptorId);

}
