/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.table.support.GlazedTableModel;
import org.springframework.richclient.util.PopupMenuMouseListener;

/**
 * This is an abstract implementation of AbstractMasterForm that uses a GlazedTableModel
 * and JTable to represent the master information.
 * <p>
 * Derived types must implement:
 * <p>
 * <dt>{@link #getColumnPropertyNames()}</dt>
 * <dd>To specify the properties for the table columns</dd>
 * <dt>{@link AbstractMasterForm#createDetailForm}</dt>
 * <dd>To construct the detail half of this master/detail form pair</dd>
 * 
 * @author Larry Streepy
 */
public abstract class AbstractTableMasterForm extends AbstractMasterForm {

    private EventList eventList;
    private JTable masterTable;
    private Matcher matcher;
    private MatcherEditor matcherEditor;
    private Comparator comparator;

    /**
     * Construct a new AbstractTableMasterForm using the given parent form model and
     * property path. The form model for this class will be constructed by getting the
     * value model of the specified property from the parent form model and constructing a
     * DeepCopyBufferedCollectionValueModel on top of it. Unless
     * {@link AbstractMasterForm#getListListModel()} has been overriden, the table will
     * contain all the elements in the domain object referenced by <code>property</code>.
     * 
     * @param parentFormModel Parent form model to access for this form's data
     * @param property Property containing this forms data (must be a collection or an
     *            array)
     * @param formId Id of this form
     * @param detailType Type of detail object managed by this master form
     */
    public AbstractTableMasterForm(HierarchicalFormModel parentFormModel, String property, String formId,
            Class detailType) {
        super( parentFormModel, property, formId, detailType );
    }

    /**
     * Set the <code>Matcher</code> to be used in filtering the elements of the master
     * set. Note that only one of a Matcher or MatcherEditor may be used, not both. If
     * both are specified, then the Matcher will take precedence.
     * 
     * @param matcher The Matcher to use to filter elements in the master set.
     */
    public void setFilterMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    /**
     * Get the <code>Matcher</code> to be used in filtering the elements of the master
     * set.
     * 
     * @return matcher
     */
    public Matcher getFilterMatcher() {
        return matcher;
    }

    /**
     * Set the <code>MatcherEditor</code> to be used in filtering the elements of the
     * master set. Note that only one of a Matcher or MatcherEditor may be used, not both.
     * If both are specified, then the Matcher will take precedence.
     * 
     * @param matcherEditor The MatcherEditor to use to filter elements in the master set.
     */
    public void setFilterMatcherEditor(MatcherEditor matcherEditor) {
        this.matcherEditor = matcherEditor;
    }

    /**
     * Get the <code>MatcherEditor</code> to be used in filtering the elements of the
     * master set.
     * 
     * @return matcherEditor
     */
    public MatcherEditor getFilterMatcherEditor() {
        return matcherEditor;
    }

    /**
     * Set the comparator to use for sorting the table.
     * 
     * @param comparator to use for sorting the table
     */
    public void setSortComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Get the comparator to use for sorting the table.
     * 
     * @return comparator to use for sorting the table
     */
    public Comparator getSortComparator() {
        return comparator;
    }

    /**
     * Set the name of the property on which to compare for sorting elements in the master
     * table.
     * 
     * @param propertyName Name of the property on which to sort.
     */
    public void setSortProperty(String propertyName) {
        setSortComparator( new PropertyComparator( propertyName, true, true ) );
    }

    /**
     * Default is false (unless you have given a Comparator), override this method to use the
     * default SortedList from GlazedLists.
     * @return false
     */
    protected boolean useSortedList() {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.form.AbstractForm#createFormControl()
     */
    protected JComponent createFormControl() {

        configure();    // Configure all our sub-components

        eventList = getRootEventList();

        // Install the matcher if configured (this will filter the list)
        if( matcher != null ) {
            eventList = new FilterList(eventList, matcher);
        } else if( matcherEditor != null ) {
            eventList = new FilterList(eventList, matcherEditor);
        }

        // Install the sorter if configured
        SortedList sortedList = null;
        if( comparator != null || useSortedList()) {
            eventList = sortedList = new SortedList(eventList, comparator);
        }

        // Install this new event list configuration (sorting and filtering)
        installEventList(eventList);

        masterTable = createTable( createTableModel() );

        // Finish the sorting installation
        if( comparator != null || useSortedList()) {
            new TableComparatorChooser(masterTable, sortedList, true );
        }

        // If we have either a sort or a filter, we need a special selection model
        if( comparator != null || matcher != null || useSortedList()) {
            EventSelectionModel selectionModel = new EventSelectionModel(eventList);
            masterTable.setSelectionModel( selectionModel );
        }

        // masterTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        masterTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

        // Setup our selection listener so that it controls the detail form
        installSelectionHandler();

        // Enable a popup menu
        masterTable.addMouseListener( new PopupMenuMouseListener( getPopupMenu() ) );

        // Avoid the default viewport size of 450,400
        Dimension ps = getMasterTablePreferredSize( masterTable.getPreferredSize() );
        masterTable.setPreferredScrollableViewportSize( ps );

        JScrollPane sp = new JScrollPane(masterTable);

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        panel.add( sp, BorderLayout.CENTER );
        panel.add( createButtonBar(), BorderLayout.SOUTH );

        // Now put the two forms into a split pane
        JSplitPane splitter = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        configureSplitter(splitter, panel, getDetailForm().getControl());

        final SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
        TableFormBuilder formBuilder = new TableFormBuilder( sbf );
        formBuilder.getLayoutBuilder().cell( splitter, "align=default,default rowSpec=fill:default:g" );

        updateControlsForState();

        return formBuilder.getForm();
    }
    
    /**
     * Override this method is one needs to re-size/change the splitter details.
     * @param splitter
     * @param masterPanel
     * @param detailPanel
     */
    protected void configureSplitter(final JSplitPane splitter, final JPanel masterPanel, final JComponent detailPanel) {
        splitter.add( masterPanel );
        splitter.add( detailPanel );
        splitter.setResizeWeight( 1.0d );
    }

    /**
     * Create the master table.
     * @param tableModel to use in the table
     * @return table, default implementation uses the component factory to create the table
     */
    protected JTable createTable( TableModel tableModel ) {
        return getComponentFactory().createTable(tableModel);
    }

    /**
     * Create the table model for the master table.
     * @return table model to install
     */
    protected TableModel createTableModel() {
        // Make this table model read-only
        return new GlazedTableModel(eventList, getColumnPropertyNames(), getId() ) {
            protected boolean isEditable(Object row, int column) {
                return false;
            }
        };
    }

    /**
     * Get the preferred size of the master table. The current (requested) size is
     * provided for reference. This default implementation just returns the provided
     * current size.
     * 
     * @param currentSize Current (requested) preferred size of the master table
     * @return preferred size
     */
    protected Dimension getMasterTablePreferredSize(Dimension currentSize) {
        return currentSize;
    }

    /**
     * Get the selection model for the master list representation.
     * 
     * @return selection model or null if master table has not been constructed yet
     */
    protected ListSelectionModel getSelectionModel() {
        return masterTable != null ? masterTable.getSelectionModel() : null;
    }

    /**
     * Get the property names to show in columns of the master table.
     * 
     * @return String[] array of property names
     */
    protected abstract String[] getColumnPropertyNames();

    /**
     * Indicates that we are creating a new detail object.
     */
    public void creatingNewObject() {
        getSelectionModel().clearSelection();
    }

    /**
     * @return Returns the eventList.
     */
    protected EventList getEventList() {
        return eventList;
    }

    /**
     * @param list The eventList to set.
     */
    protected void setEventList(EventList list) {
        eventList = list;
    }

    /**
     * @return Returns the masterTable.
     */
    protected JTable getMasterTable() {
        return masterTable;
    }

    /**
     * @return Returns the masterTableModel.
     */
    protected TableModel getMasterTableModel() {
        return getMasterTable() != null ? getMasterTable().getModel() : null;
    }

}
