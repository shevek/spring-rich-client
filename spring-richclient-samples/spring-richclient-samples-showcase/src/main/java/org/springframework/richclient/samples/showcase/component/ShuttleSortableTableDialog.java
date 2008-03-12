package org.springframework.richclient.samples.showcase.component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.table.ShuttleSortableTableModel;
import org.springframework.richclient.table.SortTableCommand;
import org.springframework.richclient.table.TableSortIndicator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/**
 * This sample shows a {@link ShuttleSortableTableModel} being used. The
 * adding/removing of columns is possible, but note that the commands doing
 * these actions call the {@link ShuttleSortableTableModel#resetComparators()}
 * to have the correct number of comparators added/removed. A second method
 * exists to allow direct injection of your custom comparators (
 * {@link ShuttleSortableTableModel#resetComparators(java.util.Map)}). The
 * adding a specific headerRenderer with an arrow icon is done by the
 * {@link TableSortIndicator} and the actual sort action is encapsulated by the
 * {@link SortTableCommand}.
 *
 * @author Jan Hoskens
 *
 */
public class ShuttleSortableTableDialog extends TitledApplicationDialog {

	private List<Customer> customerData = new ArrayList<Customer>();

	private List<String> headers = new ArrayList<String>(4);

	private JTable table;

	private List<TableColumn> tableColumns = new ArrayList<TableColumn>(4);

	private DefaultTableColumnModel columnModel;

	private ShuttleSortableTableModel sortedModel;

	/**
	 * Initialise all needed data.
	 */
	public ShuttleSortableTableDialog() {
		customerData.add(new Customer("Jan", "Janssen", "Some dude", 4));
		customerData.add(new Customer("Peter", "Petersen", "Another dude", 6));
		customerData.add(new Customer("An", "Jaspers", "Dudette", 9));
		customerData.add(new Customer("Veronique", "DeCock", "Some dudette", 7));
		customerData.add(new Customer("Ann", "Van Elsen", "Another Dudette", 9));

		headers.add("firstName");
		headers.add("lastName");
		headers.add("comment");
		headers.add("xfactor");
	}

	@Override
	protected JComponent createTitledDialogContentPane() {
		JPanel panel = new JPanel(new FormLayout(new ColumnSpec[] {
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, ColumnSpec.DEFAULT_GROW),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, ColumnSpec.DEFAULT_GROW) }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));
		// create all columns, needed to add/remove them
		TableColumn column;
		for (int i = 0; i < headers.size(); ++i) {
			column = new TableColumn(i, -1, new DefaultTableCellRenderer(), null);
			column.setHeaderValue(headers.get(i));
			tableColumns.add(column);
		}
		columnModel = new DefaultTableColumnModel();
		// create the sortable model by wrapping our simple custom model
		sortedModel = new ShuttleSortableTableModel(new CustomerTableModel());
		table = new JTable(sortedModel, columnModel);
		table.setAutoCreateColumnsFromModel(false);
		TableSortIndicator sortIndicator = new TableSortIndicator(table);
		new SortTableCommand(table, sortIndicator.getColumnSortList());
		JScrollPane scrollPane = new JScrollPane(table);
		CellConstraints cc = new CellConstraints();
		panel.add(scrollPane, cc.xyw(1, 1, 3));
		panel.add(createAddCommand().createButton(), cc.xy(1, 3));
		panel.add(createRemoveCommand().createButton(), cc.xy(3, 3));

		return panel;
	}

	/**
	 * Returns an actionCommand that adds a column if possible. Maximum number
	 * of rows is the number of headers as defined in constructor.
	 */
	private ActionCommand createAddCommand() {
		ActionCommand addCommand = new ActionCommand("addCommand") {

			@Override
			protected void doExecuteCommand() {
				int columnCount = columnModel.getColumnCount();
				if (columnCount < (headers.size())) {
					columnModel.addColumn(tableColumns.get(columnCount));
					sortedModel.resetComparators();
				}
			}

		};
		CommandConfigurer commandConfigurer = (CommandConfigurer) ApplicationServicesLocator.services().getService(
				CommandConfigurer.class);
		commandConfigurer.configure(addCommand);
		return addCommand;
	}

	/**
	 * Returns an actionCommand removing a column.
	 */
	private ActionCommand createRemoveCommand() {
		ActionCommand removeCommand = new ActionCommand("removeCommand") {

			@Override
			protected void doExecuteCommand() {
				int columnCount = columnModel.getColumnCount();
				if (columnCount > 0) {
					columnModel.removeColumn(tableColumns.get(columnCount - 1));
					sortedModel.resetComparators();
				}
			}

		};
		CommandConfigurer commandConfigurer = (CommandConfigurer) ApplicationServicesLocator.services().getService(
				CommandConfigurer.class);
		commandConfigurer.configure(removeCommand);
		return removeCommand;
	}

	@Override
	protected boolean onFinish() {
		return true;
	}

	/**
	 * A very simplistic/minimalistic TableModel.
	 */
	private class CustomerTableModel extends AbstractTableModel {

		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		public int getRowCount() {
			return customerData.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Customer customer = customerData.get(rowIndex);
			if (columnIndex == 0)
				return customer.getFirstName();
			if (columnIndex == 1)
				return customer.getLastName();
			if (columnIndex == 2)
				return customer.getComment();
			if (columnIndex == 3)
				return customer.getXfactor();
			return null;
		}

	}

	/**
	 * Simple data container.
	 */
	private class Customer {
		private String firstName;

		private String lastName;

		private String comment;

		private int xfactor;

		public Customer() {
		}

		public Customer(String firstName, String lastName, String comment, int xfactor) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.comment = comment;
			this.xfactor = xfactor;
		}

		public int getXfactor() {
			return xfactor;
		}

		public void setXfactor(int xfactor) {
			this.xfactor = xfactor;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

	}
}