package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.richclient.samples.dataeditor.domain.ItemFilter;
import org.springframework.richclient.samples.dataeditor.domain.ItemService;
import org.springframework.richclient.widget.editor.provider.AbstractDataProvider;

import java.util.List;

public class ItemDataProvider extends AbstractDataProvider
{
    private ItemService service;

    public ItemDataProvider(ItemService service)
    {
        this.service = service;
    }

    public boolean supportsFiltering()
    {
        return true;
    }

    public List getList(Object criteria)
    {
        if (criteria instanceof ItemFilter)
        {
            ItemFilter itemFilter = (ItemFilter) criteria;
            return service.findItems(itemFilter);
        }
        else
        {
            throw new IllegalArgumentException("This provider can only filter through ItemFilter, not " + criteria.getClass());
        }
    }

    public boolean supportsUpdate()
    {
        return true;
    }

    public boolean supportsCreate()
    {
        return true;
    }

    public boolean supportsClone()
    {
        return false;
    }

    public boolean supportsDelete()
    {
        return true;
    }

     @Override
    public Object doCreate(Object newData)
    {
        return newData;
    }

    @Override
    public void doDelete(Object dataToRemove)
    {
    }

    @Override
    public Object doUpdate(Object updatedData)
    {
        return updatedData;
    }
}
