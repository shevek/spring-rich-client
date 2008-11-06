package org.springframework.richclient.form.binding.swing.text;

import javax.swing.text.DefaultFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;

@SuppressWarnings("serial")
public class TimeFormatter extends DefaultFormatter
{
    public static final char SEPARATOR = ':';
    public static final String SEPARATOR_STRING = ":";
    private DateFormat dateFormat = new SimpleDateFormat("H:mm");

    public TimeFormatter()
    {
        setAllowsInvalid(true);
        setCommitsOnValidEdit(false);
        setOverwriteMode(false);
        dateFormat.setLenient(false);
    }

    public Object stringToValue(String string) throws ParseException
    {
        if ((string == null) || (string.length() == 0))
            return null;
        int length = string.length();
        if ((length == 1) && string.equals(SEPARATOR_STRING)) //  one separator means now
            return Calendar.getInstance().getTime();
        if ((string.indexOf(SEPARATOR) == -1) && (length > 1))
        {
            if (string.charAt(length - 2) > '5')
            {
                string = string.substring(0, length - 1) + SEPARATOR_STRING + string.substring(length - 1);
            }
            else
            {
                string = string.substring(0, length - 2) + SEPARATOR_STRING + string.substring(length - 2);
            }
        }
        if (string.startsWith(Character.toString(SEPARATOR)))
            string = "0" + string;
        return dateFormat.parse(string);
    }

    public String valueToString(Object value) throws ParseException
    {
        if (value != null)
        {
            return dateFormat.format(value);
        }
        return "";
    }
}