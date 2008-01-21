package org.springframework.binding.validation.support;

import java.text.MessageFormat;
import java.util.Locale;

import junit.framework.TestCase;

import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Range;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Testcase for HibernateRulesValidator.
 * @author ldo
 *
 */
public class HibernateValidatorTests extends TestCase {
	private HibernateRulesValidator hibernateRulesValidator;

	private ValidatingFormModel model;

	/**
	 * Initialize test environment
	 */
	protected void setUp() throws Exception {
		Application.load(null);
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		DefaultApplicationServices applicationServices = new DefaultApplicationServices(applicationContext);

		DefaultApplicationLifecycleAdvisor advisor = new DefaultApplicationLifecycleAdvisor();
		Application app = new Application(advisor);
		advisor.setApplication(app);

		Application.instance().setApplicationContext(applicationContext);
		applicationServices.setApplicationContext(applicationContext);

		ApplicationServicesLocator locator = new ApplicationServicesLocator(applicationServices);
		ApplicationServicesLocator.load(locator);

		// create dummy message source that always return an empty string
		AbstractMessageSource source = new AbstractMessageSource() {
			@Override
			protected MessageFormat resolveCode(String s, Locale locale) {
				return new MessageFormat("");
			}
		};
		MessageSourceAccessor accessor = new MessageSourceAccessor(source);
		applicationServices.setMessageSourceAccesor(accessor);
		applicationServices.setMessageSource(source);

		applicationContext.refresh();

		// create formmodel to test on and initialize value models
		model = FormModelHelper.createFormModel(new ValidatingObject());
		model.getValueModel("stringValue");
		model.getValueModel("intValue");
		hibernateRulesValidator = new HibernateRulesValidator(model, ValidatingObject.class);
		model.setValidator(hibernateRulesValidator);
		model.setValidating(true);
	}

	/**
	 * Test valid object
	 */
	public void testValid() {
		ValidatingObject valid = new ValidatingObject();
		valid.setStringValue("valid");
		valid.setIntValue(8);
		model.setFormObject(valid);
		assertFalse(model.getValidationResults().getHasErrors());
		assertTrue(model.getValidationResults().getMessageCount("stringValue") == 0);
		assertTrue(model.getValidationResults().getMessageCount("intValue") == 0);

	}

	/**
	 * Test object with invalid String value (empty string on a NotEmpty
	 * property)
	 */
	public void testInvalidString() {
		ValidatingObject invalid = new ValidatingObject();
		invalid.setStringValue("");
		invalid.setIntValue(8);
		model.setFormObject(invalid);
		assertTrue(model.getValidationResults().getHasErrors());
		assertTrue(model.getValidationResults().getMessageCount("stringValue") != 0);
		assertTrue(model.getValidationResults().getMessageCount("intValue") == 0);
	}

	/**
	 * Test object with invalid Integer value (value out of range)
	 */
	public void testInvalidInt() {
		ValidatingObject invalid = new ValidatingObject();
		invalid.setStringValue("valid");
		invalid.setIntValue(20);
		model.setFormObject(invalid);
		assertTrue(model.getValidationResults().getHasErrors());
		assertTrue(model.getValidationResults().getMessageCount("stringValue") == 0);
		assertTrue(model.getValidationResults().getMessageCount("intValue") != 0);
	}

	public void testInvalidIntAndString() {
		ValidatingObject invalid = new ValidatingObject();
		invalid.setStringValue("");
		invalid.setIntValue(20);
		model.setFormObject(invalid);
		assertTrue(model.getValidationResults().getHasErrors());
		assertTrue(model.getValidationResults().getMessageCount("stringValue") != 0);
		assertTrue(model.getValidationResults().getMessageCount("intValue") != 0);
	}

	/**
	 * AssertTrue tests are not bound to a property, so they should not be
	 * included...
	 */
	public void testInvalidAssertTrue() {
		ValidatingObject invalid = new ValidatingObject();
		invalid.setStringValue("valid");
		invalid.setIntValue(12);
		model.setFormObject(invalid);
		assertFalse(model.getValidationResults().getHasErrors());
	}

	class ValidatingObject {
		private String stringValue;

		private int intValue;

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

}
