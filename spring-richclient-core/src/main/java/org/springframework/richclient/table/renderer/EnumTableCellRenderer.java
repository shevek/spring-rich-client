package org.springframework.richclient.table.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * TableCellRenderer which looks up messages by using the class and name of the
 * Enum as a messageKey.
 * 
 * @author Xavier Breton
 */
public class EnumTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1737388077316919179L;

	private MessageSourceAccessor messageSourceAccessor;

	/**
	 * Constructor.
	 * 
	 * @param messageSourceAccesor containing the messages for the Enums.
	 */
	public EnumTableCellRenderer(MessageSourceAccessor messageSourceAccesor) {
		this.messageSourceAccessor = messageSourceAccesor;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( "unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (value instanceof Enum) {
			value = messageSourceAccessor.getMessage(value.getClass().getName() + "." + ((Enum) value).name());
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
