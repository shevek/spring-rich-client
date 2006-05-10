package org.springframework.richclient.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;

public class TestableBindingFactory implements BindingFactory {

    private int bindControlCount;
    private List controls = new ArrayList();
    private List contexts = new ArrayList();
    private List propertyPaths = new ArrayList();

    public FormModel getFormModel() {
        return null;
    }

    public Binding createBinding( String formPropertyPath ) {
        return null;
    }

    public Binding createBinding( String formPropertyPath, Map context ) {
        return null;
    }

    public Binding createBinding( Class controlType, String formPropertyPath ) {
        return null;
    }

    public Binding createBinding( Class controlType, String formPropertyPath, Map context ) {
        return null;
    }

    public Binding bindControl( JComponent control, String formPropertyPath ) {
        return null;
    }

    public Binding bindControl( JComponent control, String formPropertyPath, Map context ) {
        bindControlCount++;

        controls.add( control );
        propertyPaths.add( formPropertyPath );
        contexts.add( context );

        return null;
    }

    public int getBindControlCount() {
        return bindControlCount;
    }

    public List getPropertyPaths() {
        return propertyPaths;
    }

    public List getControls() {
        return controls;
    }

    public List getContexts() {
        return contexts;
    }
}
