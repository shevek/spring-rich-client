package org.springframework.binding.validation.support;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.AssertFalse;
import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.binding.validation.RichValidator;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.Validator;
import org.springframework.richclient.core.Severity;
import org.springframework.richclient.util.Assert;
import org.springframework.rules.reporting.ObjectNameResolver;

/**
 * <p>
 * Validator which uses the {@link ClassValidator} of Hibernate to discover
 * {@link InvalidValue}. These are then translated to {@link ValidationMessage}s
 * and added to the {@link ValidationResults} as usual.
 * </p>
 *
 * <p>
 * Usage in a {@link ValidatingFormModel} where <code>SomeClass</code> has
 * annotations for Hibernate Validator:
 * </p>
 *
 * <pre>
 * formModel.setValidator(new HibernateRulesValidator(formModel, SomeClass.class));
 * </pre>
 *
 * <p>
 * This can be used in combination with other {@link Validator}s as well by
 * creating a {@link CompositeRichValidator}:
 * </p>
 *
 * <pre>
 * HibernateRulesValidator hibernateRulesValidator = new HibernateRulesValidator(getFormModel(), SomeClass.class);
 * hibernateRulesValidator.addIgnoredHibernateProperty(&quot;ignoredProperty&quot;);
 * RulesValidator rulesValidator = new RulesValidator(getFormModel(), myRulesSource);
 * getFormModel().setValidator(new CompositeRichValidator(rulesValidator, hibernateRulesValidator));
 * </pre>
 *
 * <p>
 * Note that we're adding one property to the {@link HibernateRulesValidator}
 * that will be ignored. This property will only be checked at your back-end by
 * Hibernate. The {@link RulesValidator} adds additional rules that are only
 * used at the front-end or/and may contain the equivalent of the
 * {@link AssertTrue} or {@link AssertFalse} methods on <code>SomeClass</code>.
 * </p>
 *
 * @author Andy DuPue
 * @author Lieven Doclo
 * @author Jan Hoskens
 */
@SuppressWarnings("unchecked")
public class HibernateRulesValidator implements RichValidator, ObjectNameResolver {


	private ValidatingFormModel formModel;
    private Class beanClass;
	private final ClassValidator hibernateValidator;

	private Set<String> ignoredHibernateProperties;

    private DefaultValidationResults results = new DefaultValidationResults();

	/**
	 * Creates a new HibernateRulesValidator without ignoring any properties.
	 *
	 * @param formModel The {@link ValidatingFormModel} on which validation
	 * needs to occur
	 * @param beanClass The class of the object this validator needs to check
	 */
	public HibernateRulesValidator(ValidatingFormModel formModel, Class beanClass) {
		this(formModel, beanClass, new HashSet<String>());
	}

	/**
	 * Creates a new HibernateRulesValidator with additionally a set of
	 * properties that should not be validated.
	 *
	 * @param formModel The {@link ValidatingFormModel} on which validation
	 * needs to occur
	 * @param beanClass The class of the object this validator needs to check
	 * @param ignoredHibernateProperties properties that should not be checked
	 * though are
	 */
	public HibernateRulesValidator(ValidatingFormModel formModel, Class beanClass,
                                   Set<String> ignoredHibernateProperties) {
		this.formModel = formModel;
        this.beanClass = beanClass;
        this.hibernateValidator = new ClassValidator(beanClass, new HibernateRulesMessageInterpolator());
		this.ignoredHibernateProperties = ignoredHibernateProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	public ValidationResults validate(Object object) {
		return validate(object, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public ValidationResults validate(Object object, String propertyName) {
        // TODO ge0ffrey: our code is not ready for our fail fast (due to usage of slices instead of hibernate-auditing)
//        // Normally ClassValidator.assertValid() checks this, but we use lower level methods of it instead
//        if (object != null && !beanClass.isInstance(object)) {
//            throw new IllegalArgumentException("The object (" + object + ") must be an instance of beanClass ("
//                    + beanClass + ").");
//        }
        // hibernate will return InvalidValues per propertyName, remove any
		// previous validationMessages.
		if (propertyName == null) {
			results.clearMessages();
		}
		else {
			results.clearMessages(propertyName);
		}

		addInvalidValues(doHibernateValidate(object, propertyName));
		return results;
	}

	/**
	 * Add all {@link InvalidValue}s to the {@link ValidationResults}.
	 */
	protected void addInvalidValues(InvalidValue[] invalidValues) {
		if (invalidValues != null) {
			for (InvalidValue invalidValue : invalidValues) {
				results.addMessage(translateMessage(invalidValue));
			}
		}
	}

	/**
	 * Translate a single {@link InvalidValue} to a {@link ValidationMessage}.
	 */
	protected ValidationMessage translateMessage(InvalidValue invalidValue) {
		return new DefaultValidationMessage(invalidValue.getPropertyName(), Severity.ERROR,
				resolveObjectName(invalidValue.getPropertyName()) + " " + invalidValue.getMessage());
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
				String propertyName = prop.getName();
				if (formModel.hasValueModel(propertyName) && !ignoredHibernateProperties.contains(propertyName)) {
					final InvalidValue[] result = hibernateValidator.getPotentialInvalidValues(propertyName, formModel
							.getValueModel(propertyName).getValue());
					if (result != null) {
						for (final InvalidValue r : result) {
							ret.add(r);
						}
					}
				}
			}
			return ret.toArray(new InvalidValue[ret.size()]);
		}
		else if (!ignoredHibernateProperties.contains(property) && formModel.hasValueModel(property)) {
			return hibernateValidator.getPotentialInvalidValues(property, formModel.getValueModel(property).getValue());
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
	public void clearMessages() {
		this.results.clearMessages();
	}

	/**
	 * Add a property for the Hibernate validator to ignore.
	 *
	 * @param propertyName Name of the property to ignore. Cannot be null.
	 */
	public void addIgnoredHibernateProperty(String propertyName) {
		Assert.notNull(propertyName);
		ignoredHibernateProperties.add(propertyName);
	}

	/**
	 * Remove a property for the Hibernate validator to ignore.
	 *
	 * @param propertyName Name of the property to be removed. Cannot be null.
	 */
	public void removeIgnoredHibernateProperty(String propertyName) {
		Assert.notNull(propertyName);
		ignoredHibernateProperties.remove(propertyName);
	}

	/**
	 * {@inheritDoc}
	 */
	public String resolveObjectName(String objectName) {
		return formModel.getFieldFace(objectName).getDisplayName();
	}
}