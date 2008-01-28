/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.application.splash;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.BorderFactory;

import org.springframework.richclient.progress.ProgressMonitor;

/**
 * MacOSX style splash screen inspired by a blog post by <a
 * href="http://jroller.com/page/gfx?entry=wait_with_style_in_swing">Romain Guy</a>.
 * 
 * @author Peter De Bruycker
 */
public class MacOSXSplashScreen extends AbstractSplashScreen implements MonitoringSplashScreen {
	private InfiniteProgressPanel progressPanel = new InfiniteProgressPanel();;

	protected Component createContentPane() {
		progressPanel.setPreferredSize(new Dimension(400, 250));
		progressPanel.shield = 0.5f;
		progressPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		progressPanel.setBackground(takeScreenshot());

		return progressPanel;
	}

	private Image takeScreenshot() {
		// take a screenshot
		try {
			Robot robot = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension dim = tk.getScreenSize();
			return robot.createScreenCapture(new Rectangle(0, 0, dim.width, dim.height));
		}
		catch (AWTException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ProgressMonitor getProgressMonitor() {
		return new InfiniteProgressPanelProgressMonitor(progressPanel);
	}
}
