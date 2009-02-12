package org.springframework.richclient.util.l2fprod;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;

import java.util.Enumeration;
import java.util.Properties;

@SuppressWarnings("unchecked")
public class L2fProdSupport
{

    public static Property[] makePropertyArray(Properties properties)
    {
        Property[] propertiesArray = new Property[properties.size()];
        Enumeration propertyEnum = properties.propertyNames();
        int i = 0;

        while (propertyEnum.hasMoreElements())
        {
            String name = (String) propertyEnum.nextElement();
            String value = properties.get(name).toString();
            propertiesArray[i++] = makePropertyInCategory(name, value);
        }
        return propertiesArray;
    }

    private static String getPropertyCategory(String name)
    {
        int pos = name.indexOf('.');
        return (pos == -1) ? name : name.substring(0, pos);
    }

    private static Property makePropertyInCategory(String name, String value)
    {
        String category = getPropertyCategory(name);
        return makeProperty(category, name, value);
    }

    public static Property makeProperty(String category, String name, String value)
    {
        DefaultProperty prop;
        prop = new DefaultProperty();
        prop.setCategory(category);

        prop.setName(name);
        prop.setDisplayName(name);
        prop.setValue(value);
        prop.setShortDescription(value);
        return prop;
    }

}
