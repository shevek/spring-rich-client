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

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.config.CommandFaceDescriptorRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ToStringCreator;

public class CommandFaceButtonManager implements PropertyChangeListener {
    private ReferenceQueue queue = new ReferenceQueue();

    private Set buttons = new HashSet(6);

    private AbstractCommand command;

    private String faceDescriptorId;

    private CommandFaceDescriptor faceDescriptor;

    private CommandFaceDescriptorRegistry faceDescriptorRegistry;

    private static class ManagedButton {
        private WeakReference buttonHolder;

        private CommandButtonConfigurer buttonConfigurer;

        public ManagedButton(AbstractButton button, CommandButtonConfigurer buttonConfigurer, ReferenceQueue queue) {
            this.buttonHolder = new WeakReference(button, queue);
            this.buttonConfigurer = buttonConfigurer;
        }

        public AbstractButton getButton() {
            return (AbstractButton)buttonHolder.get();
        }

        public boolean equals(Object o) {
            return ObjectUtils.nullSafeEquals(getButton(), ((ManagedButton)o).getButton());
        }

        public int hashCode() {
            return getButton().hashCode();
        }
    }

    public CommandFaceButtonManager(AbstractCommand command, String faceDescriptorKey) {
        Assert.notNull(command, "The command to manage buttons for cannot be null");
        Assert.hasText(faceDescriptorKey, "The face descriptor key is required");
        this.command = command;
        this.faceDescriptorId = faceDescriptorKey;
    }

    public CommandFaceButtonManager(AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
        this.command = command;
        setFaceDescriptor(faceDescriptor);
    }

    public void setFaceDescriptor(CommandFaceDescriptor faceDescriptor) {
        Assert.notNull(faceDescriptor, "The face descriptor for managing command button appearance is required");
        if (!ObjectUtils.nullSafeEquals(this.faceDescriptor, faceDescriptor)) {
            if (this.faceDescriptor != null) {
                this.faceDescriptor.removePropertyChangeListener(this);
            }
            this.faceDescriptor = faceDescriptor;
            this.faceDescriptor.addPropertyChangeListener(this);
            propertyChange(null);
        }
    }

    public CommandFaceDescriptor getFaceDescriptor() {
        if (this.faceDescriptor == null) {
            setFaceDescriptor(getFaceDescriptor(this.faceDescriptorId));
        }
        return faceDescriptor;
    }

    private CommandFaceDescriptor getFaceDescriptor(String faceDescriptorId) {
        if (command.getFaceDescriptorRegistry() != null) {
            return command.getFaceDescriptorRegistry().getFaceDescriptor(command, faceDescriptorId);
        }
        else {
            return CommandFaceDescriptor.BLANK_FACE_DESCRIPTOR;
        }
    }

    public boolean isFaceConfigured() {
        return this.faceDescriptor != null && this.faceDescriptor != CommandFaceDescriptor.BLANK_FACE_DESCRIPTOR;
    }

    public void attachAndConfigure(AbstractButton button, CommandButtonConfigurer strategy) {
        Assert.notNull(button, "The button to attach and configure is required");
        Assert.notNull(strategy, "The button configuration strategy is required");
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

    protected void configure(AbstractButton button, CommandButtonConfigurer strategy) {
        getFaceDescriptor().configure(button, command, strategy);
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

    public String toString() {
        return new ToStringCreator(this).append("commandId", command.getId()).append("faceDescriptor", faceDescriptor)
                .append("attachedButtonCount", buttons.size()).toString();
    }

}