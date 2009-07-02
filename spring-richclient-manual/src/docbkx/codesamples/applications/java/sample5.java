public class DynamicStatusBar extends DefaultStatusBar
{
    protected JComponent createControl()
    {
        JPanel statusBar;

        FormLayout layout = new FormLayout(
                new ColumnSpec[]
                        {
                                FormFactory.GLUE_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                        },
                new RowSpec[]
                        {
                                FormFactory.DEFAULT_ROWSPEC
                        });

        statusBar = new JPanel(layout);

        StatusBarProgressMonitor progressMonitor = createStatusBarProgressMonitor();

        statusBar.add(createMessageLabel(), new CellConstraints(1, 1));
        statusBar.add(createClock(), new CellConstraints(3, 1));
        statusBar.add(progressMonitor.getControl(), new CellConstraints(5, 1));

        progressMonitor.getControl().setPreferredSize(new Dimension(200, 17));

        statusBar.setBorder(new ShadowBorder());

        return statusBar;
    }

    private JLabel createClock()
    {
        final JLabel label = new JLabel();
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    DateFormatter formatter = new DateFormatter(DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                              DateFormat.MEDIUM));
                    final String text = formatter.formatValue(new Date());
                    label.setText(text);
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            }
        });
        t.start();
        return label;
    }
}