/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.samples.petclinic.domain;

import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;
import org.springframework.samples.petclinic.Owner;
import org.springframework.util.closure.Constraint;

/**
 * @author Keith Donald
 */
public class PetClinicValidationRulesSource extends DefaultRulesSource {

	public PetClinicValidationRulesSource() {
		super();
		addRules(createOwnerRules());
	}

	private Rules createOwnerRules() {
		return new Rules(Owner.class) {
			protected void initRules() {
				add("firstName", getNameValueConstraint());
				add("lastName", getNameValueConstraint());
				add("address", required());
			}

			private Constraint getNameValueConstraint() {
				return all(new Constraint[] { required(), maxLength(25), regexp("[a-zA-Z]*", "alphabetic") });
			}

		};
	}

}