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
package org.springframework.richclient.forms;

import java.text.DateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.BoundValueModel;
import org.springframework.binding.value.support.BufferedValueModel;

/**
 * PropertyEditorValueSetterTests
 * @author Marc Portier
 */
public class PropertyEditorValueSetterTests extends TestCase {

    public void testLeakDownToBufferedValueModel() {

        MyObject firstModel = new MyObject(new Date());

        BeanPropertyAccessStrategy propsAccessor = new BeanPropertyAccessStrategy(firstModel);
        BoundValueModel domainObjectHolder = propsAccessor.getDomainObjectHolder();
        BufferedValueModel buffer = new BufferedValueModel(propsAccessor.getPropertyValueModel("someDate"));

        MyPropertyEditor editor = new MyPropertyEditor();

        new PropertyEditorValueSetter(editor, buffer);

        // 0. just checking if the link works for sure
        buffer.setValue(new Date(2));
        assertTrue("editor not updated!", editor.isChangedByEvent());
        editor.setValue(new Date(3));
        assertTrue("value not updated", buffer.isDirty());

        //1. now we test setting through the editor, and verify that it didn't
        // get an event back
        editor.setChangedByEvent(false); //resetting
        editor.setValueWithoutChanging(new Date(1));
        assertFalse("the editor did get an event back while being the source!", editor.isChangedByEvent());

        //2. we test changing the object behind the buffer
        // based on the original problem that made us find out:
        Object secondModel = new MyObject(new Date(0));
        buffer.revert();
        domainObjectHolder.setValue(secondModel);
        assertFalse("buffer should not become dirty by setting wrappedModel\n"
                + "-- issue originally reported as bug rpc-65 \n"
                + "-- [http://opensource.atlassian.com/projects/spring/browse/RCP-65] ", buffer.isDirty());
    }

    static class MyObject {

        private Date someDate;

        public MyObject(Date initDate) {
            setSomeDate(initDate);
        }

        public void setSomeDate(Object value) {
            this.someDate = (Date) value;

        }

        public Date getSomeDate() {
            return this.someDate;
        }
    }

    static class MyPropertyEditor extends CustomDateEditor {

        private boolean changedByEvent = false;

        public MyPropertyEditor() {
            super(DateFormat.getDateInstance(DateFormat.MEDIUM), true);
        }

        public void setValueWithoutChanging(Object value) {
            super.setValue(value);
        }

        public void setValue(Object value) {
            super.setValue(value);
            changedByEvent = true;
        }

        public boolean isChangedByEvent() {
            return changedByEvent;
        }

        public void setChangedByEvent(boolean changed) {
            this.changedByEvent = changed;
        }
    }

}