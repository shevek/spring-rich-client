/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.samples.petclinic.domain;

import org.springframework.rules.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;
import org.springframework.samples.petclinic.Owner;

/**
 * @author Keith Donald
 */
public class PetClinicValidationRulesSource extends DefaultRulesSource {

    public PetClinicValidationRulesSource() {
        addRules(createOwnerRules());
    }

    private Rules createOwnerRules() {
        Rules rules = Rules.createRules(Owner.class);
        rules.add("firstName", getNamePropertyConstraint());
        rules.add("lastName", getNamePropertyConstraint());
        rules.add("address", required());
        return rules;
    }

    private Constraint getNamePropertyConstraint() {
        return all(new Constraint[] { required(), maxLength(25),
                regexp("[a-zA-Z]*", "alphabetic") });
    }

}