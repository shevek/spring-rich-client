package org.springframework.richclient.progress;

import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.table.ListSelectionListenerSupport;
import org.springframework.util.Assert;

/**
 * <code>ListSelectionListenerSupport</code> implementation that updates the statusbar
 * of the application. The <code>getSelectedObjectName</code> must return the string
 * representation of the selected object.
 * <br>
 * Usage:
 * <pre>
 * JTable table = ...
 * 
 * ListStatusBarUpdater updater = new ListStatusBarUpdater(getStatusBar()) {
 *     protected String getSelectedObjectName() {
 *         // return the selected Object's name
 *     }
 * };
 * 
 * table.getSelectionModel().addListSelectionListener(updater);
 * </pre>
 * @author peter.de.bruycker
 */
public abstract class ListStatusBarUpdater extends ListSelectionListenerSupport implements ListSelectionListener {

    private StatusBarCommandGroup statusBar;

    /**
     * Constructs a new <code>TableStatusBarUpdater</code> instance.
     * @param table the table
     * @param statusBar the status bar
     */
    public ListStatusBarUpdater(StatusBarCommandGroup statusBar) {
        Assert.notNull(statusBar);
        this.statusBar = statusBar;
    }

    /**
     * Returns the string representation of the selected object.
     * @return the string representation
     */
    protected abstract String getSelectedObjectName();

    /**
     * Method getStatusBar.
     * @return the status bar
     */
    public StatusBarCommandGroup getStatusBar() {
        return statusBar;
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onSingleSelection(int)
     */
    protected void onSingleSelection(int index) {
        updateStatusBar(getSelectedObjectName());
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onMultiSelection(int[])
     */
    protected void onMultiSelection(int[] indexes) {
        updateStatusBar(getItemsSelected());
    }

    /**
     * @see org.springframework.richclient.table.TableSelectionListenerSupport#onNoSelection()
     */
    protected void onNoSelection() {
        updateStatusBar(null);
    }

    private void updateStatusBar(int itemsSelected) {
        // TODO i18n this message
        getStatusBar().setMessage(itemsSelected + " items selected");
    }

    private void updateStatusBar(String selectedObjectName) {
        getStatusBar().setMessage(selectedObjectName);
    }

}