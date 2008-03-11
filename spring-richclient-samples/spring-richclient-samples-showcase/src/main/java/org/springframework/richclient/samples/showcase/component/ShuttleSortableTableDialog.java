package org.springframework.richclient.samples.showcase.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.table.ShuttleSortableTableModel;
import org.springframework.richclient.table.SortTableCommand;
import org.springframework.richclient.table.TableSortIndicator;
import org.springframework.richclient.table.TableUtils;

public class ShuttleSortableTableDialog extends TitledApplicationDialog {

	private List<Customer> customerData = new ArrayList<Customer>();

	private List<String> headers = new ArrayList<String>(4);

	private JTable table;

	private List<TableColumn> tableColumns = new ArrayList<TableColumn>(4);

	private DefaultTableColumnModel columnModel;

	private ShuttleSortableTableModel sortedModel;

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
		JPanel panel = new JPanel();
		TableColumn column;
		for (int i = 0; i < headers.size(); ++i) {
			column = new TableColumn(i, -1, new DefaultTableCellRenderer(), null);
			column.setHeaderValue(headers.get(i));
			tableColumns.add(column);
		}
		columnModel = new DefaultTableColumnModel();
		sortedModel = new ShuttleSortableTableModel(new CustomerTableModel());
		table = new JTable(sortedModel, columnModel);
		table.setAutoCreateColumnsFromModel(false);
		TableSortIndicator sortIndicator = new TableSortIndicator(table);
		new SortTableCommand(table, sortIndicator.getColumnSortList());
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
		panel.add(createAddCommand().createButton());
		panel.add(createRemoveCommand().createButton());

		return panel;
	}

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

	private ActionCommand createRemoveCommand() {
		ActionCommand removeCommand = new ActionCommand("removeCommand") {

			@Override
			protected void doExecuteCommand() {
				int columnCount = columnModel.getColumnCount();
				if (columnCount > 1) {
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