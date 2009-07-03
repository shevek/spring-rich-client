public void propertyChange(PropertyChangeEvent evt)
{
    if (!isSettingText && !isReadOnly())
        controlValueChanged(new DateTime(datePicker.getDate()));
}
