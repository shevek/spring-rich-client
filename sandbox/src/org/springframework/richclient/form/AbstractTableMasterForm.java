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

import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.table.support.GlazedTableModel;
import org.springframework.richclient.util.PopupMenuMouseListener;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.Matcher;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

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

    private EventList _eventList;
    private JTable _masterTable;
    private Matcher _matcher;
    private MatcherEditor _matcherEditor;
    private Comparator _comparator;

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
        _matcher = matcher;
    }

    /**
     * Get the <code>Matcher</code> to be used in filtering the elements of the master
     * set.
     * 
     * @return matcher
     */
    public Matcher getFilterMatcher() {
        return _matcher;
    }

    /**
     * Set the <code>MatcherEditor</code> to be used in filtering the elements of the
     * master set. Note that only one of a Matcher or MatcherEditor may be used, not both.
     * If both are specified, then the Matcher will take precedence.
     * 
     * @param matcherEditor The MatcherEditor to use to filter elements in the master set.
     */
    public void setFilterMatcherEditor(MatcherEditor matcherEditor) {
        _matcherEditor = matcherEditor;
    }

    /**
     * Get the <code>MatcherEditor</code> to be used in filtering the elements of the
     * master set.
     * 
     * @return matcherEditor
     */
    public MatcherEditor getFilterMatcherEditor() {
        return _matcherEditor;
    }

    /**
     * Set the comparator to use for sorting the table.
     * 
     * @param comparator to use for sorting the table
     */
    public void setSortComparator(Comparator comparator) {
        _comparator = comparator;
    }

    /**
     * Get the comparator to use for sorting the table.
     * 
     * @return comparator to use for sorting the table
     */
    public Comparator getSortComparator() {
        return _comparator;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.form.AbstractForm#createFormControl()
     */
    protected JComponent createFormControl() {

        _eventList = getRootEventList();

        // Install the matcher if configured (this will filter the list)
        if( _matcher != null ) {
            _eventList = new FilterList( _eventList, _matcher );
        } else if( _matcherEditor != null ) {
            _eventList = new FilterList( _eventList, _matcherEditor );
        }

        // Install the sorter if configured
        SortedList sortedList = null;
        if( _comparator != null ) {
            _eventList = sortedList = new SortedList( _eventList, _comparator );
        }

        // Install this new event list configuration (sorting and filtering)
        installEventList( _eventList );

        _masterTable = createTable( createTableModel() );

        // Finish the sorting installation
        if( _comparator != null ) {
            new TableComparatorChooser( _masterTable, sortedList, true );
        }

        // If we have either a sort or a filter, we need a special selection model
        if( _comparator != null || _matcher != null ) {
            EventSelectionModel selectionModel = new EventSelectionModel( _eventList );
            _masterTable.setSelectionModel( selectionModel );
        }

        // _masterTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        _masterTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

        // Setup our selection listener so that it controls the detail form
        installSelectionHandler();

        // Enable a popup menu
        _masterTable.addMouseListener( new PopupMenuMouseListener( getPopupMenu() ) );

        // Avoid the default viewport size of 450,400
        Dimension ps = getMasterTablePreferredSize( _masterTable.getPreferredSize() );
        _masterTable.setPreferredScrollableViewportSize( ps );

        JScrollPane sp = new JScrollPane( _masterTable );

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        panel.add( sp, BorderLayout.CENTER );
        panel.add( createButtonBar(), BorderLayout.SOUTH );

        // Now put the two forms into a split pane
        JSplitPane splitter = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        splitter.add( panel );
        splitter.add( getDetailForm().getControl() );
        splitter.setResizeWeight( 1.0d );

        final SwingBindingFactory sbf = (SwingBindingFactory) getBindingFactory();
        TableFormBuilder formBuilder = new TableFormBuilder( sbf );
        formBuilder.getLayoutBuilder().cell( splitter, "align=default,default rowSpec=fill:default:g" );

        updateControlsForState();

        return formBuilder.getForm();
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
        return new GlazedTableModel( _eventList, getMessageSource(), getColumnPropertyNames(), getId() ) {
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
        return _masterTable != null ? _masterTable.getSelectionModel() : null;
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
        return _eventList;
    }

    /**
     * @param list The eventList to set.
     */
    protected void setEventList(EventList list) {
        _eventList = list;
    }

    /**
     * @return Returns the masterTable.
     */
    protected JTable getMasterTable() {
        return _masterTable;
    }

    /**
     * @return Returns the masterTableModel.
     */
    protected TableModel getMasterTableModel() {
        return getMasterTable() != null ? getMasterTable().getModel() : null;
    }

}
