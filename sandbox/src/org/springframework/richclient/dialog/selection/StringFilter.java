package org.springframework.richclient.dialog.selection;

/**
 * @author peter.de.bruycker
 */
public abstract class StringFilter implements SelectionFilter {

    /** 
     * @see org.springframework.richclient.dialog.SelectionFilter#accept(java.lang.Object, java.lang.String)
     */
    public boolean accept(Object obj, String filter) {
        return doAccept((String)obj, filter == null ? "" : filter.trim());
    }

    protected abstract boolean doAccept(String str, String filter);
}