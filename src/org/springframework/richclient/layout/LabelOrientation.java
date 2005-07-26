package org.springframework.richclient.layout;

import javax.swing.SwingConstants;

import org.springframework.core.enums.ShortCodedLabeledEnum;

public final class LabelOrientation extends ShortCodedLabeledEnum {
	public static final LabelOrientation TOP = new LabelOrientation(SwingConstants.TOP, "Top");

	public static final LabelOrientation BOTTOM = new LabelOrientation(SwingConstants.BOTTOM, "Bottom");

	public static final LabelOrientation LEFT = new LabelOrientation(SwingConstants.LEFT, "Left");

	public static final LabelOrientation RIGHT = new LabelOrientation(SwingConstants.RIGHT, "Right");

	private LabelOrientation(int code, String label) {
		super(code, label);
	}
}