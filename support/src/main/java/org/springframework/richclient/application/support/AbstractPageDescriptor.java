/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.support.ShowPageCommand;
import org.springframework.richclient.core.LabeledObjectSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link PageDescriptor} implementations. Extends
 * {@link LabeledObjectSupport} for gui related configuration.
 *
 * @author Peter De Bruycker
 */
public abstract class AbstractPageDescriptor extends LabeledObjectSupport implements PageDescriptor, BeanNameAware,
        InitializingBean {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBeanName(String name) {
        setId(name);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.state(StringUtils.hasText(getId()), "id is mandatory");
    }

    public CommandButtonLabelInfo getShowPageCommandLabel() {
        return getLabel();
    }

    public ActionCommand createShowPageCommand(ApplicationWindow window) {
        return new ShowPageCommand(this, window);
    }


}
