public class JodaTimeDateTimeBinding extends CustomBinding implements PropertyChangeListener
{
    private final JXDatePicker datePicker;
    private final boolean readOnly;

    private boolean isSettingText = false;

    public JodaTimeDateTimeBinding(FormModel model, String path, JXDatePicker datePicker, boolean readOnly)
    {
        super(model, path, DateTime.class);
        this.datePicker = datePicker;
        this.readOnly = readOnly;
    }

    @Override
    protected void valueModelChanged(Object newValue)
    {
        isSettingText = true;
        setDatePickerValue((DateTime) newValue);
        readOnlyChanged();
        isSettingText = false;
    }

    private void setDatePickerValue(DateTime dateTime)
    {
        if (dateTime == null)
        {
            datePicker.setDate(null);
        }
        else
        {
            datePicker.setDate(dateTime.toDate());
        }
    }

    @Override
    protected JComponent doBindControl()
    {
        setDatePickerValue((DateTime) getValue());
        datePicker.getEditor().addPropertyChangeListener("value", this);
        return datePicker;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if (!isSettingText && !isReadOnly())
            controlValueChanged(new DateTime(datePicker.getDate()));
    }

    @Override
    protected void readOnlyChanged()
    {
        datePicker.setEditable(isEnabled() && !this.readOnly && !isReadOnly());
    }

    @Override
    protected void enabledChanged()
    {
        datePicker.setEnabled(isEnabled());
        readOnlyChanged();
    }
}