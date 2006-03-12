package org.springframework.binding.form.support;

import junit.framework.TestCase;

import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.support.TestBean;


public class FormModelPropertyAccessStrategyTests extends TestCase
{

    protected AbstractFormModel getFormModel(Object formObject) 
    {
        return new TestAbstractFormModel(formObject);
    }
    
    /**
     * Test to ensure that the AccessStrategy works correctly with writeable/readable properties.     *
     */
    public void testReadOnlyPropertyAccess()
    {
        AbstractFormModel model = getFormModel(new TestBean());
        PropertyAccessStrategy propertyAccessStrategy = model.getPropertyAccessStrategy();
        PropertyMetadataAccessStrategy metaDataAccessStrategy = propertyAccessStrategy.getMetadataAccessStrategy();

        assertFalse("Property is readonly, isWriteable() should return false.", metaDataAccessStrategy.isWriteable("readOnly"));
        assertTrue("Property is readonly, isReadable() should return true.", metaDataAccessStrategy.isReadable("readOnly"));
    }
}
