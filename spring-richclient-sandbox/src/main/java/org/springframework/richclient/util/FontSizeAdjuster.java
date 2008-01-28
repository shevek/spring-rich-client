package org.springframework.richclient.util;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontSizeAdjuster {
	private static final AWTEventListener listener = new AWTEventListener() {
		public void eventDispatched(AWTEvent e) {
			MouseWheelEvent event = (MouseWheelEvent) e;
			if (event.isControlDown()) {
				adjust(event.getWheelRotation() < 0 ? -1 : 1);
			}
		}
	};

	private static int adjustment = 0;

	private FontSizeAdjuster() {
		// static class only
	}

	public static void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(listener,
				AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	public static void dispose() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
	}

	public static void adjust(int amount) {
		Object[] objs = UIManager.getLookAndFeel().getDefaults().keySet()
				.toArray();
		for (int i = 0; i < objs.length; i++) {
			if (objs[i].toString().toUpperCase().indexOf("FONT") != -1) {
				Font font = UIManager.getFont(objs[i]);
				if (font != null) {
					UIManager.put(objs[i], new FontUIResource(font
							.deriveFont((float)font.getSize() + amount)));
				}
			}
		}

		adjustment = adjustment + amount;

		updateUI();
	}

	public static int getAdjustment() {
		return adjustment;
	}

	private static void updateUI() {
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			update(frames[i]);
		}
	}

	private static void update(Window window) {
		SwingUtilities.updateComponentTreeUI(window);
		window.pack();

		// update the owned windows and dialogs
		Window[] ownedWindows = window.getOwnedWindows();
		for (int i = 0; i < ownedWindows.length; i++) {
			Window childWindow = ownedWindows[i];
			update(childWindow);
		}
	}
}
