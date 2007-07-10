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
public class TigerEnumTableCellRendererTests extends TestCase {
    
	public enum TigerEnum {
	    
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
	
	private TigerEnumTableCellRenderer tetcr;
	
	public void testGetTableCellRendererComponent() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.ONE", Locale.getDefault(), "one");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.TWO", Locale.getDefault(), "two");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.THREE", Locale.getDefault(), "three");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.FOUR", Locale.getDefault(), "four");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.FIVE", Locale.getDefault(), "five");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.SIX", Locale.getDefault(), "six");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.SEVEN", Locale.getDefault(), "seven");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.EIGHT", Locale.getDefault(), "eight");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.NINE", Locale.getDefault(), "nine");
		messageSource.addMessage("org.springframework.richclient.table.renderer.TigerEnumTableCellRendererTests$TigerEnum.TEN", Locale.getDefault(), "ten");
		MessageSourceAccessor msa = new MessageSourceAccessor(messageSource);
		tetcr = new TigerEnumTableCellRenderer(msa);
		Object[][] rowData = { 
				{ TigerEnum.ONE },
				{ TigerEnum.TWO },
				{ TigerEnum.THREE }, 
				{ TigerEnum.FOUR }, 
				{ TigerEnum.FIVE }, 
				{ TigerEnum.SIX }, 
				{ TigerEnum.SEVEN }, 
				{ TigerEnum.EIGHT }, 
				{ TigerEnum.NINE }, 
				{ TigerEnum.TEN }
				};
		Object[] columnNames = { "Numbers" };
		JTable table = new JTable(rowData, columnNames);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setCellRenderer(tetcr);
		tetcr.getTableCellRendererComponent(table, TigerEnum.SEVEN, false, false, 6, 0);
		Component component = tetcr.getTableCellRendererComponent(table, TigerEnum.SEVEN, false, false, 6, 0);
		assertTrue(component instanceof TigerEnumTableCellRenderer);
	}
}