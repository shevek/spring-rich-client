package org.springframework.binding.validation.support;

import org.springframework.binding.validation.RichValidator;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.richclient.util.Assert;

/**
 * This {@link RichValidator} allows combining several {@link RichValidator}s.
 * Eg when using a validator for Hibernate (validation available on persistent
 * object through annotations), you might want to add a RulesValidator for more
 * specific rules or just to expand its features.
 *
 * @author Jan Hoskens
 *
 */
public class CompositeRichValidator implements RichValidator {

	private RichValidator[] validators;

	/**
	 * Convenient creation of {@link CompositeRichValidator} using two
	 * validators.
	 */
	public CompositeRichValidator(RichValidator validator1, RichValidator validator2) {
		this(new RichValidator[] { validator1, validator2 });
	}

	/**
	 * Create a {@link CompositeRichValidator} that combines all the results
	 * from the given validators.
	 */
	public CompositeRichValidator(RichValidator[] validators) {
		Assert.notNull(validators);
		this.validators = validators;
	}

	public ValidationResults validate(Object object, String property) {
		DefaultValidationResults results = new DefaultValidationResults();
		for (int i = 0; i < validators.length; ++i) {
			results.addAllMessages(validators[i].validate(object, property));
		}
		return results;
	}

	public ValidationResults validate(Object object) {
		DefaultValidationResults results = new DefaultValidationResults();
		for (int i = 0; i < validators.length; ++i) {
			results.addAllMessages(validators[i].validate(object));
		}
		return results;
	}

}