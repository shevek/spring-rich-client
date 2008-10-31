package org.springframework.richclient.table.renderer;

import java.awt.Component;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import junit.framework.TestCase;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;

/**
 * @author Xavier Breton
 */
public class EnumTableCellRendererTests extends TestCase {
    
	public enum Numbers {
	    
		ONE, 
		TWO, 
		THREE,
		FOUR, 
		FIVE, 
		SIX, 
		SEVEN, 
		EIGHT, 
		NINE, 
		TEN;
		
	}
	
	private EnumTableCellRenderer tetcr;
	
	public void testGetTableCellRendererComponent() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.ONE", Locale.getDefault(), "one");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.TWO", Locale.getDefault(), "two");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.THREE", Locale.getDefault(), "three");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.FOUR", Locale.getDefault(), "four");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.FIVE", Locale.getDefault(), "five");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.SIX", Locale.getDefault(), "six");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.SEVEN", Locale.getDefault(), "seven");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.EIGHT", Locale.getDefault(), "eight");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.NINE", Locale.getDefault(), "nine");
		messageSource.addMessage("org.springframework.richclient.table.renderer.EnumTableCellRendererTests$Numbers.TEN", Locale.getDefault(), "ten");
		MessageSourceAccessor msa = new MessageSourceAccessor(messageSource);
		tetcr = new EnumTableCellRenderer(msa);
		Object[][] rowData = { 
				{ Numbers.ONE },
				{ Numbers.TWO },
				{ Numbers.THREE },
				{ Numbers.FOUR },
				{ Numbers.FIVE },
				{ Numbers.SIX },
				{ Numbers.SEVEN },
				{ Numbers.EIGHT },
				{ Numbers.NINE },
				{ Numbers.TEN }
				};
		Object[] columnNames = { "Numbers" };
		JTable table = new JTable(rowData, columnNames);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setCellRenderer(tetcr);
		tetcr.getTableCellRendererComponent(table, Numbers.SEVEN, false, false, 6, 0);
		Component component = tetcr.getTableCellRendererComponent(table, Numbers.SEVEN, false, false, 6, 0);
		assertTrue(component instanceof EnumTableCellRenderer);
	}
}