package org.springframework.richclient.application.setup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.util.LabelUtils;
import org.springframework.richclient.wizard.AbstractWizardPage;

/**
 * @author cro
 */
public class SetupIntroWizardPage extends AbstractWizardPage {
    private static final Color TITLE_COLOR = new Color(48, 48, 48);

    public SetupIntroWizardPage() {
        super("intro");
    }

    protected JComponent createControl() {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();

        builder.setDefaultInsets(new Insets(10, 20, 0, 0));

        builder.append(createWelcomeToLabel(), 1, 1, true, false);
        builder.nextLine();
        builder.append(createTitleLabel(), 1, 1, true, false, new Insets(10, 20, 15, 0));

        builder.nextLine();
        builder.append(createDescriptionLabel(), 1, 1, true, false);

        builder.nextLine();
        builder.append(createSpacer(0, 0), 1, 1, true, true);

        JPanel control = builder.getPanel();
        control.setOpaque(false);
        return control;
    }

    protected JLabel createTitleLabel() {
        JLabel bigTitleLabel = new JLabel(getMessage("setup.intro.title")) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g2);
            }
        };
        Font font = new Font(bigTitleLabel.getFont().getFontName(), Font.BOLD, 22);
        bigTitleLabel.setFont(font);
        bigTitleLabel.setForeground(TITLE_COLOR);
        return bigTitleLabel;
    }

    private JLabel createWelcomeToLabel() {
        JLabel welcomeToLabel = new JLabel(this.getMessage("setup.intro.welcomeTo"));
        Font font = new Font(welcomeToLabel.getFont().getFontName(), Font.BOLD, welcomeToLabel.getFont().getSize());

        welcomeToLabel.setFont(font);
        welcomeToLabel.setForeground(TITLE_COLOR);

        return welcomeToLabel;
    }

    private JLabel createDescriptionLabel() {
        return new JLabel(LabelUtils.htmlBlock(getMessage("setup.intro.description")));
    }

    private JComponent createSpacer(final int x, final int y) {
        JPanel spacer = new JPanel() {
            public Dimension getPreferredSize() {
                return new Dimension(x, y);
            }
        };
        spacer.setOpaque(false);
        return spacer;
    }
}