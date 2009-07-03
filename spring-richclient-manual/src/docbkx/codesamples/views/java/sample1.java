public class InitialView extends AbstractView
{
    // omitted for brevity

	protected JComponent createControl()
        {
		// In this view, we're just going to use standard Swing to place a
		// few controls.

		// The location of the text to display has been set as a Resource in the
		// property descriptionTextPath. So, use that resource to obtain a URL
		// and set that as the page for the text pane.

		JTextPane textPane = new JTextPane();
		JScrollPane spDescription = getComponentFactory().createScrollPane(textPane);
		try {
			textPane.setPage(getDescriptionTextPath().getURL());
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to load description URL", e);
		}

		JLabel lblMessage = getComponentFactory().createLabel(getFirstMessage());
		lblMessage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		JPanel panel = getComponentFactory().createPanel(new BorderLayout());
		panel.add(spDescription);
		panel.add(lblMessage, BorderLayout.SOUTH);

		return panel;
	}
}