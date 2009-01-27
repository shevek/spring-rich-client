package org.springframework.richclient.widget.table;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Comparator;


/**
 * TableDescription
 */
public interface TableDescription
{
    /**
     * @return Type van rijobjecten die in de tabel worden gezet. Gebruikt voor property access.
     */
    Class getDataType();

    /**
     * De properties die mee in de textFilter (indien gesupporteerd) moeten worden opgenomen.
     */
    String[] getPropertiesInTextFilter();

    /**
     * Datatype voor de betrokken kolom.
     */
    Class getType(int propertyIndex);

    /**
     * Kolom header.
     */
    String getHeader(int propertyIndex);

    /**
     * Waarde voor de kolom uit het meegeleverde (rij-) object.
     */
    Object getValue(Object rowObject, int propertyIndex);

    /**
     * Zet de waarde newValue in het rij-object.
     */
    void setValue(Object rowObject, int propertyIndex, Object newValue);

    /**
     * Maximumbreedte voor deze kolom.
     */
    int getMaxColumnWidth(int propertyIndex);

    /**
     * Minimumbreedte voor deze kolom.
     */
    int getMinColumnWidth(int propertyIndex);

    /**
     * Kolom resizable of niet.
     */
    boolean isResizable(int propertyIndex);

    /**
     * Geef de specifieke customrenderer voor deze kolom.
     */
    TableCellRenderer getColumnRenderer(int propertyIndex);

    /**
     * Geef de specifieke customeditor voor deze kolom.
     */
    TableCellEditor getColumnEditor(int propertyIndex);

    /**
     * Is deze kolom de selecteer kolom.
     */
    boolean isSelectColumn(int propertyIndex);

    /**
     * Geef de specifieke comparator voor deze kolom.
     */
    Comparator getColumnComparator(int propertyIndex);

    /**
     * Geef de defaultComparator terug, null in geval van originele volgorde.
     */
    Comparator getDefaultComparator();

    /**
     * Aantal kolommen.
     */
    int getColumnCount();

    /**
     * @return TRUE indien deze tabel een selectiekolom met editeerbare checkboxen heeft.
     */
    boolean hasSelectColumn();

    /**
     * Initieel zichtbaar of niet
     */
    boolean isVisible(int propertyIndex);

}
