package org.springframework.richclient.selection.dialog;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.text.TextComponentPopup;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

public class FilterListSelectionDialog extends ListSelectionDialog {

	private TextFilterator filterator;

	private FilterList filterList;

	public void setFilterator(TextFilterator filterator) {
		this.filterator = filterator;
	}

	public FilterListSelectionDialog(String title, Window parent, FilterList filterList) {
		super(title, parent, filterList);
		this.filterList = filterList;
	}

	protected JComponent createDialogContentPane() {
		TableLayoutBuilder builder = new TableLayoutBuilder();

		JComponent filterComponent = createFilterComponent();
		builder.cell(filterComponent);
		builder.row();
		builder.relatedGapRow();
		builder.cell(super.createDialogContentPane());

		return builder.getPanel();
	}

	protected JComponent createFilterComponent() {
		JTextField filter = new JTextField();

		filterList.setMatcherEditor(new TextComponentMatcherEditor(filter, filterator));

		TextComponentPopup.attachPopup(filter);
		filter.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					// transfer focus to list
					getList().requestFocusInWindow();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (getFinishCommand().isEnabled())
						getFinishCommand().execute();
				}
			}
		});

		return filter;
	}
}
