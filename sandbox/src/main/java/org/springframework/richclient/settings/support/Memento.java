package org.springframework.richclient.settings.support;

import org.springframework.richclient.settings.Settings;

public interface Memento {
	void saveState(Settings settings);

	void restoreState(Settings settings);
}
