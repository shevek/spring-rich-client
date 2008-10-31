/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.binding.value.support;

import java.beans.PropertyChangeListener;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.value.DerivedValueModel;
import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.ValueModel;
import org.springframework.rules.closure.Closure;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * A value model wrapper that supports converting the wrapped value to and from
 * another type using the supplied conversion Closures.
 *
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public class TypeConverter extends AbstractValueModelWrapper implements DerivedValueModel {

	private final Closure convertTo;

	private final Closure convertFrom;

	private ValueChangeDetector valueChangeDetector;

	/**
	 * Convenience constructor using conversionExecutors.
	 *
	 * @param wrappedModel the inner valueModel
	 * @param convertTo conversion to use when setting a value.
	 * @param convertFrom conversion to use when getting a value.
	 *
	 * @see #TypeConverter(ValueModel, Closure, Closure)
	 */
	public TypeConverter(ValueModel wrappedModel, ConversionExecutor convertTo, ConversionExecutor convertFrom) {
		this(wrappedModel, new ConversionExecutorClosure(convertTo), new ConversionExecutorClosure(convertFrom));
	}

	/**
	 * Constructor which uses Closure blocks to convert between values.
	 *
	 * @param wrappedModel the inner valueModel
	 * @param convertTo Closure to execute when setting a value.
	 * @param convertFrom Closure to execute when getting a value.
	 */
	public TypeConverter(ValueModel wrappedModel, Closure convertTo, Closure convertFrom) {
		super(wrappedModel);
		this.convertTo = convertFrom;
		this.convertFrom = convertTo;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Value from inner model will be converted using the supplied convertFrom closure.
	 */
	public Object getValue() throws IllegalArgumentException {
		return convertFrom.call(super.getValue());
	}

	public void setValueSilently(Object value, PropertyChangeListener listenerToSkip) throws IllegalArgumentException {
		// only set the convertTo value if the convertFrom value has changed
		if (getValueChangeDetector().hasValueChanged(getValue(), value)) {
			super.setValueSilently(convertTo.call(value), listenerToSkip);
		}
	}

	public ValueModel[] getSourceValueModels() {
		return new ValueModel[] { getWrappedValueModel() };
	}

	public boolean isReadOnly() {
		return false;
	}

	public void setValueChangeDetector(ValueChangeDetector valueChangeDetector) {
		this.valueChangeDetector = valueChangeDetector;
	}

	protected ValueChangeDetector getValueChangeDetector() {
		if (valueChangeDetector == null) {
			valueChangeDetector = (ValueChangeDetector) ApplicationServicesLocator.services().getService(
					ValueChangeDetector.class);
		}
		return valueChangeDetector;
	}

	/**
	 * Helper class wrapping ConversionExecutors in a Closure.
	 */
	private static class ConversionExecutorClosure implements Closure {

		private final ConversionExecutor conversionExecutor;

		public ConversionExecutorClosure(ConversionExecutor conversionExecutor) {
			this.conversionExecutor = conversionExecutor;
		}

		public Object call(Object argument) {
			return conversionExecutor.execute(argument);
		}

	}
}