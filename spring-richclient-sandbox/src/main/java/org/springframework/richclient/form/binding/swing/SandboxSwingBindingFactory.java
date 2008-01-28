package org.springframework.richclient.form.binding.swing;

import java.util.Map;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.binding.Binding;

import org.springframework.richclient.components.ShuttleList;
import org.springframework.richclient.components.ShuttleListBinder;

/**
 * A convenient extension of <code>SwingBindingFactory</code>. Provides a set
 * of methods that address the typical binding requirements of Swing components
 * in the sandbox.
 * <p>
 * In order to use this factory, specify the
 * {@link SandboxSwingBindingFactoryProvider} as the binding factory provider in
 * the application context. Like this:
 * 
 * <pre>
 *      &lt;bean id=&quot;bindingFactoryProvider&quot; class=&quot;org.springframework.richclient.form.binding.swing.SandboxSwingBindingFactoryProvider&quot; /&gt;
 * </pre>
 * 
 * @author Larry Streepy
 * 
 */
public class SandboxSwingBindingFactory extends SwingBindingFactory {

    /**
     * Constructor. Use specified formModel.
     * 
     * @param formModel
     */
    public SandboxSwingBindingFactory( ConfigurableFormModel formModel ) {
        super(formModel);
    }

    /**
     * Binds the values specified in the collection contained within
     * <code>selectableItemsHolder</code> to a {@link ShuttleList}, with any
     * user selection being placed in the form property referred to by
     * <code>selectionFormProperty</code>. Each item in the list will be
     * rendered by looking up a property on the item by the name contained in
     * <code>renderedProperty</code>, retrieving the value of the property,
     * and rendering that value in the UI.
     * <p>
     * Note that the selection in the bound list will track any changes to the
     * <code>selectionFormProperty</code>. This is especially useful to
     * preselect items in the list - if <code>selectionFormProperty</code> is
     * not empty when the list is bound, then its content will be used for the
     * initial selection.
     * 
     * @param selectionFormProperty form property to hold user's selection. This
     *        property must be a <code>Collection</code> or array type.
     * @param selectableItemsHolder <code>ValueModel</code> containing the
     *        items with which to populate the list.
     * @param renderedProperty the property to be queried for each item in the
     *        list, the result of which will be used to render that item in the
     *        UI. May be null, in which case the selectable items will be
     *        rendered as strings.
     * @return constructed {@link Binding}. Note that the bound control is of
     *         type {@link ShuttleList}. Access this component to set specific
     *         display properties.
     */
    public Binding createBoundShuttleList( String selectionFormProperty, ValueModel selectableItemsHolder,
            String renderedProperty ) {
        Map context = ShuttleListBinder.createBindingContext(getFormModel(), selectionFormProperty,
                selectableItemsHolder, renderedProperty);
        return createBinding(ShuttleList.class, selectionFormProperty, context);
    }

    /**
     * Binds the values specified in the collection contained within
     * <code>selectableItems</code> (which will be wrapped in a
     * {@link ValueHolder} to a {@link ShuttleList}, with any user selection
     * being placed in the form property referred to by
     * <code>selectionFormProperty</code>. Each item in the list will be
     * rendered by looking up a property on the item by the name contained in
     * <code>renderedProperty</code>, retrieving the value of the property,
     * and rendering that value in the UI.
     * <p>
     * Note that the selection in the bound list will track any changes to the
     * <code>selectionFormProperty</code>. This is especially useful to
     * preselect items in the list - if <code>selectionFormProperty</code> is
     * not empty when the list is bound, then its content will be used for the
     * initial selection.
     * 
     * @param selectionFormProperty form property to hold user's selection. This
     *        property must be a <code>Collection</code> or array type.
     * @param selectableItems Collection or array containing the items with
     *        which to populate the selectable list (this object will be wrapped
     *        in a ValueHolder).
     * @param renderedProperty the property to be queried for each item in the
     *        list, the result of which will be used to render that item in the
     *        UI. May be null, in which case the selectable items will be
     *        rendered as strings.
     * @return constructed {@link Binding}. Note that the bound control is of
     *         type {@link ShuttleList}. Access this component to set specific
     *         display properties.
     */
    public Binding createBoundShuttleList( String selectionFormProperty, Object selectableItems, String renderedProperty ) {
        return createBoundShuttleList(selectionFormProperty, new ValueHolder(selectableItems), renderedProperty);
    }

    /**
     * Binds the values specified in the collection contained within
     * <code>selectableItems</code> (which will be wrapped in a
     * {@link ValueHolder} to a {@link ShuttleList}, with any user selection
     * being placed in the form property referred to by
     * <code>selectionFormProperty</code>. Each item in the list will be
     * rendered as a String.
     * <p>
     * Note that the selection in the bound list will track any changes to the
     * <code>selectionFormProperty</code>. This is especially useful to
     * preselect items in the list - if <code>selectionFormProperty</code> is
     * not empty when the list is bound, then its content will be used for the
     * initial selection.
     * 
     * @param selectionFormProperty form property to hold user's selection. This
     *        property must be a <code>Collection</code> or array type.
     * @param selectableItems Collection or array containing the items with
     *        which to populate the selectable list (this object will be wrapped
     *        in a ValueHolder).
     * @return constructed {@link Binding}. Note that the bound control is of
     *         type {@link ShuttleList}. Access this component to set specific
     *         display properties.
     */
    public Binding createBoundShuttleList( String selectionFormProperty, Object selectableItems ) {
        return createBoundShuttleList(selectionFormProperty, new ValueHolder(selectableItems), null);
    }

}
