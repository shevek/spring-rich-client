package org.springframework.richclient.widget.editor.provider;


/**
 * DataFilterModel specifieert een aantal specifieke methodes verwacht van form-modellen van {@link be.schaubroeck.util.springrcp.form.FilterForm}'s
 */
public interface DataFilterModel
{
    /** retourneert een map met alle huidige search-criteria */
    public Object getCriteria();

    /** het initieel ingestelde criterium */
    public void setDefaultCriterium(String input); //TODO voorzien voor gebruik in selectionmode..
}
