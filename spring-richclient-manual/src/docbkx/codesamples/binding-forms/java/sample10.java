@Override
protected void valueModelChanged(Object newValue)
{
    isSettingText = true;
    setDatePickerValue((DateTime) newValue);
    readOnlyChanged();
    isSettingText = false;
}
