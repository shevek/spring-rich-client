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
package org.springframework.richclient.command.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractButton;

import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class CommandButtonManager implements PropertyChangeListener {
    private ReferenceQueue queue = new ReferenceQueue();

    private Set buttons = new HashSet(6);

    private CommandFaceDescriptor face;

    private static class ManagedButton {

        private WeakReference buttonHolder;

        private CommandButtonConfigurer buttonConfigurer;

        public ManagedButton(AbstractButton button,
                CommandButtonConfigurer buttonConfigurer, ReferenceQueue queue) {
            this.buttonHolder = new WeakReference(button, queue);
            this.buttonConfigurer = buttonConfigurer;
        }

        public AbstractButton getButton() {
            return (AbstractButton)buttonHolder.get();
        }

        public boolean equals(Object o) {
            return ObjectUtils.nullSafeEquals(getButton(), ((ManagedButton)o)
                    .getButton());
        }

        public int hashCode() {
            return getButton().hashCode();
        }
    }

    public CommandButtonManager(CommandFaceDescriptor face) {
        setFaceDescriptor(face);
    }

    public void setFaceDescriptor(CommandFaceDescriptor face) {
        if (!ObjectUtils.nullSafeEquals(this.face, face)) {
            if (this.face != null) {
                this.face.removePropertyChangeListener(this);
            }
            this.face = face;
            this.face.addPropertyChangeListener(this);
        }
    }

    public void attachAndConfigure(AbstractButton button,
            CommandButtonConfigurer strategy) {
        Assert.notNull(button);
        Assert.notNull(strategy);
        cleanUp();
        ManagedButton managedButton = new ManagedButton(button, strategy, queue);
        if (buttons.add(managedButton)) {
            configure(button, strategy);
        }
    }

    private void cleanUp() {
        Reference reference;
        while ((reference = this.queue.poll()) != null) {
            buttons.remove(reference.get());
        }
    }

    protected void configure(AbstractButton button,
            CommandButtonConfigurer strategy) {
        if (face != null) {
            face.configure(button, strategy);
        }
    }

    public void detach(AbstractButton button) {
        buttons.remove(button);
    }

    public void detachAll() {
        buttons.clear();
    }

    public boolean isAttachedTo(AbstractButton button) {
        return buttons.contains(button);
    }

    public Iterator iterator() {
        return new ButtonIterator(buttons.iterator());
    }

    private static class ButtonIterator implements Iterator {
        private Iterator it;

        public ButtonIterator(Iterator it) {
            this.it = it;
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public Object next() {
            ManagedButton mb = ((ManagedButton)it.next());
            return mb.getButton();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    public void propertyChange(PropertyChangeEvent e) {
        Iterator it = buttons.iterator();
        while (it.hasNext()) {
            ManagedButton mb = (ManagedButton)it.next();
            Assert.notNull(mb, "Managed button reference cannot be null");
            if (mb.getButton() == null) {
                it.remove();
            }
            else {
                configure(mb.getButton(), mb.buttonConfigurer);
            }
        }
    }

}