/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.samples.petclinic.domain;

import org.springframework.rules.DefaultRulesSource;
import org.springframework.rules.Rules;
import org.springframework.rules.UnaryPredicate;
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
        return rules;
    }

    private UnaryPredicate getNamePropertyConstraint() {
        return all(new UnaryPredicate[] { required(), maxLength(25),
                regexp("[a-zA-Z]*", "alphabetic") });
    }

}