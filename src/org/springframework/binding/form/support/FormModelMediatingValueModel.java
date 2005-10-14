package org.springframework.binding.form.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.AbstractValueModelWrapper;
import org.springframework.binding.value.support.DirtyTrackingValueModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.util.EventListenerListHelper;

/**
 * Mediates between the base property value model and the derived view value models. 
 * 
 * TODO: allow for disconnection of the property valuemodel form the view value models; so 
 * for instance we can bulk update all properties without efcting the view.  
 * 
 * <p>
 * NOTE: This is a framework internal class and should not be
 * instantiated in user code. 
 * 
 * @author Oliver Hutchison
 */
public class FormModelMediatingValueModel extends AbstractValueModelWrapper implements
        DirtyTrackingValueModel, PropertyChangeListener {

    private final EventListenerListHelper dirtyChangeListeners = new EventListenerListHelper(
            PropertyChangeListener.class);

    private Object originalValue;

    private boolean settingValue;

    private boolean oldDirty;

    private final boolean trackDirty;
    
    private ValueChangeDetector valueChangeDetector;


    public FormModelMediatingValueModel(ValueModel propertyValueModel) {
        this(propertyValueModel, true);
    }

    public FormModelMediatingValueModel(ValueModel propertyValueModel, boolean trackDirty) {
        super(propertyValueModel);
        propertyValueModel.addValueChangeListener(this);
        originalValue = getValue();
        this.trackDirty = trackDirty;
    }

    public void setValueSilently(Object value, PropertyChangeListener listenerToSkip) {
        try {
            settingValue = true;
            super.setValueSilently(value, listenerToSkip);
            valueUpdated();
        }
        finally {
            settingValue = false;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!settingValue) {
            originalValue = getValue();
            valueUpdated();
        }
    }

    public boolean isDirty() {
        return this.trackDirty &&
            getValueChangeDetector().hasValueChanged(originalValue, getValue());
    }

    public void clearDirty() {
        if (this.trackDirty) {
            originalValue = getValue();
            valueUpdated();
        }
    }
    
    public void revertToOriginal() {
        if (this.trackDirty) {
            setValue(originalValue);
        }
    }

    protected void valueUpdated() {
        boolean dirty = isDirty();
        if (oldDirty != dirty) {
            oldDirty = dirty;
            firePropertyChange(DIRTY_PROPERTY, !dirty, dirty);
        }
    }

    public void setValueChangeDetector(ValueChangeDetector valueChangeDetector) {
        this.valueChangeDetector = valueChangeDetector;
    }

    protected ValueChangeDetector getValueChangeDetector() {
        if( valueChangeDetector == null ) {
            valueChangeDetector = Application.services().getValueChangeDetector();
        }
        return valueChangeDetector;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            dirtyChangeListeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            dirtyChangeListeners.remove(listener);
        }
    }

    protected final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (DIRTY_PROPERTY.equals(propertyName)) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, Boolean.valueOf(oldValue),
                    Boolean.valueOf(newValue));
            for (Iterator i = dirtyChangeListeners.iterator(); i.hasNext();) {
                ((PropertyChangeListener)i.next()).propertyChange(evt);
            }
        }
    }
}