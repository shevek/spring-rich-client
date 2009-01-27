package org.springframework.richclient.widget.table;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.springframework.richclient.util.RcpSupport;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.*;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Voorziet een paar eenvoudige renderers voor gebruiksgemak.
 */
public class TableCellRenderers
{

    /**
     * Zorgt er voor dat er horizontaal centraal gealigneerd wordt. Als het gaat om een Date, dan zal deze ook
     * geformatteerd worden.
     */
    public static final TableCellRenderer CENTER_ALIGNED_RENDERER = new AlignedRenderer(SwingConstants.CENTER);
    /**
     * Zorgt er voor dat er horizontaal rechts gealigneerd wordt. Als het gaat om een Date, dan zal deze ook
     * geformatteerd worden.
     */
    public static final TableCellRenderer RIGHT_ALIGNED_RENDERER = new AlignedRenderer(SwingConstants.RIGHT);
    /**
     * Zorgt er voor dat er horizontaal links gealigneerd en verticaal boven gealigneerd wordt. Als het gaat
     * om een Date, dan zal deze ook geformatteerd worden.
     */
    public static final TableCellRenderer TOP_ALIGNED_RENDERER = new AlignedRenderer(SwingConstants.LEFT,
            SwingConstants.TOP);
    /**
     * Zorgt er voor dat er horizontaal links gealigneerd en verticaal onderaan gealigneerd wordt. Als het
     * gaat om een Date, dan zal deze ook geformatteerd worden.
     */
    public static final TableCellRenderer BOTTOM_ALIGNED_RENDERER = new AlignedRenderer(SwingConstants.LEFT,
            SwingConstants.BOTTOM);
    public static final TableCellRenderer PERCENTAGE_RENDERER = new PercentageRenderer();
    public static final TableCellRenderer MONEY_RENDERER = new BigDecimalRenderer(NumberFormat
            .getCurrencyInstance(Locale.getDefault()));
    public static final TableCellRenderer LEFT_ALIGNED_HEADER_RENDERER = new AlignedTableHeaderRenderer(
            SwingConstants.LEFT);
    public static final TableCellRenderer CENTER_ALIGNED_HEADER_RENDERER = new AlignedTableHeaderRenderer(
            SwingConstants.CENTER);
    public static final TableCellRenderer RIGHT_ALIGNED_HEADER_RENDERER = new AlignedTableHeaderRenderer(
            SwingConstants.RIGHT);
    public static final TableCellRenderer ENUM_RENDERER = new EnumTableCellRenderer();

    /**
     * Zorgt er voor dat er horizontaal links gealigneerd en dat er geen thousendseperator gebruikt wordt.
     * TODO Dit werd enkel getetst op Integers, moet nog getest worden op doubles.
     */
    public static final TableCellRenderer FLAT_NUMBER_RENDERER = new FlatNumberRenderer();

    public static class FlatNumberRenderer extends DefaultTableRenderer
    {

        private static NumberFormat format = NumberFormat.getIntegerInstance();
        static
        {
            format.setGroupingUsed(false);
        }

        public FlatNumberRenderer()
        {
            super(new FormatStringValue(format));
        }
    }

    /**
     * Simpele alignatie renderer gebruikt SwingConstants om horizontal alignment en vertical alignment te
     * voorzien. Als het gaat om een Date, dan zal deze ook geformatteerd worden.
     */
    public static class AlignedRenderer extends DefaultTableCellRenderer
    {

        /**
         * Constructor.
         *
         * @param horizontalAlignment
         *            E\u00e9n van de SwingConstants LEFT/CENTER of RIGHT
         * @see SwingConstants
         */
        public AlignedRenderer(int horizontalAlignment)
        {
            super();
            setHorizontalAlignment(horizontalAlignment);
        }

        /**
         * Constructor.
         *
         * @param horizontalAlignment
         *            E\u00e9n van de SwingConstants LEFT/CENTER of RIGHT
         * @param verticalAlignment
         *            E\u00e9n van de SwingConstants TOP/CENTER of BOTTOM
         * @see SwingConstants
         */
        public AlignedRenderer(int horizontalAlignment, int verticalAlignment)
        {
            super();
            setHorizontalAlignment(horizontalAlignment);
            setVerticalAlignment(verticalAlignment);
        }

        DateFormat formatter;

        @Override
        public void setValue(Object value)
        {
            if (value != null && value instanceof Date)
            {
                if (formatter == null)
                {
                    formatter = DateFormat.getDateInstance();
                }
                setText(formatter.format(value));
            }
            else
            {
                super.setValue(value);
            }
        }

    }

    public static class PercentageRenderer extends DefaultTableCellRenderer
    {

        private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        private static final Format nonFractionalFormat = new DecimalFormat("###     %", symbols);
        private static final Format fractionalFormat = new DecimalFormat("##0.00%", symbols);
        private static final BigDecimal multiplyFactor = new BigDecimal("100");

        public PercentageRenderer()
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value)
        {
            if (value instanceof BigDecimal)
            {
                BigDecimal percentage = ((BigDecimal) value).multiply(multiplyFactor);
                if (percentage.doubleValue() == percentage.intValue())
                {
                    super.setValue(nonFractionalFormat.format(value));
                }
                else
                {
                    super.setValue(fractionalFormat.format(value));
                }
            }
            else
            {
                super.setValue(value);
            }
        }
    }

    /**
     * Renderer die gebruikt kan worden om BigDecimals te vermenigvuldigen alvorens te tonen (zoals voor
     * percentages) en tegelijkertijd een horizontal alignment kan voorzien.
     */
    public static class BigDecimalRenderer extends DefaultTableCellRenderer
    {

        private final BigDecimal multiplyFactor;
        private final Format format;

        public BigDecimalRenderer(Format format)
        {
            this(null, format);
        }

        /**
         * Constructor.
         *
         * @param multiplyFactor
         *            Vermenigvuldig elke waarde met deze factor alvorens te tonen.
         */
        public BigDecimalRenderer(BigDecimal multiplyFactor)
        {
            this(multiplyFactor, NumberFormat.getNumberInstance());
        }

        public BigDecimalRenderer(BigDecimal multiplyFactor, Format format)
        {
            this(multiplyFactor, format, SwingConstants.RIGHT);
        }

        /**
         * Constructor.
         *
         * @param multiplyFactor
         *            Vermenigvuldig elke waarde met deze factor alvorens te tonen.
         * @param horizontalAlignment
         *            Horizontal alignment via SwingConstants
         */
        public BigDecimalRenderer(BigDecimal multiplyFactor, Format format, int horizontalAlignment)
        {
            this.multiplyFactor = multiplyFactor;
            this.format = format;
            setHorizontalAlignment(horizontalAlignment);
        }

        @Override
        protected void setValue(Object value)
        {
            if (value instanceof BigDecimal)
            {
                if (multiplyFactor != null)
                {
                    value = ((BigDecimal) value).multiply(multiplyFactor);
                }
                super.setValue(format.format(value));
            }
            else
            {
                super.setValue(value);
            }
        }
    }

    public static class AlignedTableHeaderRenderer extends DefaultTableCellRenderer
    {

        private int align = SwingConstants.CENTER;

        public AlignedTableHeaderRenderer(int align)
        {
            this.align = align;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            if (table != null)
            {
                JTableHeader header = table.getTableHeader();
                if (header != null)
                {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }

            setText((value == null) ? "" : value.toString() + " ");
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(align);
            return this;
        }
    }

    /**
     * Renderer die enum waarden kan mappen naar strings in je messages.properties. Gebruik de fully qualified
     * name voor de enumwaarde in je messages. <br/> Bijv.:<br/> be.schaubroeck.MyEnum.MYVALUE wordt in je
     * properties file gemapt als be.schaubroeck.MyEnum.MYVALUE = Mijn waarde
     */
    public static class EnumTableCellRenderer extends DefaultTableCellRenderer
    {

        public EnumTableCellRenderer()
        {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value == null)
            {
                setValue("");
                setIcon(null);
            }
            else
            {
                if (value instanceof Enum)
                {
                    Enum valueEnum = (Enum) value;
                    Class<? extends Enum> valueClass = valueEnum.getClass();
                    // juiste description uit messages.properties proberen halen
                    setValue(RcpSupport.getMessage(valueClass.getName() + "." + valueEnum.name()));
                    setIcon(RcpSupport.getIcon(valueClass.getName() + "." + valueEnum.name()));
                }
                else
                {
                    setValue(value);
                }
            }
            return this;
        }
    }

    /**
     * Renderer die een specifieke property van een lijst van entiteiten weergeeft als een lijst binnen één
     * cel.
     */
    public static class ListPropertyCellRenderer extends JPanel implements TableCellRenderer
    {

        protected String property;

        protected int verticalAlignment;

        protected int horizontalAlignment;

        protected Format format;

        protected float alignmentX;

        private static Border border = new EmptyBorder(1, 2, 1, 2);

        /**
         * Constructor
         *
         * @param property
         *            De weer te geven property
         */
        public ListPropertyCellRenderer(String property)
        {
            this(property, SwingConstants.LEFT, SwingConstants.CENTER);
        }

        /**
         * Constructor
         *
         * @param property
         *            De weer te geven property
         * @param horizontalAlignment
         *            Horizontal alignment via SwingConstants
         * @param verticalAlignment
         *            Vertical alignment via SwingConstants
         */
        public ListPropertyCellRenderer(String property, int horizontalAlignment, int verticalAlignment)
        {
            this(property, horizontalAlignment, verticalAlignment, null);
        }

        /**
         * Constructor
         *
         * @param property
         *            De weer te geven property
         * @param horizontalAlignment
         *            Horizontal alignment via SwingConstants
         * @param verticalAlignment
         *            Vertical alignment via SwingConstants
         * @param format
         *            De toe te passen formattering alvorens de property weer te geven
         */
        public ListPropertyCellRenderer(String property, int horizontalAlignment, int verticalAlignment,
                Format format)
        {
            this.property = property;
            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
            this.format = format;
            switch (horizontalAlignment)
            {
                case SwingConstants.LEFT :
                    alignmentX = (float) 0.0;
                    break;

                case SwingConstants.CENTER :
                    alignmentX = (float) 0.5;
                    break;

                case SwingConstants.RIGHT :
                    alignmentX = (float) 1.0;
                    break;

                default :
                    throw new IllegalArgumentException("Illegal horizontal alignment value");
            }

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(true);
            setBorder(border);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            removeAll();
            invalidate();

            Color fg = table.getForeground();
            Color bg = table.getBackground();

            if (isSelected)
            {
                fg = table.getSelectionForeground();
                bg = table.getSelectionBackground();
            }

            Font font = table.getFont();

            setFont(font);

            if (hasFocus)
            {
                Border border = null;
                if (isSelected)
                {
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                }
                if (border == null)
                {
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
                }
                setBorder(border);

                if (!isSelected && table.isCellEditable(row, column))
                {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null)
                    {
                        fg = col;
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null)
                    {
                        bg = col;
                    }
                }
            }
            else
            {
                setBorder(border);
            }

            super.setForeground(fg);
            super.setBackground(bg);

            if (verticalAlignment != SwingConstants.TOP)
            {
                add(Box.createVerticalGlue());
            }

            Object[] values;
            if (value instanceof Collection)
            {
                values = ((Collection) value).toArray();
            }
            else
                throw new IllegalArgumentException("Value must be an instance of Collection.");

            for (int i = 0; i < values.length; i++)
            {
                Object o = values[i];
                Object line;
                try
                {
                    line = PropertyUtils.getProperty(o, property);
                }
                catch (NestedNullException e)
                {
                    line = null;
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error reading property " + property + " from object " + o, e);
                }
                JLabel lineLabel = new JLabel();
                lineLabel.setForeground(fg);
                lineLabel.setFont(font);
                setValue(lineLabel, line, i);
                add(lineLabel);
            }

            int height_wanted = (int) getPreferredSize().getHeight();
            if (height_wanted > table.getRowHeight(row))
                table.setRowHeight(row, height_wanted);

            if (verticalAlignment != SwingConstants.BOTTOM)
            {
                add(Box.createVerticalGlue());
            }
            return this;
        }

        protected void setValue(JLabel l, Object value, int lineNumber)
        {
            if (format != null && value != null)
                value = format.format(value);
            l.setText(value == null ? " " : value.toString());
            l.setHorizontalAlignment(horizontalAlignment);
            l.setAlignmentX(alignmentX);
            l.setOpaque(false);
        }

    }
}

