public class TestForm extends AbstractForm
{
    public TestForm()
    {
        super(FormModelHelper.createFormModel(new TestObject(), "testForm"));
    }

    protected JComponent createFormControl()
    {
        TableFormBuilder builder = new TableFormBuilder(getBindingFactory());
        builder.add("field1");
        builder.row();
        builder.add("field2");
        JPanel panel = (JPanel) builder.getForm();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
}