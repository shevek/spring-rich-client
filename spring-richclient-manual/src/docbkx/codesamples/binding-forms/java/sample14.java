public class TestForm extends AbstractForm
{
    public TestForm()
    {
        super(FormModelHelper.createFormModel(new TestObject(), "testForm"));
    }

    protected JComponent createFormControl()
    {
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new FormLayout(
                new ColumnSpec[]
                        {
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
                        },
                new RowSpec[]
                        {
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC
                        }
        ));

        TextComponentBinder binder = new TextComponentBinder();

        Map map = new HashMap();

        content.add(new JLabel("Field 1"), new CellConstraints(1, 1));
        content.add(binder.bind(getFormModel(), "field1", map).getControl(), new CellConstraints(3, 1));
        content.add(new JLabel("Field 2"), new CellConstraints(1, 3));
        content.add(binder.bind(getFormModel(), "field2", map).getControl(), new CellConstraints(3, 3));

        return content;
    }
}
