public class JodaTimeDateTimeBinder extends org.springframework.richclient.form.binding.support.AbstractBinder
{
    private boolean defaultsSet = false;

    private boolean readOnly = false;

    public JodaTimeDateTimeBinder()
    {
        super(DateTime.class);
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @SuppressWarnings("unchecked")
    protected JComponent createControl(Map context)
    {
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setEditor(new DateTextField());
        return datePicker;
    }


    @SuppressWarnings("unchecked")
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context)
    {
        if (!defaultsSet)
        {
            Map<Object, Object> defaults = UIManager.getDefaults();
            defaults.put("JXDatePicker.longFormat", "EEE dd/MM/yyyy");
            defaults.put("JXDatePicker.mediumFormat", "dd/MM/yyyy");
            defaults.put("JXDatePicker.shortFormat", "dd/MM");
            defaultsSet = true;
        }
        return new JodaTimeDateTimeBinding(formModel, formPropertyPath, ((JXDatePicker) control), this.readOnly);
    }
}
