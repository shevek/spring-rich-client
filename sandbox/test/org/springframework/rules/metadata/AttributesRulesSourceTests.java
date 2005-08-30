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
package org.springframework.rules.metadata;

import junit.framework.TestCase;

/**
 * @author Oliver Hutchison
 */    
public class AttributesRulesSourceTests extends TestCase {
        
    public void testLoadsAttributes() {
//        Attributes attributes = new CommonsAttributes();        
//        assertTrue("You must compile the attributes to run this test", 
//                attributes.getAttributes(Foo.class).size() == 1);        
//        AttributesRulesSource ars = new AttributesRulesSource(attributes);
// 
//        Rules rules = ars.getRules(Foo.class);
//
//        CompoundPropertyConstraint pc1 = (CompoundPropertyConstraint) rules.getPropertyConstraint("property1");
//        assertNotNull(pc1);
//        assertEquals(3, ((CompoundConstraint) pc1.getPredicate()).size());
//        assertContains(pc1, Required.class);
//        assertContains(pc1, StringLengthConstraint.class);
//        assertContains(pc1, Not.class);
//        
//        PropertyConstraint pc2 = rules.getPropertyConstraint("property2");
//        assertNull(pc2);
    }
    
//    private void assertContains(CompoundPropertyConstraint cpc, Class constraintClass) {
//        CompoundConstraint cc = (CompoundConstraint) cpc.getPredicate();
//        for (int i = 0; i < cc.size(); i++) {
//            Constraint c = cc.get(i);
//            
//            if (c.getClass().equals(constraintClass)) {
//                return; 
//            }          
//            if (c instanceof PropertyValueConstraint) {                    
//                c = ((PropertyValueConstraint) c).getConstraint();
//                if (c.getClass().equals(constraintClass)) {
//                    return;
//                } 
//            }
//        }
//        fail("Could not find constraint with class [" + constraintClass + "]");
//    }

    /**
     * @@Object()
     */
    public static class Foo {

        private String property1;

        /**
         * @@Object()
         * 
         * @@StringLengthConstraint(255)
         * @@Not(new PropertiesConstraint("property1", new EqualTo(),
         *           "property2"))
         */
        public String getProperty1() {
            return property1;
        }

        /**
         * @@Required()
         */
        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return "";
        }

        public void setProperty2(String property2) {
        }
    }
}