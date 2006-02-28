package org.springframework.richclient.command.support;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import org.springframework.richclient.command.CommandGroupFactoryBean;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Size;

/**
 * Creates a buttonstack: a panel with buttons that are vertically positioned.
 * 
 * @see org.springframework.richclient.command.support.ButtonBarGroupContainerPopulator
 * @see com.jgoodies.forms.builder.ButtonStackBuilder
 * 
 * @author jh
 */
public class ButtonStackGroupContainerPopulator extends SimpleGroupContainerPopulator
{
    private Size minimumSize;

    private ButtonStackBuilder builder;

    private List buttons = new ArrayList();

    /**
     * Constructor. 
     */
    public ButtonStackGroupContainerPopulator() {
        super(new JPanel());
        builder = new ButtonStackBuilder((JPanel)getContainer());
    }

    /**
     * Define the minimum buttonsize of the buttonStack. 
     * 
     * @param minimumSize
     */
    public void setMinimumButtonSize(Size minimumSize) {
        this.minimumSize = minimumSize;
    }

    /**
     * @return the created ButtonStack panel
     */
    public JPanel getButtonStack() {
        return builder.getPanel();
    }

    /**
     * @see SimpleGroupContainerPopulator#add(Component)
     */
    public void add(Component c) {
        buttons.add(c);
    }

    /**
     * @see SimpleGroupContainerPopulator#addSeparator()
     */
    public void addSeparator() {
        buttons.add(CommandGroupFactoryBean.SEPARATOR_MEMBER_CODE);
    }

    /**
     * @see SimpleGroupContainerPopulator#onPopulated()
     */
    public void onPopulated() {
        builder.addGlue();
        int length = buttons.size();
        for (int i = 0; i < length; i++) {
            Object o = buttons.get(i);
            if (o instanceof String && o == CommandGroupFactoryBean.SEPARATOR_MEMBER_CODE) {
                builder.addUnrelatedGap();
            }
            else if (o instanceof AbstractButton) {
                AbstractButton button = (AbstractButton)o;
                if (minimumSize != null) {
                    addCustomGridded(button);
                }
                else {
                    builder.addGridded(button);
                }
                if (i < buttons.size() - 1) {
                    builder.addRelatedGap();
                }
            }
        }
    }

    /**
     * Handle the minimumSize by grouping the rows and defining a minimumSize on that row.
     * 
     * @param button
     */
    private void addCustomGridded(AbstractButton button) {
        builder.getLayout().appendRow(new RowSpec(minimumSize));
        builder.getLayout().addGroupedRow(builder.getRow());
        button.putClientProperty("jgoodies.isNarrow", Boolean.TRUE);
        builder.add(button);
        builder.nextRow();
    }

}
