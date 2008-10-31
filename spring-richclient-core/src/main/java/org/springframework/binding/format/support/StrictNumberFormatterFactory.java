package org.springframework.binding.format.support;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.binding.format.Formatter;

/**
 * Extending the {@link SimpleFormatterFactory} to return a
 * {@link StrictNumberFormat} wrapping the default {@link NumberFormat} to have
 * strict number parsing.
 *
 * @author Yudhi Widyatama
 * @author Jan Hoskens
 */
public class StrictNumberFormatterFactory extends SimpleFormatterFactory {
	public Formatter getNumberFormatter(Class numberClass) {
		Locale locale = getLocale();
		NumberFormat instance = NumberFormat.getInstance(locale);
		NumberFormat wrappedInstance = new StrictNumberFormat(instance);
		return new NumberFormatter(wrappedInstance);
	}
}