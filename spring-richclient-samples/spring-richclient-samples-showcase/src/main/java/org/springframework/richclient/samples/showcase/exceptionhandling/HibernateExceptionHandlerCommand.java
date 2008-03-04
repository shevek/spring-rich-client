package org.springframework.richclient.samples.showcase.exceptionhandling;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.exceptionhandling.HibernateValidatorDialogExceptionHandler;

/**
 * Throws an {@link InvalidStateException} with several {@link InvalidValue}s
 * to show the {@link HibernateValidatorDialogExceptionHandler}.
 *
 * @author Jan Hoskens
 *
 */
public class HibernateExceptionHandlerCommand extends ActionCommand {

	/**
	 * Dummy bean for invalidValues.
	 */
	public static class MyBean {

	}

	@Override
	protected void doExecuteCommand() {
		List<InvalidValue> invalidExceptions = new ArrayList<InvalidValue>(5);
		MyBean myBean = new MyBean();
		invalidExceptions.add(new InvalidValue("first invalid message", MyBean.class, "firstProperty",
				"first invalid value", myBean));
		invalidExceptions.add(new InvalidValue("second invalid message", MyBean.class, "secondProperty",
				"second invalid value", myBean));
		invalidExceptions.add(new InvalidValue("third invalid message", MyBean.class, "thirdProperty",
				"third invalid value", myBean));
		invalidExceptions.add(new InvalidValue("fourth invalid message", MyBean.class, "fourthProperty",
				"fourth invalid value", myBean));
		invalidExceptions.add(new InvalidValue("fifth invalid message", MyBean.class, "fifthProperty",
				"fifth invalid value", myBean));
		throw new InvalidStateException(invalidExceptions.toArray(new InvalidValue[] {}));
	}
}