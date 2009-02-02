package org.springframework.richclient.samples.dataeditor.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationLauncher;

public class DataEditorApp
{
    private static final Log logger = LogFactory.getLog(DataEditorApp.class);

	public static void main(String[] args) {
		logger.info("Data editor sample starting up");

		String rootContextDirectoryClassPath = "/org/springframework/richclient/samples/dataeditor/ctx";

		String startupContextPath = rootContextDirectoryClassPath + "/startup.xml";

		String richclientApplicationContextPath = rootContextDirectoryClassPath + "/appbundle.xml";

		try {
			new ApplicationLauncher(startupContextPath, new String[] { richclientApplicationContextPath });
		}
		catch (RuntimeException e) {
			logger.error("RuntimeException during startup", e);
		}
	}
}
