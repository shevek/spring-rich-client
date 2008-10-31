package org.springframework.binding.validation.support;

import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Range;


public class ValidatingObject {
    private String stringValue;

    private int intValue;

    public ValidatingObject()
    {
        stringValue = "invalid";
        intValue = 12;
    }

    @NotEmpty
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Range(min = 5, max = 15)
    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @AssertTrue
    public boolean intShouldBeEightAndStringShouldBeValid() {
        return intValue == 8 && stringValue.equals("valid");
    }
}
