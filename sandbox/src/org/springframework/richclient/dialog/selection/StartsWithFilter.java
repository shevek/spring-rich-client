package org.springframework.richclient.dialog.selection;

/**
 * @author peter.de.bruycker
 */
public class StartsWithFilter extends StringFilter {

    /** 
     * @see org.springframework.richclient.dialog.StringFilter#doAccept(java.lang.String, java.lang.String)
     */
    protected boolean doAccept(String str, String filter) {
        return str.startsWith(filter);
    }

}