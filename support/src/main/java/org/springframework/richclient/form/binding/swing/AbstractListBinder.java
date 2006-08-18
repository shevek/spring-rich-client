/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.form.binding.swing;

import java.util.Comparator;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.core.closure.Closure;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;

/**
 * @author Mathias Broekelmann
 * 
 */
public abstract class AbstractListBinder extends AbstractBinder {

    public static final String SELECTABLE_ITEMS_KEY = "selectableItems";

    public static final String COMPARATOR_KEY = "comparator";

    public static final String FILTER_KEY = "filter";

    private Object selectableItems;

    private Comparator comparator;

    private Constraint filter;

    public AbstractListBinder(Class requiredSourceClass) {
        this(requiredSourceClass, new String[] { SELECTABLE_ITEMS_KEY, COMPARATOR_KEY, FILTER_KEY });
    }

    public AbstractListBinder(Class requiredSourceClass, String[] supportedContextKeys) {
        super(requiredSourceClass, supportedContextKeys);
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public Constraint getFilter() {
        return filter;
    }

    public void setFilter(Constraint filter) {
        this.filter = filter;
    }

    public Object getSelectableItems() {
        return selectableItems;
    }

    public void setSelectableItems(Object selectableItems) {
        this.selectableItems = selectableItems;
    }

    protected final Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        AbstractListBinding binding = createListBinding(control, formModel, formPropertyPath);
        applyContext(binding, context);
        return binding;
    }

    protected abstract AbstractListBinding createListBinding(JComponent control, FormModel formModel,
            String formPropertyPath);

    /**
     * @param binding
     * @param context
     */
    protected void applyContext(AbstractListBinding binding, Map context) {
        if (context.containsKey(SELECTABLE_ITEMS_KEY)) {
            binding.setSelectableItems(context.get(SELECTABLE_ITEMS_KEY));
        } else if (selectableItems != null) {
            binding.setSelectableItems(selectableItems);
        }
        if (context.containsKey(COMPARATOR_KEY)) {
            binding.setComparator((Comparator) context.get(COMPARATOR_KEY));
        } else if (comparator != null) {
            binding.setComparator(comparator);
        }
        if (context.containsKey(FILTER_KEY)) {
            binding.setFilter((Constraint) context.get(FILTER_KEY));
        } else if (filter != null) {
            binding.setFilter(filter);
        }
    }

    /**
     * Decorates an object instance if the closure param is an instance of {@link Closure}.
     * 
     * @param closure
     *            the closure which is used to decorate the object
     * @param object
     * @return
     */
    protected Object decorate(Object closure, Object object) {
        if (closure instanceof Closure) {
            return ((Closure) closure).call(object);
        }
        return closure;
    }
}
