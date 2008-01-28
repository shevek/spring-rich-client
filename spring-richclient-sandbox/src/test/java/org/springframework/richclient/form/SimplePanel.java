package org.springframework.richclient.form;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Simple Panel that mimics a panel created by a visual designer.
 * @author Peter De Bruycker
 */
public class SimplePanel extends JPanel {
    private JTextField stringField;
    private JComboBox comboBox;
    private JCheckBox checkBox;
    private JTextField nestedField;

    public SimplePanel() {
        TableLayoutBuilder builder = new TableLayoutBuilder(this);

        stringField = new JTextField(10);
        stringField.setName("stringProperty");

        comboBox = new JComboBox(new String[] { "item 0", "item 1", "item 2" });
        comboBox.setName("comboProperty");

        checkBox = new JCheckBox("checkbox");
        checkBox.setName("booleanProperty");

        builder.cell(new JLabel("string"));
        builder.gapCol();
        builder.cell(stringField);
        builder.relatedGapRow();
        builder.cell(new JLabel("combo"));
        builder.gapCol();
        builder.cell(comboBox);
        builder.relatedGapRow();
        builder.cell(checkBox);
        builder.relatedGapRow();
        
        JPanel nestedPanel =new JPanel();
        nestedField = new JTextField("test");
        nestedField.setName("nestedField");
        nestedPanel.add(nestedField);
        
        builder.cell(nestedPanel);

        builder.getPanel();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new SimplePanel());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JTextField getStringField() {
        return stringField;
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    public JTextField getNestedField() {
        return nestedField;
    }
}
