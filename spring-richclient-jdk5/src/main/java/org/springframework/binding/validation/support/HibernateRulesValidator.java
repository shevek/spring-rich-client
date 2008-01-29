package org.springframework.binding.validation.support;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.util.Assert;
import org.springframework.rules.RulesSource;

/**
 * Validator that combines Hibernate validation with rules defined by a
 * RulesSource. First the rules will be checked, then validation will occurs
 * through a ClassValidator.<br/>
 *
 * Usage in a {@link ValidatingFormModel}: <br>
 *
 * {@example formModel.setValidator( new HibernateRulesValidator(formModel, rulesSource, SomeClass.class)); }
 *
 * @author Andy DuPue
 * @author Lieven Doclo
 *
 */
@SuppressWarnings("unchecked")
public class HibernateRulesValidator extends RulesValidator {

	private final ClassValidator hibernateValidator;

	private ValidatingFormModel formModel;

	private DefaultValidationResults results = new DefaultValidationResults();

	private Map<String, DefaultValidationResults> propertyResults;

	private Set<String> ignoredHibernateProperties;

	/**
	 * Creates a new HibernateRulesValidator
	 *
	 * @param formModel The {@link ValidatingFormModel} on which validation
	 * needs to occur
	 * @param rulesSource The {@link RulesSource} that contains the rules. If
	 * <code>null</code>, no rules will be enfored.
	 * @param clazz The class of the object this validator needs to check
	 */
	public HibernateRulesValidator(ValidatingFormModel formModel, RulesSource rulesSource, Class clazz) {
		super(formModel, rulesSource);
		this.formModel = formModel;
		hibernateValidator = new ClassValidator(clazz, new HibernateRulesMessageInterpolator());
		propertyResults = new HashMap<String, DefaultValidationResults>();
		ignoredHibernateProperties = new HashSet<String>();
	}

	/**
	 * Creates a new HibernateRulesValidator without specific Rules
	 *
	 * @param formModel The {@link ValidatingFormModel} on which validation
	 * needs to occur
	 * @param rulesSource The {@link RulesSource} that contains the rules. If
	 * <code>null</code>, no rules will be enfored.
	 * @param clazz The class of the object this validator needs to check
	 */
	public HibernateRulesValidator(ValidatingFormModel formModel, Class clazz) {
	    this(formModel, null, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValidationResults validate(Object object, String propertyName) {
		if (propertyName != null) {
			DefaultValidationResults result = propertyResults.get(propertyName);
			if (result == null) {
				result = new DefaultValidationResults();
				propertyResults.put(propertyName, result);
			}
			else {
				result.clearMessages();
			}
			result.addAllMessages(super.validate(object, propertyName));
		}
		final InvalidValue[] validationMessages = doHibernateValidate(object, propertyName);
		if (validationMessages != null) {
			for (final InvalidValue validationMessage : validationMessages) {
				ValidationMessage message = new DefaultValidationMessage(validationMessage.getPropertyName(),
						Severity.ERROR, resolveObjectName(validationMessage.getPropertyName()) + " "
								+ validationMessage.getMessage());
				propertyResults.get(validationMessage.getPropertyName()).addMessage(message);
			}
		}
		return combineResults(propertyResults);
	}

	/**
	 * Removes the messages not bound to a particular property in a
	 * {@link DefaultValidationResults}
	 *
	 * @param defaultValidationResults The {@link DefaultValidationResults} of a
	 * property
	 * @param propertyName The name of the property
	 * @return A {@link DefaultValidationResults} containing only results bound
	 * to the property
	 */
	private DefaultValidationResults cleanup(DefaultValidationResults defaultValidationResults,
			final String propertyName) {
		DefaultValidationResults cleanedUpResults = new DefaultValidationResults();
		for (ValidationMessage message : (Set<ValidationMessage>) defaultValidationResults.getMessages()) {
			if (message.getProperty().equals(propertyName)) {
				cleanedUpResults.addMessage(message);
			}
		}
		return cleanedUpResults;
	}

	/**
	 * Combines all {@link DefaultValidationResults}
	 *
	 * @param values The map with {@link DefaultValidationResults} by property
	 * @return A {@link ValidationResults} containing the combined results
	 */
	private ValidationResults combineResults(Map<String, DefaultValidationResults> values) {
		DefaultValidationResults results = new DefaultValidationResults();
		for (Map.Entry<String, DefaultValidationResults> entry : values.entrySet()) {
			results.addAllMessages(cleanup(entry.getValue(), entry.getKey()));
		}
		return results;
	}

	/**
	 * Validates the object through Hibernate Validator
	 *
	 * @param object The object that needs to be validated
	 * @param property The properties that needs to be validated
	 * @return An array of {@link InvalidValue}, containing all validation
	 * errors
	 */
	protected InvalidValue[] doHibernateValidate(final Object object, final String property) {
		if (property == null) {
			final List<InvalidValue> ret = new ArrayList<InvalidValue>();
			PropertyDescriptor[] propertyDescriptors;
			try {
				propertyDescriptors = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
			}
			catch (IntrospectionException e) {
				throw new IllegalStateException("Could not retrieve property information");
			}
			for (final PropertyDescriptor prop : propertyDescriptors) {
				if (formModel.hasValueModel(prop.getName())) {
				    if (!ignoredHibernateProperties.contains(prop.getName()))
                    {
                        final InvalidValue[] result = hibernateValidator.getPotentialInvalidValues(prop
                                .getName(), formModel.getValueModel(prop.getName()).getValue());
                        if (result != null)
                        {
                            for (final InvalidValue r : result)
                            {
                                ret.add(r);
                            }
                        }
                    }
				}
			}
			return ret.toArray(new InvalidValue[ret.size()]);
		}
		else if (object != null && !ignoredHibernateProperties.contains(property)) {
			if (formModel.hasValueModel(property)) {
				return hibernateValidator.getPotentialInvalidValues(property, formModel.getValueModel(property)
						.getValue());
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Clear the current validationMessages and the errors.
	 *
	 * @see #validate(Object, String)
	 */
	@Override
	public void clearMessages() {
		this.results.clearMessages();
	}

	/**
     * Add a property for the Hibernate validator to ignore.
     *
     * @param propertyName
     *            Name of the property to ignore. Cannot be null.
     */
    public void addIgnoredHibernateProperty(String propertyName)
    {
        Assert.notNull(propertyName);
        ignoredHibernateProperties.add(propertyName);
    }

    /**
     * Remove a property for the Hibernate validator to ignore.
     *
     * @param propertyName
     *            Name of the property to be removed. Cannot be null.
     */
    public void removeIgnoredHibernateProperty(String propertyName)
    {
        Assert.notNull(propertyName);
        ignoredHibernateProperties.remove(propertyName);
    }
}