package org.springframework.richclient.factory.jide;

import com.jidesoft.swing.ButtonStyle;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideToggleButton;
import org.springframework.richclient.factory.DefaultButtonFactory;

import javax.swing.*;

/**
 * Extension of the default button factory to replace the JButton by
 * the JideButton and the JToggleButton by JideToggleButton.
 * 
 * @author Jonny Wray
 *
 */
public class JideButtonFactory extends DefaultButtonFactory
{

	private int buttonStyle = ButtonStyle.TOOLBAR_STYLE;
	
	/**
	 * Sets the style to be used by the JideButton instances. Should
	 * be one of the values of com.jidesoft.swing.ButtonStyle
	 * 
	 * @param style
	 */
	public void setButtonStyle(int style){
		this.buttonStyle = style;
	}
	
	public AbstractButton createButton() {
		JideButton button = new JideButton();
		button.setButtonStyle(buttonStyle);
		return button;
	}

	public AbstractButton createToggleButton() {
		JideToggleButton button = new JideToggleButton();
		return button;
	}

}
