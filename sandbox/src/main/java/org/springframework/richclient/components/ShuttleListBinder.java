/**
 *
 */
package org.springframework.richclient.components;

import java.util.Comparator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

/**
 * Binder for handling ShuttleList component. Use the following keys to
 * configure the produced binding:
 * <dt><code>SELECTABLE_ITEMS_HOLDER_KEY</code></dt>
 * <dd> to specify the list of "available" source values (this may be a
 * Collection or an array).</dd>
 * <p>
 * <dt><code>SELECTED_ITEMS_HOLDER_KEY</code></dt>
 * <dd>to specify the value holder into which the selected items will be placed
 * (this may be a Collection or an array). Initially, this set must contain only
 * values that exist in the selectable set.</dd>
 * <p>
 * <dt><code>SELECTED_ITEM_TYPE_KEY</code></dt>
 * <dd>to specify the underlying type of the elements in the selected and
 * selectable value sets.</dd>
 * <p>
 * <dt><code>COMPARATOR_KEY</code></dt>
 * <dd>to specify the Comparator to use for comparing elements in the selected
 * and selectable value sets.</dd>
 * <p>
 * <dt><code>RENDERER_KEY</code></dt>
 * <dd>to specify the a {@link ListCellRenderer} for elements of the value
 * sets. This is typically used if the String value of the elements is not
 * appropriate for use in the shuttle lists.</dd>
 * <p>
 * <dt><code>FORM_ID</code></dt>
 * <dd>to specify formId in which this ShuttleList appears, this allow
 * form-specific settings like the texts and icon.</dd>
 * 
 * @author lstreepy
 * @author Benoit Xhenseval
 */
public class ShuttleListBinder extends AbstractBinder {

    public static final String SELECTABLE_ITEMS_HOLDER_KEY = "selectableItemsHolder";

    public static final String SELECTED_ITEMS_HOLDER_KEY = "selectedItemHolder";

    public static final String SELECTED_ITEM_TYPE_KEY = "selectedItemType";

    public static final String MODEL_KEY = "model";

    public static final String FORM_ID = "formId";

    public static final String COMPARATOR_KEY = "comparator";

    public static final String RENDERER_KEY = "renderer";

    public ShuttleListBinder() {
        super(null, new String[] { SELECTABLE_ITEMS_HOLDER_KEY, SELECTED_ITEMS_HOLDER_KEY, SELECTED_ITEM_TYPE_KEY,
                MODEL_KEY, COMPARATOR_KEY, RENDERER_KEY, FORM_ID });
    }

    public ShuttleListBinder(final String[] supportedContextKeys) {
        super(null, supportedContextKeys);
    }

    /**
     * @inheritDoc
     */
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof ShuttleList, formPropertyPath);
        ShuttleListBinding binding = new ShuttleListBinding((ShuttleList) control, formModel, formPropertyPath);
        applyContext(binding, context);
        return binding;
    }

    protected void applyContext(ShuttleListBinding binding, Map context) {
        if (context.containsKey(MODEL_KEY)) {
            binding.setModel((ListModel) context.get(MODEL_KEY));
        }
        if (context.containsKey(SELECTABLE_ITEMS_HOLDER_KEY)) {
            binding.setSelectableItemsHolder((ValueModel) context.get(SELECTABLE_ITEMS_HOLDER_KEY));
        }
        if (context.containsKey(SELECTED_ITEMS_HOLDER_KEY)) {
            binding.setSelectedItemsHolder((ValueModel) context.get(SELECTED_ITEMS_HOLDER_KEY));
        }
        if (context.containsKey(RENDERER_KEY)) {
            binding.setRenderer((ListCellRenderer) context.get(RENDERER_KEY));
        }
        if (context.containsKey(COMPARATOR_KEY)) {
            binding.setComparator((Comparator) context.get(COMPARATOR_KEY));
        }
        if (context.containsKey(SELECTED_ITEM_TYPE_KEY)) {
            binding.setSelectedItemType((Class) context.get(SELECTED_ITEM_TYPE_KEY));
        }
        if (context.containsKey(FORM_ID)) {
            binding.setFormId((String) context.get(FORM_ID));
        }
    }

    /**
     * @inheritDoc
     */
    protected JComponent createControl(Map context) {
        return new ShuttleList();
    }
}
