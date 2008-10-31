package org.springframework.richclient.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author peter.de.bruycker
 */
public class ListTableModelTests extends AbstractBaseTableModelTests {
    
    
    private final ListTableModel dummyListTableModel = new ListTableModel() {

        protected Class[] createColumnClasses() {
            return new Class[] { String.class };
        }

        protected String[] createColumnNames() {
            return new String[] { "column" };
        }
        
        
    };
    
    /**
     * {@inheritDoc}
     */
    protected BaseTableModel getBaseTableModel() {
        return this.dummyListTableModel;
    }

    /**
     * TestCase for bug #RCP-14
     */
    public void testConstructorThrowsNullPointerException() {
        try {
            ListTableModel model = new ListTableModel() {
                protected Class[] createColumnClasses() {
                    return new Class[] { String.class };
                }

                protected String[] createColumnNames() {
                    return new String[] { "column" };
                }
            };
            model.createColumnInfo();
            model.getColumnCount();
        }
        catch (NullPointerException e) {
            fail("Should not throw NullPointerException");
        }

        try {
            ListTableModel model = new ListTableModel(new ArrayList()) {
                protected Class[] createColumnClasses() {
                    return new Class[] { String.class };
                }

                protected String[] createColumnNames() {
                    return new String[] { "col0" };
                }
            };
            model.createColumnInfo();
            model.getColumnCount();
        }
        catch (NullPointerException e) {
            fail("Should not throw NullPointerException");
        }
    }

    public void testGetValueAtInternalWithOneColumnNoArray() {
        ListTableModel model = new ListTableModel() {
            protected Class[] createColumnClasses() {
                return new Class[] { String.class };
            }

            protected String[] createColumnNames() {
                return new String[] { "col0" };
            }
        };
        model.setRowNumbers(false);

        String row = "col0";

        assertEquals("col0", model.getValueAtInternal(row, 0));
    }

    public void testGetValueAtInternalWithArray() {
        ListTableModel model = new ListTableModel() {
            protected Class[] createColumnClasses() {
                return new Class[] { String.class, String.class };
            }

            protected String[] createColumnNames() {
                return new String[] { "col0", "col1" };
            }
        };
        model.setRowNumbers(false);

        String[] row = new String[] { "col0", "col1" };

        assertEquals("col0", model.getValueAtInternal(row, 0));
        assertEquals("col1", model.getValueAtInternal(row, 1));
    }

    public void testGetValueAtInternalWithInvalidObjectType() {
        // model with two columns, but no list or array as rows
        ListTableModel model = new ListTableModel() {
            protected Class[] createColumnClasses() {
                return new Class[] { String.class, String.class };
            }

            protected String[] createColumnNames() {
                return new String[] { "col0", "col1" };
            }
        };
        model.setRowNumbers(false);

        String row = "col0";

        try {
            model.getValueAtInternal(row, 0);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    private static void pass() {
        // test passes
    }

    public void testGetValueAtInternalWithList() {
        ListTableModel model = new ListTableModel() {
            protected Class[] createColumnClasses() {
                return new Class[] { String.class, String.class };
            }

            protected String[] createColumnNames() {
                return new String[] { "col0", "col1" };
            }
        };
        model.createColumnInfo();
        List row = Arrays.asList(new String[] { "col0", "col1" });
        assertEquals("col0", model.getValueAtInternal(row, 0));
        assertEquals("col1", model.getValueAtInternal(row, 1));
    }

    public void testGetValueAtInternalWithOneColumnAndArray() {
        ListTableModel model = new ListTableModel() {
            protected Class[] createColumnClasses() {
                return new Class[] { String.class };
            }

            protected String[] createColumnNames() {
                return new String[] { "col0" };
            }
        };
        model.setRowNumbers(false);

        String[] row = new String[] { "col0", "col1" };

        assertEquals("col0", model.getValueAtInternal(row, 0));
    }
}