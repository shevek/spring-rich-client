package org.springframework.richclient.dialog.selection;

/**
 * A <code>SelectionFilter</code> tells whether an object is acceptable for a filter.
 * @author peter.de.bruycker
 */
public interface SelectionFilter {
    public boolean accept(Object obj, String filter);
}
