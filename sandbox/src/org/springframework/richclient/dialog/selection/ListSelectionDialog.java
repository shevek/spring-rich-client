package org.springframework.richclient.dialog.selection;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.FilteredListModel;
import org.springframework.richclient.list.ListListModel;
import org.springframework.richclient.text.TextComponentPopup;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.closure.Constraint;

/**
 * This class is not yet finished, there is still some work to do here...
 * A <code>ListSelectionDialog</code> can be used to select an item from a list.
 * <br/>
 * It has optional support for a filter field.
 * @author peter.de.bruycker
 */
public abstract class ListSelectionDialog extends ApplicationDialog {

    private JTextField filterField;

    private SelectionFilterConstraint constraint;

    private SelectionFilter filter;

    private String description;

    private ListListModel model;

    private ListCellRenderer renderer;

    private JList list;

    private List items;

    public ListSelectionDialog(String title, Window parent, List items) {
        this(title, parent, items, null);
    }

    public ListSelectionDialog(String title, Window parent, List items, SelectionFilter filter) {
        super(title, parent);
        setFilter(filter);
        this.items = items;
        setDescription("&Select an item");
    }

    /**
     * Method setFilter.
     * @param filter
     */
    private void setFilter(SelectionFilter filter) {
        this.filter = filter;
        if (filter != null) {
            constraint = new SelectionFilterConstraint(filter);
        }
        else {
            constraint = null;
        }

    }

    public void setDescription(String desc) {
        Assert.isTrue(!isControlCreated(), "Set the description before the control is created.");

        description = desc;
    }

    public void setRenderer(ListCellRenderer renderer) {
        Assert.notNull(renderer, "Renderer cannot be null.");
        Assert.isTrue(!isControlCreated(), "Install the renderer before the control is created.");

        this.renderer = renderer;
    }

    private void setListModel() {
        model = new ListListModel(items);
        if (constraint != null) {
            list.setModel(new FilteredListModel(model, constraint));
        }
        else {
            list.setModel(model);
        }
    }

    private static class SelectionFilterConstraint extends Observable implements Constraint, DocumentListener {
        private String text;

        private SelectionFilter filter;

        /** 
         * @see org.springframework.util.closure.Constraint#test(java.lang.Object)
         */
        public boolean test(Object obj) {
            return filter.accept(obj, text);
        }

        /**
         * Constructs a new <code>ConstraintAdapter</code> instance.
         * @param filter
         */
        public SelectionFilterConstraint(SelectionFilter filter) {
            this.filter = filter;
        }

        /** 
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e) {
            // not used
        }

        /** 
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e) {
            contentChanged(e.getDocument());
        }

        private void contentChanged(Document doc) {
            try {
                text = doc.getText(0, doc.getLength());
            }
            catch (BadLocationException e) {
                // if this happens, somethings really wrong
                throw new RuntimeException(e);
            }
            setChanged();
            notifyObservers();
        }

        /** 
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e) {
            contentChanged(e.getDocument());
        }
    }

    /** 
     * @see org.springframework.richclient.dialog.ApplicationDialog#createDialogContentPane()
     */
    protected JComponent createDialogContentPane() {
        createListControl();
        createFilterControl();

        setListModel();

        setFinishEnabled(false);

        if (!model.isEmpty()) {
            list.setSelectedIndex(0);
        }

        TableLayoutBuilder builder = new TableLayoutBuilder();

        if (StringUtils.hasText(description)) {
            if (filterField != null) {
                builder.cell(getComponentFactory().createLabelFor(description, filterField));
            }
            else {
                builder.cell(getComponentFactory().createLabelFor(description, list));
            }
            builder.row();
        }

        builder.cell(filterField);
        builder.row();

        builder.cell(new JScrollPane(list));

        return builder.getPanel();
    }

    private void createFilterControl() {
        filterField = getComponentFactory().createTextField();
        TextComponentPopup.attachPopup(filterField);
        filterField.getDocument().addDocumentListener(constraint);
        filterField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // transfer focus to list
                    list.requestFocusInWindow();
                }
            }
        });
    }

    private void createListControl() {
        list = new JList();

        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {

            private int lastIndex = -1;

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (list.getSelectionModel().isSelectionEmpty() && lastIndex > -1) {
                    list.setSelectedIndex(lastIndex);
                    return;
                }

                setFinishEnabled(!list.getSelectionModel().isSelectionEmpty());
                lastIndex = list.getSelectedIndex();
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    getFinishCommand().execute();
                }
            }
        });

        if (renderer != null) {
            list.setCellRenderer(renderer);
        }
    }

    /** 
     * @see org.springframework.richclient.dialog.ApplicationDialog#onFinish()
     */
    protected boolean onFinish() {
        onSelect(getSelectedObject());
        return true;
    }

    private Object getSelectedObject() {
        return model.get(list.getSelectedIndex());
    }

    protected abstract void onSelect(Object selection);
}