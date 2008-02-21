package org.springframework.richclient.swing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class BigDecimalTextFieldTests extends TestCase {

	/**
	 * Note: we're testing if the NumberClass after getting the value from the
	 * {@link BigDecimalTextField} is the same as the input. Not the max/min of
	 * any Number. (hence the numbers are a bit random)
	 */
	private void doNumberTest(int digits, int decimals, NumberFormat format, Number expectedNumber) {
		BigDecimalTextField textField = new BigDecimalTextField(digits, decimals, true, expectedNumber.getClass(),
				format, format);
		textField.setValue(expectedNumber);
		assertEquals(expectedNumber, textField.getValue());
	}

	public void testBigDecimal() {
		doNumberTest(12, 10, new DecimalFormat("###,###,###,##0.##########"), new BigDecimal("123456789012.0123456789"));
	}

	public void testDouble() {
		doNumberTest(12, 10, new DecimalFormat("###,###,###,##0.##########"), new Double("1234567890.0123456789"));
	}

	public void testFloat() {
		doNumberTest(12, 10, new DecimalFormat("###,###,###,##0.##########"), new Float("1234567890.0123456789"));
	}

	public void testBigInteger() {
		doNumberTest(20, 0, new DecimalFormat("###,###,###,###,###,###,###,##0"),
				new BigInteger("12345678901234567890"));
	}

	public void testLong() {
		doNumberTest(20, 0, NumberFormat.getIntegerInstance(), Long.valueOf("123456789012345"));
	}

	public void testInteger() {
		doNumberTest(20, 0, NumberFormat.getIntegerInstance(), Integer.valueOf("1234567890"));
	}

	public void testShort() {
		doNumberTest(20, 0, NumberFormat.getIntegerInstance(), Short.valueOf("12345"));
	}
}