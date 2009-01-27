package org.springframework.richclient.form;

import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.widget.editor.provider.DataFilterModel;

public abstract class FilterForm extends AbstractForm implements TitleConfigurable
{

    private String title;

    protected FilterForm()
    {
    }

    protected FilterForm(String id)
    {
        super(id);
    }

    @Override
    protected void init()
    {
        Object filterModel = newFormObject();
        setFormModel(FormModelHelper.createFormModel(filterModel));
        getFormModel().setId(getId());
        getObjectConfigurer().configure(this, getId());
    }

    /**
     * Levert de ingestelde filter-criteria op het formulier.
     *
     * @see DataFilterModel#getCriteria()
     */
    public Object getFilterCriteria()
    {
        return getFormObject();
    }

    /**
     * Herstelt de criteria-instellingen (clear).
     */
    public void resetCriteria()
    {
        this.setFormObject(newFormObject());
    }

    /**
     * Het object gebruikt om te filteren, kan een {@link DataFilterModel} zijn,
     * maar ook elk ander willekeurig object.
     *
     * @return <b>not</b> <code>null</code> formObject voor filterForm
     */
    protected Object newFormObject()
    {
        return null;
    }

    public void setDefaultCriteria()
    {
        Object newFormObject = newFormObject();
        setFormObject(newFormObject);
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
}
