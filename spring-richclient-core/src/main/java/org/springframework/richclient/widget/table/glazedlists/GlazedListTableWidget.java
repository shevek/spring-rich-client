package org.springframework.richclient.widget.table.glazedlists;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Size;
import com.jgoodies.forms.layout.Sizes;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.xswingx.JXSearchField;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.util.ValueMonitor;
import org.springframework.richclient.widget.AbstractWidget;
import org.springframework.richclient.widget.table.TableCellRenderers;
import org.springframework.richclient.widget.table.TableDescription;
import org.springframework.richclient.widget.table.TableWidget;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * ListTableWidget is een factory voor sorteerbare, filterbare tabellen op basis
 * van het glazedlists project.
 */
public final class GlazedListTableWidget extends AbstractWidget implements TableWidget
{
    /**
     * De visuele tabel. Component van swingx library.
     */
    private JXTable theTable = new JXTable();

    /**
     * Scrollpane die rond de tabel staat.
     */
    private JScrollPane tableScroller;

    /**
     * Monitor die de selectie events zal doorsturen de geregistreerde
     * listeners.
     */
    private ValueMonitor selectionMonitor = new ValueMonitor();

    /**
     * Achterliggend TableModel van glazedLists.
     */
    private EventTableModel<Object> tableModel;

    /**
     * Achterliggend selectieModel van glazedLists.
     */
    private EventSelectionModel<Object> selectionModel;

    /**
     * De volledige datalijst.
     */
    private EventList<Object> dataList;

    /**
     * De getoonde lijst na sortering, filtering...
     */
    private EventList<Object> shownList;

    /**
     * De gesorteerde lijst, null indien geen sortering (niet comparable).
     */
    private SortedList<Object> sortedList;

    /**
     * Het textField dat een filtering op de datalijst verzorgt, weerspiegelt in
     * de getoonde lijst.
     */
    private JTextField textFilterField;

    /**
     * De navigatieCommando's als array.
     */
    private AbstractCommand[] navigationCommands;

    /**
     * CommandGroup met navigatieCommando's.
     */
    private CommandGroup navigationCommandGroup;

    /**
     * De commando's specifiek voor de selectiekolom (unselect all, select all,
     * reverse selection).
     */
    private CommandGroup selectColumnCommandGroup;

    /**
     * Een specifieke configurer voor de navigatie en selectiecommands.
     */
    private CommandConfigurer commandConfigurer;

    /**
     * countLabel geeft het aantal records en de geselecteerde rij weer : record / aantal
     */
    private JLabel countLabel;

    static
    {
        UIManager.put("JXTable.column.horizontalScroll", RcpSupport.getMessage("JXTable.horizontalScroll.label"));
        UIManager.put("JXTable.column.packAll", RcpSupport.getMessage("JXTable.packAll.label"));
        UIManager.put("JXTable.column.packSelected", RcpSupport.getMessage("JXTable.packSelected.label"));
    }

    /**
     * CellEditorListener op de selectiekolom om de selectieListeners gezamelijk
     * te triggeren .
     */
    private CellEditorListener userSelectionCellEditorListener = new CellEditorListener()
    {

        public void editingStopped(ChangeEvent e)
        {
            fireUserSelectionChangedEvent();
        }

        public void editingCanceled(ChangeEvent e)
        {
        }
    };

    private Set dirtyRows = new HashSet();

    private CellEditorListener dirtyRowCellEditorListener = new CellEditorListener()
    {

        public void editingCanceled(ChangeEvent e)
        {
        }


        public void editingStopped(ChangeEvent e)
        {
            dirtyRows.add(getSelectedRows()[0]);
        }
    };

    /**
     * De listeners geregistreerd op de selectiekolom, getriggerd door
     * {@link #userSelectionCellEditorListener}.
     */
    private List<PropertyChangeListener> userSelectionListeners;

    public GlazedListTableWidget(List<? extends Object> rows, TableDescription tableDesc)
    {
        this(rows, tableDesc, tableDesc.getDefaultComparator());
    }

    public GlazedListTableWidget(List<? extends Object> rows, TableDescription tableDesc,
                                 Comparator comparator)
    {
        this(tableDesc.getDataType(), rows, GlazedListsSupport.makeTableFormat(tableDesc), GlazedListsSupport
                .makeFilterProperties(tableDesc), comparator, tableDesc.hasSelectColumn());
        // Als de tablewidget met ons eigen TableDescription class is gemaakt
        // kunnen we additionele dingen als width/resizable/renderer en editor
        // zetten
        // bedenking: zouden we tabledesc van een iterator voorzien om over de
        // kolommen te lopen?
        TableCellEditor columnEditor = null;
        for (int i = 0; i < tableDesc.getColumnCount(); ++i)
        {
            TableColumnExt column = (TableColumnExt) theTable.getColumns(true).get(i);
            int columnWidth = tableDesc.getMaxColumnWidth(i);
            if (columnWidth > 0)
            {
                column.setMaxWidth(columnWidth);
            }
            columnWidth = tableDesc.getMinColumnWidth(i);
            if (columnWidth > 0)
            {
                column.setMinWidth(columnWidth);
            }
            column.setResizable(tableDesc.isResizable(i));
            column.setVisible(tableDesc.isVisible(i));
            columnEditor = tableDesc.getColumnEditor(i);
            if (columnEditor != null)
            {
                if (tableDesc.isSelectColumn(i))
                {
                    columnEditor.addCellEditorListener(userSelectionCellEditorListener);
                }
                else
                {
                    columnEditor.addCellEditorListener(dirtyRowCellEditorListener);
                }
                column.setCellEditor(columnEditor);
            }
            if (tableDesc.getColumnRenderer(i) != null)
            {
                TableCellRenderer renderer = tableDesc.getColumnRenderer(i);
                column.setCellRenderer(renderer);
                if (renderer instanceof DefaultTableCellRenderer)
                {
                    int align = ((DefaultTableCellRenderer) renderer).getHorizontalAlignment();
                    switch (align)
                    {
                        case SwingConstants.CENTER:
                            column.setHeaderRenderer(wrapInSortArrowHeaderRenderer(TableCellRenderers.CENTER_ALIGNED_HEADER_RENDERER));
                            break;
                        case SwingConstants.RIGHT:
                            column.setHeaderRenderer(wrapInSortArrowHeaderRenderer(TableCellRenderers.RIGHT_ALIGNED_HEADER_RENDERER));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private TableCellRenderer wrapInSortArrowHeaderRenderer(TableCellRenderer renderer)
    {
        if (tableComparatorChooser != null)
        {
            return tableComparatorChooser.createSortArrowHeaderRenderer(renderer);
        }
        else
        {
            return renderer;
        }
    }

    public GlazedListTableWidget(Class dataType, List<? extends Object> rows, TableFormat format,
                                 String[] filterProperties)
    {
        this(dataType, rows, format, filterProperties, null, false);
    }

    public GlazedListTableWidget(Class dataType, List<? extends Object> rows, TableFormat format,
                                 String[] filterProperties, Comparator comparator, boolean addHighlightSelectColumn)
    {
        // eventTableSelectionModel van glazedLists werkt niet goed samen met
        // SelectionMapper
        // https://glazedlists.dev.java.net/issues/show_bug.cgi?id=363
        theTable.setColumnControlVisible(true);
        theTable.getSelectionMapper().setEnabled(false);
        commandConfigurer = (CommandConfigurer) Application.services().getService(CommandConfigurer.class);

        // -jh- vertrekken van lege lijst (= null) mag ook
        dataList = rows == null ? new BasicEventList<Object>() : GlazedLists.eventList(rows);

        // eventueel sortering toepassen (bij null : originele volgorde).
        sortedList = new SortedList<Object>(dataList, comparator);
        this.shownList = sortedList;

        if (filterProperties != null)
        {
            textFilterField = new JXSearchField(RcpSupport.getMessage("glazedListTableWidget.textFilterField.prompt"));
            textFilterField.addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusGained(FocusEvent e)
                {
                    textFilterField.selectAll();
                }
            });
            shownList = new FilterList<Object>(shownList,
                    new TextComponentMatcherEditor(textFilterField, GlazedLists.textFilterator(dataType,
                            filterProperties)));
        }

        tableModel = new EventTableModel<Object>(shownList, format);
        theTable.setModel(tableModel);

        if (addHighlightSelectColumn)
        {
            Highlighter selectHighlighter = new ColorHighlighter(HIGHLIGHTSELECTCOLUMN, new Color(0xF0, 0xF0, 0xE0), Color.BLACK);
            setHighlighters(HighlighterFactory.createSimpleStriping(), selectHighlighter);
            initializeSelectColumnCommands();
        }
        else
        {
            setHighlighters(HighlighterFactory.createSimpleStriping());
        }

        selectionModel = new EventSelectionModel<Object>(shownList);
        selectionModel.addListSelectionListener(new SelectionNavigationListener());
        theTable.setSelectionModel(selectionModel);

        if (sortedList != null)
        {
            theTable.setSortable(false);
            theTable.getTableHeader().setDefaultRenderer(TableCellRenderers.LEFT_ALIGNED_HEADER_RENDERER);
            tableComparatorChooser = TableComparatorChooser
                    .install(theTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE_WITH_UNDO);
            // the following is a fix for the selection sort and navigation problem
            tableComparatorChooser.addSortActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    EventList<Object> selected = selectionModel.getSelected();
                    int[] indexes = new int[selected.size()];
                    int i = 0;
                    for (Object o : selected)
                    {
                        indexes[i++] = shownList.indexOf(o);
                    }
                    selectionModel.clearSelection();
                    for (int index : indexes)
                    {
                        selectionModel.addSelectionInterval(index, index);
                    }
                }
            });
        }       
        
        theTable.setPreferredScrollableViewportSize(new Dimension(50, 50));
        tableScroller = new JScrollPane(theTable);
        theTable.setHorizontalScrollEnabled(true);
        initializeNavigationCommands();
    }

    /**
     * Enable the row height to diverge from the default height.
     * <p/>
     * NOTE: this is experimental as there is a problem with glazedlists and jxtable.
     * (see note on ExtendedJXTable above)
     */
    public void setRowHeightEnabled(boolean rowHeightEnabled)
    {
        // toelaten de rowheight aan te passen, zodat er meerdere lijnen per cell kunnen gerenderd worden.
        theTable.setRowHeightEnabled(true);
    }

    /**
     * SelectionListener die de navigatieknoppen aan en af zet.
     */
    private class SelectionNavigationListener implements ListSelectionListener
    {

        public void valueChanged(ListSelectionEvent e)
        {
            // enkel op einde van reeks selection veranderingen reageren.
            if (!e.getValueIsAdjusting())
            {
                if (selectionModel.getSelected().size() == 1)
                {
                    selectionMonitor.setValue(selectionModel.getSelected().get(0));
                }
                else
                {
                    Object[] selectedRows = selectionModel.getSelected().toArray();
                    selectionMonitor.setValue(selectedRows.length > 0 ? selectedRows : null);
                }

                int selectedIndex = selectionModel.getAnchorSelectionIndex();
                int lastIndex = shownList.size() - 1;
                boolean emptyList = (lastIndex == -1);
                boolean onFirst = (selectedIndex == 0);
                boolean onLast = (selectedIndex == lastIndex);

                navigationCommands[NAVIGATE_FIRST].setEnabled(!emptyList && !onFirst);
                navigationCommands[NAVIGATE_PREVIOUS].setEnabled(!emptyList && !onFirst);
                navigationCommands[NAVIGATE_NEXT].setEnabled(!emptyList && !onLast);
                navigationCommands[NAVIGATE_LAST].setEnabled(!emptyList && !onLast);
            }
        }
    }

    public static final HighlightPredicate HIGHLIGHTSELECTCOLUMN = new HighlightSelectColumn();

    private TableComparatorChooser tableComparatorChooser;

    /**
     * Deze class is een extensie op ConditionalHighlighter die er voor zorgt
     * dat de rijen/cellen die voldoen aan de test kunnen worden opgelicht.
     *
     * @author jh
     */
    static class HighlightSelectColumn implements HighlightPredicate
    {

        /**
         * Test op de rij of de rij geselecteerd was met behulp van de checkbox
         * in de eerste kolom.
         *
         * @return boolean TRUE indien getrouwd.
         */
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter)
        {
            Object selectedValue = adapter.getValueAt(adapter.row, 0);
            return Boolean.TRUE.equals(selectedValue);
        }
    }

    /**
     * Zet een pipeline van highlighters om de rijen een verschillende kleur te
     * geven wanneer bepaalde voorwaarden voldaan zijn.
     *
     * @param highlighters highlighters die gebruikt moet worden bij deze table.
     */
    public void setHighlighters(Highlighter... highlighters)
    {
        this.theTable.setHighlighters(highlighters);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return this.dataList.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public int nrOfRows()
    {
        return this.tableModel.getRowCount();
    }

    /**
     * Initializatie en configuratie van de navigatiecommando's.
     */
    private void initializeNavigationCommands()
    {
        this.navigationCommands = new AbstractCommand[4];
        this.navigationCommands[NAVIGATE_FIRST] = new ActionCommand(NAVIGATE_FIRSTROW_CMDID)
        {

            @Override
            protected void doExecuteCommand()
            {
                selectionModel.setSelectionInterval(0, 0);
                scrollToSelectedRow();
            }
        };
        this.navigationCommands[NAVIGATE_PREVIOUS] = new ActionCommand(NAVIGATE_PREVIOUSROW_CMDID)
        {

            @Override
            protected void doExecuteCommand()
            {
                int newIndex = selectionModel.getAnchorSelectionIndex() - 1;
                newIndex = (newIndex < 0) ? 0 : newIndex;
                selectionModel.setSelectionInterval(newIndex, newIndex);
                scrollToSelectedRow();
            }
        };
        this.navigationCommands[NAVIGATE_NEXT] = new ActionCommand(NAVIGATE_NEXTROW_CMDID)
        {

            @Override
            protected void doExecuteCommand()
            {
                int newIndex = selectionModel.getAnchorSelectionIndex() + 1;
                int lastIndex = shownList.size() - 1;
                newIndex = (newIndex > lastIndex) ? lastIndex : newIndex;
                selectionModel.setSelectionInterval(newIndex, newIndex);
                scrollToSelectedRow();
            }
        };
        this.navigationCommands[NAVIGATE_LAST] = new ActionCommand(NAVIGATE_LASTROW_CMDID)
        {

            @Override
            protected void doExecuteCommand()
            {
                int lastIndex = shownList.size() - 1;
                selectionModel.setSelectionInterval(lastIndex, lastIndex);
                scrollToSelectedRow();
            }
        };

        for (int i = 0; i < this.navigationCommands.length; i++)
        {
            this.commandConfigurer.configure(this.navigationCommands[i]);
            this.navigationCommands[i].setEnabled(false);
        }
        this.navigationCommandGroup = CommandGroup.createCommandGroup(this.navigationCommands);
    }

    /**
     * Verandering in selectiekolom.
     */
    private void fireUserSelectionChangedEvent()
    {
        if (userSelectionListeners != null)
        {
            for (Iterator listeners = userSelectionListeners.iterator(); listeners.hasNext();)
            {
                PropertyChangeListener listener = (PropertyChangeListener) listeners.next();
                listener.propertyChange(new PropertyChangeEvent(this, "selection", null, null));
            }
        }
    }

    /**
     * Indien de tabel een selectiekolom bevat (met checkboxjes) kan je hierop
     * een listener registreren.
     *
     * @param listener
     */
    public void addUserSelectionListener(PropertyChangeListener listener)
    {
        if (userSelectionListeners == null)
        {
            userSelectionListeners = new ArrayList<PropertyChangeListener>();
        }
        userSelectionListeners.add(listener);
    }

    /**
     * Aanmaken en configureren van selectieCommando's. (select all, select
     * inverse, select none)
     */
    private void initializeSelectColumnCommands()
    {
        final WritableTableFormat writableTableFormat = (WritableTableFormat) this.tableModel
                .getTableFormat();
        AbstractCommand selectAll = new ActionCommand(SELECT_ALL_ID)
        {

            @Override
            protected void doExecuteCommand()
            {
                shownList.getReadWriteLock().writeLock().lock();
                Iterator i = shownList.iterator();
                while (i.hasNext())
                {
                    writableTableFormat.setColumnValue(i.next(), Boolean.TRUE, 0);
                }
                shownList.getReadWriteLock().writeLock().unlock();
                theTable.repaint();
                fireUserSelectionChangedEvent();
            }
        };
        this.commandConfigurer.configure(selectAll);
        AbstractCommand selectNone = new ActionCommand(SELECT_NONE_ID)
        {

            @Override
            protected void doExecuteCommand()
            {
                shownList.getReadWriteLock().writeLock().lock();
                Iterator i = shownList.iterator();
                while (i.hasNext())
                {
                    writableTableFormat.setColumnValue(i.next(), Boolean.FALSE, 0);
                }
                shownList.getReadWriteLock().writeLock().unlock();
                theTable.repaint();
                fireUserSelectionChangedEvent();
            }
        };
        this.commandConfigurer.configure(selectNone);
        AbstractCommand selectInverse = new ActionCommand(SELECT_INVERSE_ID)
        {

            @Override
            protected void doExecuteCommand()
            {
                shownList.getReadWriteLock().writeLock().lock();
                Iterator i = shownList.iterator();
                while (i.hasNext())
                {
                    Object rowObject = i.next();
                    Object columnValue = writableTableFormat.getColumnValue(rowObject, 0);
                    writableTableFormat.setColumnValue(rowObject, Boolean.TRUE.equals(columnValue)
                            ? Boolean.FALSE
                            : Boolean.TRUE, 0);
                }
                shownList.getReadWriteLock().writeLock().unlock();
                theTable.repaint();
                fireUserSelectionChangedEvent();
            }
        };
        this.commandConfigurer.configure(selectInverse);
        this.selectColumnCommandGroup = CommandGroup.createCommandGroup(new Object[]{selectAll, selectNone,
                selectInverse});
    }

    /**
     * Vervang de datalijst objecten met de gegeven objecten.
     *
     * @param newRows de lijst van nieuwe dataobjecten.
     */
    public final void setRows(Collection newRows)
    {
        this.dataList.getReadWriteLock().writeLock().lock();
        try
        {
            this.dirtyRows.clear();
            theTable.clearSelection();
            this.dataList.clear();
            this.dataList.addAll(newRows);

            scrollToSelectedRow(); // new rows, scroll back to top
        }
        finally
        {
            this.dataList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Geef alle rijen in de dataLijst terug. Inclusief rijen die momenteel niet
     * zichtbaar zijn door de textFithis.viewControllerObjectlter. De sortering kan ook afwijken van die
     * in de tabel. Indien je een lijst van de momenteel zichtbare items wilt,
     * gebruik dan {@ link #getVisibleRows()}.
     *
     * @return een lijst met alle dataObjecten in (zonder sortering en zonder de
     *         lokale filter).
     * @see #getVisibleRows()
     */
    public final List getRows()
    {
        return new ArrayList<Object>(this.dataList);
    }

    /**
     * Geef de rijen terug zoals getoond in de tabel: dwz rekening houdend met
     * de lokale filter en de sortering. Indien je de volledige lijst wilt,
     * gebruik dan {@link #getRows()}.
     *
     * @return de lijst zoals getoond in de tabel.
     * @see #getRows()
     */
    public final List getVisibleRows()
    {
        return new ArrayList<Object>(this.shownList);
    }

    /**
     * Toevoegen van een object aan de dataLijst.
     *
     * @param newObject het nieuwe lijstObject.
     */
    public void addRowObject(Object newObject)
    {
        this.dataList.getReadWriteLock().writeLock().lock();
        try
        {
            this.dataList.add(newObject);
        }
        finally
        {
            this.dataList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Een collectie toevoegen aan de datalijst.
     *
     * @param rows de collection met de toe te voegen objecten.
     */
    public void addRows(Collection rows)
    {
        this.dataList.getReadWriteLock().writeLock().lock();
        try
        {
            this.dataList.addAll(rows);
        }
        finally
        {
            this.dataList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Verwijder een object uit de dataLijst.
     *
     * @param objectToRemove het te verwijderen object.
     */
    public void removeRowObject(Object objectToRemove)
    {
        this.dataList.getReadWriteLock().writeLock().lock();
        try
        {
            dirtyRows.remove(objectToRemove);
            this.dataList.remove(objectToRemove);
        }
        finally
        {
            this.dataList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Selecteer het gegeven object in de tabel.
     *
     * @param toPointTo           het object dat moet geselecteerd worden.
     * @param originatingObserver observer geregistreerd op selectieEvents die het event dat zal
     *                            gestuurd worden bij oproepen van deze functie niet meer mag
     *                            krijgen. (tegengaan van circulaire oproepen)
     * @return int de index van het object in de getoonde lijst.
     */
    public int selectRowObject(Object toPointTo, Observer originatingObserver)
    {
        int index = this.shownList.indexOf(toPointTo);
        selectRowObject(index, originatingObserver);
        return index;
    }

    /**
     * Selecteer het object op de gegeven index (van de getoonde lijst).
     *
     * @param index               de index van het object in de getoonde lijst dat moet
     *                            geselecteerd worden. Indien index -1 is zal er een
     *                            deselectAll() gebeuren.
     * @param originatingObserver observer geregistreerd op selectieEvents die het event dat zal
     *                            gestuurd worden bij oproepen van deze functie niet meer mag
     *                            krijgen. (tegengaan van circulaire oproepen)
     */
    public void selectRowObject(final int index, final Observer originatingObserver)
    {
        Runnable doSelectRowObject = new Runnable()
        {

            public void run()
            {
                if (originatingObserver != null)
                {
                    selectionMonitor.deleteObserver(originatingObserver);
                }

                if ((index > -1) && (shownList.size() > index))
                {
                    selectionModel.setSelectionInterval(index, index);
                }
                else
                {
                    selectionModel.clearSelection();
                }
                scrollToSelectedRow();

                if (originatingObserver != null)
                {
                    selectionMonitor.addObserver(originatingObserver);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread())
        {
            doSelectRowObject.run();
        }
        else
        {
            SwingUtilities.invokeLater(doSelectRowObject);
        }

    }

    /**
     * Voeg de gegeven rijen toe aan de selectie.
     *
     * @param rows                de rijen die aan de selectie moeten worden toegevoegd.
     * @param originatingObserver observer geregistreerd op selectieEvents die het event dat zal
     *                            gestuurd worden bij oproepen van deze functie niet meer mag
     *                            krijgen. (tegengaan van circulaire oproepen)
     */
    public void addSelection(final Object[] rows, final Observer originatingObserver)
    {
        Runnable doAddSelection = new Runnable()
        {
            public void run()
            {
                if (originatingObserver != null)
                {
                    selectionMonitor.deleteObserver(originatingObserver);
                }
                for (int i = 0; i < rows.length; i++)
                {
                    int index = shownList.indexOf(rows[i]);
                    selectionModel.addSelectionInterval(index, index);
                }
                if (originatingObserver != null)
                {
                    selectionMonitor.addObserver(originatingObserver);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread())
        {
            doAddSelection.run();
        }
        else
        {
            SwingUtilities.invokeLater(doAddSelection);
        }
    }

    /**
     * Check of de huidig getoonde lijst een selectie bevat.
     *
     * @return <code>true</code> als er een selectie bestaat.
     */
    public boolean hasSelection()
    {
        return !this.selectionModel.isSelectionEmpty();
    }

    /**
     * Scroll naar het eerste element dat is geselecteerd
     */
    public synchronized void scrollToSelectedRow()
    {
        Runnable doScrollToSelectedRow = new Runnable()
        {
            public void run()
            {
                if (theTable.isVisible())
                {
                    int selectedRow = theTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        Rectangle cellRect = theTable.getCellRect(selectedRow, 0, true);
                        Rectangle viewRect = tableScroller.getViewport().getViewRect();
                        if (!viewRect.contains(cellRect))
                        {
                            if (cellRect.y < viewRect.y) // cell is above view (or cut above)
                            {
                                tableScroller.getViewport().setViewPosition(cellRect.getLocation());
                            }
                            else // cell is below view (or cut below)
                            {
                                tableScroller.getViewport().scrollRectToVisible(cellRect);
                            }
                        }
                    }
                    else
                    {
                        tableScroller.getViewport().setViewPosition(new Point(0, 0));
                    }
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread())
        {
            doScrollToSelectedRow.run();
        }
        else
        {
            SwingUtilities.invokeLater(doScrollToSelectedRow);
        }
    }

    /**
     * Vervang een object in de datalijst. Indien het object geselecteerd was,
     * zal deze selectie overgenomen worden op het nieuwe object.
     *
     * @param oldObject           het te vervangen object.
     * @param newObject           het nieuwe object dat in de plaats komt, ook toegevoegd aan de
     *                            selectie indien het oude object geselecteerd was.
     * @param originatingObserver optionele observer die geen events meer mag ontvangen van de
     *                            selectie veranderingen die gebeuren tijdens een replace. Als
     *                            de te vervangen rij geselecteerd was, krijg je een event
     *                            wanneer het oude object wordt verwijderd en wanneer het nieuwe
     *                            wordt gezet.
     */
    public void replaceRowObject(Object oldObject, Object newObject, Observer originatingObserver)
    {
        this.dataList.getReadWriteLock().writeLock().lock();
        try
        {
            dirtyRows.remove(oldObject);
            int index = this.dataList.indexOf(oldObject);
            if (index != -1)
            {
                // oppassen hier: selectionModel werkt op getoonde lijst met
                // andere indexen als dataList
                boolean wasSelected = this.selectionModel.isSelectedIndex(this.shownList.indexOf(oldObject));

                if (wasSelected && (originatingObserver != null))
                {
                    this.selectionMonitor.deleteObserver(originatingObserver);
                }

                this.dataList.set(index, newObject);

                if (wasSelected)
                {
                    int indexToSelect = this.shownList.indexOf(newObject);
                    this.selectionModel.addSelectionInterval(indexToSelect, indexToSelect);
                    if (originatingObserver != null)
                    {
                        this.selectionMonitor.addObserver(originatingObserver);
                    }
                }
            }
        }
        finally
        {
            this.dataList.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * 'Vervang' een aantal rijen: zal een collectie rijen verwijderen en een
     * andere collectie toevoegen. Is meer een shortcut van multiple object
     * toevoegen en verwijderen in 1 beweging.
     *
     * @param oldObject de lijst met te verwijderen objecten.
     * @param newObject de lijst met toe te voegen objecten.
     */
    public void replaceRows(final Collection oldObject, final Collection newObject)
    {
        Runnable doReplaceRows = new Runnable()
        {
            public void run()
            {
                dataList.getReadWriteLock().writeLock().lock();
                try
                {
                    dirtyRows.clear();
                    dataList.removeAll(oldObject);
                    dataList.addAll(newObject);
                }
                finally
                {
                    dataList.getReadWriteLock().writeLock().unlock();
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread())
        {
            doReplaceRows.run();
        }
        else
        {
            SwingUtilities.invokeLater(doReplaceRows);
        }
    }

    /**
     * Deselecteer alles.
     */
    public void unSelectAll()
    {
        Runnable doUnselectAll = new Runnable()
        {
            public void run()
            {
                selectionModel.clearSelection();
            }
        };
        if (SwingUtilities.isEventDispatchThread())
        {
            doUnselectAll.run();
        }
        else
        {
            SwingUtilities.invokeLater(doUnselectAll);
        }
    }

    /**
     * Geef de selectie terug.
     *
     * @return een collectie met de huidige geselecteerde rijen.
     */
    public Object[] getSelectedRows()
    {
        return this.selectionModel.getSelected().toArray();
    }

    /**
     * Geeft de component van deze widget terug, in dit geval een scrollpane met
     * een tabel in.
     *
     * @return JComponent, de scrollPane met de tabel.
     */
    public JComponent getComponent()
    {
        return this.tableScroller;
    }

    /**
     * Als je de tabel wilt raadplegen, kan je niet op {@link #getComponent()}
     * vertrouwen. Daarom hier een method om specifiek de tabel terug te
     * krijgen.
     *
     * @return JTable met de getoonde lijst.
     */
    public JTable getTable()
    {
        return this.theTable;
    }

    /**
     * Voeg een observer toe die selectie events moet krijgen.
     *
     * @param observer de observer die selectie events moet krijgen.
     */
    public void addSelectionObserver(Observer observer)
    {
        this.selectionMonitor.addObserver(observer);
    }

    public void removeSelectionObserver(Observer observer)
    {
        this.selectionMonitor.deleteObserver(observer);
    }

    public void addTableModelListener(TableModelListener listener)
    {
        this.tableModel.addTableModelListener(listener);
    }

    public void removeTableModelListener(TableModelListener listener)
    {
        this.tableModel.removeTableModelListener(listener);
    }

    public void updateTable()
    {
        this.tableModel.fireTableDataChanged();
    }

    public JTextField getTextFilterField()
    {
        return textFilterField;
    }

    public AbstractCommand[] getNavigationCommands()
    {
        return navigationCommands;
    }

    public JComponent getNavigationButtonBar()
    {
        return getNavigationButtonBar(Sizes.PREFERRED, BorderFactory.createEmptyBorder());
    }

    public JComponent getNavigationButtonBar(Size size, Border border)
    {
        return this.navigationCommandGroup.createButtonBar(size, border);
    }

    public CommandGroup getNavigationCommandGroup()
    {
        return this.navigationCommandGroup;
    }

    public CommandGroup getSelectColumnCommandGroup()
    {
        return this.selectColumnCommandGroup;
    }

    public JComponent getSelectButtonBar()
    {
        return this.selectColumnCommandGroup.createButtonBar(Sizes.PREFERRED, BorderFactory
                .createEmptyBorder());
    }

    public JComponent getButtonBar()
    {
        if (this.selectColumnCommandGroup != null)
        {
            JPanel buttons = new JPanel(new FormLayout("fill:pref, 3dlu, fill:pref, 3dlu, fill:pref",
                    "fill:pref:grow"));
            CellConstraints cc = new CellConstraints();
            buttons.add(getSelectButtonBar(), cc.xy(1, 1));
            buttons.add(new JSeparator(SwingConstants.VERTICAL), cc.xy(3, 1));
            buttons.add(getNavigationButtonBar(), cc.xy(5, 1));
            return buttons;
        }
        return getNavigationButtonBar();
    }

    public JLabel getListSummaryLabel()
    {
        if (countLabel == null)
        {
            countLabel = createCountLabel();
        }
        return countLabel;
    }

    private JLabel createCountLabel()
    {
        final JLabel label = new JLabel("");

        // aangepaste text invullen:
        setTextForListSummaryLabel(label);

        /* Deze listener is nodig om de veranderingen van de lijst de detecteren
         * bv. boven aan in de quick-filter wat text toevoegen */
        shownList.addListEventListener(new ListEventListener<Object>()
        {
            public void listChanged(ListEvent<Object> evt)
            {
                if (!evt.isReordering())
                {
                    setTextForListSummaryLabel(label);
                }
            }
        });

        /*
         *   de ListEventListener wordt niet altijd geactiveerd wanneer een item uit de lijst wordt geselecteerd.
         *   of de geselecteerde index is pas aangepast na de ListEventListener is uitgevoerd.
         */
        theTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    // index :
                    // Integer.toString(e.getFirstIndex() + 1) dit kan de index zijn van een item dat gedeselecteerd wordt.
                    setTextForListSummaryLabel(label);
                }
            }

        });

        return label;
    }

    private void setTextForListSummaryLabel(final JLabel label)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                Integer index = 0;
                Integer selectedCount = 0;
                Integer totalCount = shownList.size();

                if (getSelectedRows() != null && getSelectedRows().length > 0)
                {
                    index = shownList.indexOf(getSelectedRows()[0]);
                    index++;
                    selectedCount = getSelectedRows().length;
                }

                label.setText(RcpSupport.getMessage("glazedListTableWidget", "listSummary", "label", new Object[]{index, selectedCount, totalCount}));
            }
        });
    }


    @Override
    public void onAboutToShow()
    {
        super.onAboutToShow();
        this.theTable.requestFocusInWindow();
    }

    public Set getDirtyRows()
    {
        return dirtyRows;
    }

}