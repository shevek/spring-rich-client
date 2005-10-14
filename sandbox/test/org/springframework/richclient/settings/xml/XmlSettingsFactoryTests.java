package org.springframework.richclient.settings.xml;

import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsException;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class XmlSettingsFactoryTests extends TestCase {
	public void testGetAndSetLocation() {
		XmlSettingsFactory settingsFactory = new XmlSettingsFactory();

		assertEquals("default settings location is \"settings\"", "settings", settingsFactory.getLocation());

		settingsFactory.setLocation("other-settings");
		assertEquals("other-settings", settingsFactory.getLocation());

		settingsFactory.setLocation(null);
		assertEquals("location not reset to default", "settings", settingsFactory.getLocation());
	}

	public void testGetAndSetReaderWriter() {
		XmlSettingsFactory settingsFactory = new XmlSettingsFactory();
		settingsFactory.setLocation("other-settings");

		XmlSettingsReaderWriter readerWriter = settingsFactory.getReaderWriter();
		assertTrue("default must be FileSystemXmlSettingsReaderWriter",
				readerWriter instanceof FileSystemXmlSettingsReaderWriter);
		// test location
		FileSystemXmlSettingsReaderWriter fileSystemXmlSettingsReaderWriter = (FileSystemXmlSettingsReaderWriter) readerWriter;
		assertEquals("other-settings", fileSystemXmlSettingsReaderWriter.getLocation());

		StringXmlSettingsReaderWriter newReaderWriter = new StringXmlSettingsReaderWriter(null);
		settingsFactory.setReaderWriter(newReaderWriter);
		assertEquals(newReaderWriter, settingsFactory.getReaderWriter());

		settingsFactory.setReaderWriter(null);
		assertTrue("not reset to default",
				settingsFactory.getReaderWriter() instanceof FileSystemXmlSettingsReaderWriter);
	}

	public void testCreate() throws SettingsException {
		XmlSettingsFactory settingsFactory = new XmlSettingsFactory();
		settingsFactory.setReaderWriter(new StringXmlSettingsReaderWriter(null));

		Settings settings = settingsFactory.createSettings("user");
		assertNotNull(settings);
		assertTrue(settings instanceof RootXmlSettings);
		assertEquals("user", settings.getName());

		RootXmlSettings rootXmlSettings = (RootXmlSettings) settings;
		Document document = rootXmlSettings.getDocument();
		assertNotNull(document);
	}
}
