/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * A panel builder that provides the capability to quickly build grid based
 * forms. The builder allows for layout to be defined in a way that will be
 * familiar to anyone used to HTML tables and JGoodies Forms. Key features:
 * <ul>
 * <li>unlike HTML, cells automatically span all empty columns to the right.
 * This can be disabled by setting "colSpan=1"</li>
 * <li>support for gap rows and columns. You don't need to keep track of gap
 * rows or columns when specifying row or column spans</li>
 * <li>need only define colSpec and rowSpec when it varies from the default.
 * Save you having to work out column specs before you start laying out the 
 * form</li>
 * <li>rows and columns can be aliased with a group ID which save you having 
 * to keep track of row or column indexes for grouping. This also makes 
 * grouping less fragile when the table layout changes</li>
 * </ul>
 * <strong>Example: </strong> <br>
 * 
 * <pre>
 *    TablePanelBuilder table = new TablePanelBuilder();
 *    table
 *       .row()
 *           .separator("General 1")
 *       .row()
 *           .cell(new JLabel("Company"),"colSpec=right:pref colGrId=labels")
 *           .labelGapCol()
 *           .cell(new JFormattedTextField())
 *       .row()
 *           .cell(new JLabel("Contact"))
 *           .cell(new JFormattedTextField())
 *       .unrelatedGapRow()
 *           .separator("Propeller")
 *       .row()
 *           .cell(new JLabel("PTI [kW]"))
 *           .cell(new JFormattedTextField())
 *           .unrelatedGapCol()
 *           .cell(new JLabel("Description"), "colSpec=right:pref colGrId=labels")
 *           .labelGapCol()
 *           .cell(new JScrollPane(new JTextArea()), "rowspan=3")
 *       .row()
 *           .cell(new JLabel("R [mm]"))
 *           .cell(new JFormattedTextField())
 *           .cell()
 *       .row()
 *           .cell(new JLabel("D [mm]"))
 *           .cell(new JFormattedTextField())
 *           .cell();
 * 
 *    table.getPanel();
 * </pre>
 * 
 * @author oliverh
 */
public class TablePanelBuilder {

    private static final Log logger = LogFactory
            .getLog(TablePanelBuilder.class);

    private static final String VALIGN = "valign";

    private static final String ALIGN = "align";

    private static final String ROWSPEC = "rowspec";

    private static final String COLSPEC = "colspec";

    private static final String ROWSPAN = "rowspan";

    private static final String COLSPAN = "colspan";

    private static final String ROWGROUPID = "rowgrid";

    private static final String COLGROUPID = "colgrid";

    private List rowSpecs = new ArrayList();

    private List rowBitsSets = new ArrayList();

    private List columnSpecs = new ArrayList();

    private Map gapCols = new HashMap();

    private Map gapRows = new HashMap();

    private Map rowGroups = new HashMap();

    private Map colGroups = new HashMap();

    private int[][] adjustedColGroupIndices;

    private int[][] adjustedRowGroupIndices;

    private InternalCellConstraints lastCC = null;

    private int maxColumns = 0;

    private int currentRow = 0;

    private int currentCol = 0;

    private Map items = new HashMap();

    private FormLayout layout;

    private JPanel panel;
    
//    private List focusOrder; 

    public TablePanelBuilder() {
        this(new JPanel());
    }

    public TablePanelBuilder(JPanel panel) {
        this.layout = new FormLayout(new ColumnSpec[0], new RowSpec[0]);
        this.panel = panel;
    }

    public TablePanelBuilder table() {
        return this;
    }

    public TablePanelBuilder row() {
        return row(FormFactory.RELATED_GAP_ROWSPEC);
    }

    public TablePanelBuilder row(String rowSpec) {
        return row(new RowSpec(rowSpec));
    }

    public TablePanelBuilder row(RowSpec rowSpec) {
        gapRows.put(new Integer(currentRow), rowSpec);
        ++currentRow;
        lastCC = null;
        maxColumns = Math.max(maxColumns, currentCol);
        currentCol = 0;
        return this;
    }

    public TablePanelBuilder unrelatedGapRow() {
        return row(FormFactory.UNRELATED_GAP_ROWSPEC);
    }

    public TablePanelBuilder cell() {
        return cell("");
    }

    public TablePanelBuilder cell(String attributes) {
        cellInternal(attributes);
        return this;
    }

    public TablePanelBuilder cell(JComponent component) {
        return cell(component, "");
    }

    public TablePanelBuilder cell(JComponent component, String attributes) {
        InternalCellConstraints cc = cellInternal(attributes);
        lastCC = cc;
        items.put(component, cc);
        return this;
    }

    public TablePanelBuilder gapCol() {
        return gapCol(FormFactory.RELATED_GAP_COLSPEC);
    }

    public TablePanelBuilder gapCol(String colSpec) {
        return gapCol(new ColumnSpec(colSpec));
    }

    public TablePanelBuilder gapCol(ColumnSpec colSpec) {
        gapCols.put(new Integer(currentCol), colSpec);
        return this;
    }

    public TablePanelBuilder labelGapCol() {
        return gapCol(FormFactory.LABEL_COMPONENT_GAP_COLSPEC);
    }

    public TablePanelBuilder unrelatedGapCol() {
        return gapCol(FormFactory.UNRELATED_GAP_COLSPEC);
    }

    public TablePanelBuilder separator(String text) {
        return separator(text, "");
    }

    public TablePanelBuilder separator(String text, String attributes) {
        InternalCellConstraints cc = cellInternal(attributes);
        lastCC = cc;
        items.put(text, cc);
        return this;
    }

    private InternalCellConstraints cellInternal(String attributes) {
        nextCol();
        Map attributeMap = getAttributes(attributes);
        RowSpec rowSpec = getRowSpec(getAttribute(ROWSPEC, attributeMap, ""));
        if (rowSpec != null) {
            setRowSpec(currentCol, rowSpec);
        }
        ColumnSpec columnSpec = getColumnSpec(getAttribute(COLSPEC,
                attributeMap, ""));
        if (columnSpec != null) {
            setColumnSpec(currentCol, columnSpec);
        }
        addRowGroup(getAttribute(ROWGROUPID, attributeMap, null));
        addColGroup(getAttribute(COLGROUPID, attributeMap, null));

        InternalCellConstraints cc = getCellConstraints(attributeMap);
        currentCol = cc.endCol < cc.startCol ? cc.startCol : cc.endCol;
        markContained(cc);
        return cc;
    }

    private void addRowGroup(String groupId) {
        if (StringUtils.hasText(groupId)) {
            Set group = (Set)rowGroups.get(groupId);
            if (group == null) {
                group = new HashSet();
                rowGroups.put(groupId, group);
            }
            group.add(new Integer(currentRow));
        }
    }

    private void addColGroup(String groupId) {
        if (StringUtils.hasText(groupId)) {
            Set group = (Set)colGroups.get(groupId);
            if (group == null) {
                group = new HashSet();
                colGroups.put(groupId, group);
            }
            group.add(new Integer(currentCol));
        }
    }

    private void setRowSpec(int row, RowSpec rowSpec) {
        if (row >= rowSpecs.size()) {
            int missingSpecs = row - rowSpecs.size() + 1;
            for (int i = 0; i < missingSpecs; i++) {
                rowSpecs.add(getDefaultRowSpec());
            }
        }
        rowSpecs.set(row, rowSpec);
    }

    private void setColumnSpec(int col, ColumnSpec columnSpec) {
        col = col - 1;
        if (col >= columnSpecs.size()) {
            int missingSpecs = col - columnSpecs.size() + 1;
            for (int i = 0; i < missingSpecs; i++) {
                columnSpecs.add(getDefaultColSpec());
            }
        }
        columnSpecs.set(col, columnSpec);
    }

    private RowSpec getRowSpec(String rowSpec) {
        if (org.springframework.util.StringUtils.hasText(rowSpec)) {
            return new RowSpec(rowSpec);
        }
        else {
            return null;
        }
    }

    private ColumnSpec getColumnSpec(String columnSpec) {
        if (StringUtils.hasText(columnSpec)) {
            return new ColumnSpec(columnSpec);
        }
        else {
            return null;
        }
    }

    private void nextCol() {
        if (lastCC != null && lastCC.endCol < lastCC.startCol) {
            lastCC.endCol = lastCC.startCol;
            lastCC = null;
        }
        BitSet currentColCells = getRowBitSet(currentRow);
        do {
            ++currentCol;
        }
        while (currentColCells.get(currentCol));
    }

    private BitSet getRowBitSet(int row) {
        row = row - 1;
        if (row >= rowBitsSets.size()) {
            int missingBitSets = (row - rowBitsSets.size()) + 1;
            for (int i = 0; i < missingBitSets; i++) {
                rowBitsSets.add(new BitSet());
            }
        }
        return (BitSet)rowBitsSets.get(row);
    }

    private void markContained(InternalCellConstraints cc) {
        for (int row = cc.startRow; row <= cc.endRow; row++) {
            getRowBitSet(row).set(cc.startCol,
                    cc.endCol < cc.startCol ? cc.startCol + 1 : cc.endCol + 1);
        }
    }

    private InternalCellConstraints getCellConstraints(Map attributes) {
        String align = getAttribute(ALIGN, attributes, "default");
        String valign = getAttribute(VALIGN, attributes, "default");
        int colSpan;
        try {
            colSpan = Integer.parseInt(getAttribute(COLSPAN, attributes, "-1"));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Attribute 'colspan' must be an integer.");
        }
        int rowSpan;
        try {
            rowSpan = Integer.parseInt(getAttribute(ROWSPAN, attributes, "1"));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Attribute 'rowspan' must be an integer.");
        }

        return new InternalCellConstraints(currentCol, currentRow, colSpan,
                rowSpan, align + "," + valign);
    }

    private void fixColSpans() {
        for (Iterator i = items.values().iterator(); i.hasNext();) {
            InternalCellConstraints cc = (InternalCellConstraints)i.next();
            if (cc.endCol < cc.startCol) {
                int endCol = cc.startCol;
                BitSet currentColCells = getRowBitSet(cc.startRow);
                while (endCol < maxColumns
                        && currentColCells.get(endCol + 1) == false) {
                    ++endCol;
                }
                cc.endCol = endCol;
            }
        }
    }

    private void fillInGaps() {
        List adjustedCols = new ArrayList();
        int adjustedCol = 0;
        for (int col = 0; col < maxColumns; col++, adjustedCol++) {
            ColumnSpec colSpec = (ColumnSpec)gapCols.get(new Integer(col));
            if (colSpec != null) {
                columnSpecs.add(adjustedCol, colSpec);
                adjustedCol++;
            }
            adjustedCols.add(new Integer(adjustedCol + 1));
        }
        List adjustedRows = new ArrayList();
        int adjustedRow = 0;
        int numRows = rowSpecs.size();
        for (int row = 0; row < numRows; row++, adjustedRow++) {
            RowSpec rowSpec = (RowSpec)gapRows.get(new Integer(row));
            if (rowSpec != null) {
                rowSpecs.add(adjustedRow, rowSpec);
                adjustedRow++;
            }
            adjustedRows.add(new Integer(adjustedRow + 1));
        }
        for (Iterator i = items.values().iterator(); i.hasNext();) {
            InternalCellConstraints cc = (InternalCellConstraints)i.next();
            cc.startCol = ((Integer)adjustedCols.get(cc.startCol - 1))
                    .intValue();
            cc.endCol = ((Integer)adjustedCols.get(cc.endCol - 1)).intValue();
            cc.startRow = ((Integer)adjustedRows.get(cc.startRow - 1))
                    .intValue();
            cc.endRow = ((Integer)adjustedRows.get(cc.endRow - 1)).intValue();
        }
        adjustedColGroupIndices = new int[colGroups.size()][];
        int groupsCount = 0;
        for (Iterator i = colGroups.values().iterator(); i.hasNext();) {
            Set group = (Set)i.next();
            adjustedColGroupIndices[groupsCount] = new int[group.size()];
            int groupCount = 0;
            for (Iterator j = group.iterator(); j.hasNext();) {
                adjustedColGroupIndices[groupsCount][groupCount++] = ((Integer)adjustedCols
                        .get(((Integer)j.next()).intValue() - 1)).intValue();
            }
            groupsCount++;
        }

        adjustedRowGroupIndices = new int[rowGroups.size()][];
        groupsCount = 0;
        for (Iterator i = rowGroups.values().iterator(); i.hasNext();) {
            Set group = (Set)i.next();
            adjustedRowGroupIndices[groupsCount] = new int[group.size()];
            int groupCount = 0;
            for (Iterator j = group.iterator(); j.hasNext();) {
                adjustedRowGroupIndices[groupsCount][groupCount++] = ((Integer)adjustedRows
                        .get(((Integer)j.next()).intValue() - 1)).intValue();
            }
            groupsCount++;
        }
    }
    
//    private void buildFocusOrder() {
//        focusOrder = new ArrayList(items.size());
//        for (Iterator i = items.values().iterator(); i.hasNext();) {
//             Object o = i.next();
//             if (o instanceof JComponent) {
//                 focusOrder.add(o);
//             }            
//        }
//        Collections.reverse(focusOrder);
//    }
    

    public JPanel getPanel() {
        maxColumns = Math.max(maxColumns, currentCol);
        if (columnSpecs.size() < maxColumns) {
            setColumnSpec(maxColumns, getDefaultColSpec());
        }
        if (rowSpecs.size() < currentRow) {
            setRowSpec(currentRow, getDefaultRowSpec());
        }
        fixColSpans();
        fillInGaps();        
        for (Iterator i = columnSpecs.iterator(); i.hasNext();) {
            layout.appendColumn((ColumnSpec)i.next());
        }
        for (Iterator i = rowSpecs.iterator(); i.hasNext();) {
            layout.appendRow((RowSpec)i.next());
        }
        layout.setColumnGroups(adjustedColGroupIndices);
        layout.setRowGroups(adjustedRowGroupIndices);
        
//        buildFocusOrder();
//        panel.setFocusTraversalPolicy(new SortingFocusTraversalPolicy(new Comparator() {
//
//            public int compare(Object c1, Object c2) {
//                int offset1 = focusOrder.indexOf(c1);
//                if (offset1 < 0) {
//                    System.out.println("Unknow comp1 " + c1);
//                }
//                int offset2 = focusOrder.indexOf(c2);
//                if (offset2 < 0) {
//                    System.out.println("Unknow comp2 " + c2);
//                }
//                
//                return offset2-offset1;
//            }
//            
//        }));
        
        PanelBuilder builder = new PanelBuilder(panel, layout);
        builder.setDefaultDialogBorder();
        for (Iterator i = items.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            if (entry.getKey() instanceof JComponent) {
                builder.add((JComponent)entry.getKey(),
                        ((InternalCellConstraints)entry.getValue())
                                .getCellConstraints());
            }
            else {
                builder.addSeparator((String)entry.getKey(),
                        ((InternalCellConstraints)entry.getValue())
                                .getCellConstraints());
            }
        }
        return builder.getPanel();
    }

    private RowSpec getDefaultRowSpec() {
        return FormFactory.DEFAULT_ROWSPEC;
    }

    private ColumnSpec getDefaultColSpec() {
        return new ColumnSpec("default:grow");
    }

    private static final Set allowedAttributes;
    static {
        allowedAttributes = new HashSet();
        allowedAttributes.add(COLSPAN);
        allowedAttributes.add(ROWSPAN);
        allowedAttributes.add(COLSPEC);
        allowedAttributes.add(ROWSPEC);
        allowedAttributes.add(ALIGN);
        allowedAttributes.add(VALIGN);
        allowedAttributes.add(ROWGROUPID);
        allowedAttributes.add(COLGROUPID);
    }

    private String getAttribute(String name, Map attributeMap,
            String defaultValue) {
        String value = (String)attributeMap.get(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    private Map getAttributes(String attributes) {
        Map attributeMap = new HashMap();
        try {
            StreamTokenizer st = new StreamTokenizer(new StringReader(
                    attributes));
            st.resetSyntax();
            st.wordChars(33, 126);
            st.wordChars(128 + 32, 255);
            st.whitespaceChars(0, ' ');
            st.quoteChar('"');
            st.quoteChar('\'');
            st.ordinaryChar('=');

            String name = null;
            boolean needEquals = false;

            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (name == null && st.ttype == StreamTokenizer.TT_WORD) {
                    name = st.sval;
                    if (!allowedAttributes.contains(name.toLowerCase())) { throw new IllegalArgumentException(
                            "Attribute name '" + name + "' not recognised."); }
                    needEquals = true;
                }
                else if (needEquals && st.ttype == '=') {
                    needEquals = false;
                }
                else if (name != null
                        && (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '\''
                                | st.ttype == '"')) {
                    attributeMap.put(name.toLowerCase(), st.sval);
                    name = null;
                }
                else {
                    throw new IllegalArgumentException(
                            "Expecting '=' but found '" + st.sval + "'");
                }
            }
            if (needEquals || name != null) { throw new IllegalArgumentException(
                    "Premature end of string. Expecting "
                            + (needEquals ? " '='." : " value for attribute '"
                                    + name + "'.")); }
        }
        catch (IOException e) {
            throw new UnsupportedOperationException(
                    "Encounterd unexpected IOException. " + e.getMessage());
        }

        return attributeMap;
    }

    private class InternalCellConstraints {
        int startCol;

        int startRow;

        int endCol;

        int endRow;

        String align;

        public InternalCellConstraints(int x, int y, int w, int h, String align) {
            startCol = x;
            startRow = y;
            endCol = x + w - 1;
            endRow = y + h - 1;
            this.align = align;
        }

        public CellConstraints getCellConstraints() {
            return new CellConstraints().xywh(startCol, startRow, endCol
                    - startCol + 1, endRow - startRow + 1, align);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Layout Fun");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JComponent panel = buildPanel();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.show();
    }

    private static JComponent buildPanel() {

        TablePanelBuilder table = new TablePanelBuilder(new FormDebugPanel(
                true, false));
        table
            .row()
                .separator("General 1")
            .row()
                .cell(new JLabel("Company"),"colSpec=right:pref colGrId=labels")
                .labelGapCol()
                .cell(new JFormattedTextField())
            .row()
                .cell(new JLabel("Contact"))
                .cell(new JFormattedTextField())
                .unrelatedGapRow()
                .separator("Propeller")
            .row()
                .cell(new JLabel("PTI [kW]"))
                .cell(new JFormattedTextField())
                .unrelatedGapCol()
                .cell(new JLabel("Description"), "colSpec=right:pref colGrId=labels")
                .labelGapCol()
                .cell(new JScrollPane(new JTextArea()), "rowspan=3")
            .row()
                .cell(new JLabel("R [mm]"))
                .cell(new JFormattedTextField())
                .cell()
            .row()
                .cell(new JLabel("D [mm]"))
                .cell(new JFormattedTextField())
                .cell();

        return table.getPanel();
    }
}