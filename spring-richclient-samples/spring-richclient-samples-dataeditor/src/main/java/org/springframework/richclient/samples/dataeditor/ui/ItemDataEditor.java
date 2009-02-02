package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.richclient.samples.dataeditor.domain.Item;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.richclient.widget.table.PropertyColumnTableDescription;

public class ItemDataEditor extends DefaultDataEditorWidget
{
    private static final PropertyColumnTableDescription TABLE_DESCRIPTION;

    static
    {
        TABLE_DESCRIPTION = new PropertyColumnTableDescription("itemDataEditor", Item.class);
        TABLE_DESCRIPTION.addPropertyColumn("name");
        TABLE_DESCRIPTION.addPropertyColumn("description");
        TABLE_DESCRIPTION.addPropertyColumn("supplier.name");
    }

    public ItemDataEditor(ItemDataProvider itemDataProvider)
    {
         super("itemDataEditor", itemDataProvider, new ItemForm(), TABLE_DESCRIPTION, new ItemFilterForm());
    }
}
