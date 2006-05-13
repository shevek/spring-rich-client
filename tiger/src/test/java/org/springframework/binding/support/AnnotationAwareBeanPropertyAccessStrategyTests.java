/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.binding.support;

import java.util.Map;

import org.springframework.binding.PropertyMetadataAccessStrategy;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.binding.form.support.DefaultFormModel;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author andy
 * @since May 8, 2006 5:20:39 PM
 */
public class AnnotationAwareBeanPropertyAccessStrategyTests extends SpringRichTestCase {
    public void testBeanWithNoAnnotations() {
        final AnnotationAwareBeanPropertyAccessStrategy pas = new AnnotationAwareBeanPropertyAccessStrategy(new NoAnnotationTestBean());
        final PropertyMetadataAccessStrategy mas = pas.getMetadataAccessStrategy();
        
        Map<String,Object> um = mas.getAllUserMetadata("name");
        assertTrue(um == null || um.size() == 0);
        um = mas.getAllUserMetadata("age");
        assertTrue(um == null || um.size() == 0);
        um = mas.getAllUserMetadata("rank");
        assertTrue(um == null || um.size() == 0);
    }
    
    public void testBeanWithAnnotations() throws Exception {
        final AnnotationAwareBeanPropertyAccessStrategy pas = new AnnotationAwareBeanPropertyAccessStrategy(new TestBean());
        final PropertyMetadataAccessStrategy mas = pas.getMetadataAccessStrategy();
        
        Map<String,Object> um = mas.getAllUserMetadata("name");
        assertNotNull(um);
        assertEquals(2, um.size());
        assertEquals(Boolean.TRUE, um.get("org.springframework.binding.support.NoValueAnnotation"));
        NoValueAnnotation nva = (NoValueAnnotation)um.get("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        
        um = mas.getAllUserMetadata("age");
        assertNotNull(um);
        assertEquals(4, um.size());
        assertEquals(Boolean.TRUE, um.get("org.springframework.binding.support.NoValueAnnotation"));
        assertEquals("The Age Method", um.get("org.springframework.binding.support.SingleValueAnnotation"));
        nva = (NoValueAnnotation)um.get("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        SingleValueAnnotation sva = (SingleValueAnnotation)um.get("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("The Age Method", sva.value());
        
        um = mas.getAllUserMetadata("rank");
        assertNotNull(um);
        assertEquals(9, um.size());
        assertEquals(Boolean.TRUE, um.get("org.springframework.binding.support.NoValueAnnotation"));
        assertEquals("The Rank Method", um.get("org.springframework.binding.support.SingleValueAnnotation"));
        assertEquals(Boolean.TRUE, um.get("org.springframework.binding.support.MultiValueAnnotation"));
        assertEquals("First Test Name", um.get("org.springframework.binding.support.MultiValueAnnotation.name"));
        assertEquals(24.5, um.get("org.springframework.binding.support.MultiValueAnnotation.age"));
        assertEquals(10, um.get("org.springframework.binding.support.MultiValueAnnotation.rank"));
        nva = (NoValueAnnotation)um.get("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        sva = (SingleValueAnnotation)um.get("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("The Rank Method", sva.value());
        MultiValueAnnotation mva = (MultiValueAnnotation)um.get("@" + MultiValueAnnotation.class.getName());
        assertNotNull(mva);
        assertEquals("First Test Name", mva.name());
        assertEquals(24.5, mva.age());
        assertEquals(10, mva.rank());
        
        um = mas.getAllUserMetadata("description");
        assertTrue(um == null || um.size() == 0);
    }
    
    public void testInFormModel() {
        final AnnotationAwareBeanPropertyAccessStrategy pas = new AnnotationAwareBeanPropertyAccessStrategy(new TestBean());
        final FormModel fm = new DefaultFormModel(pas);
        
        FieldMetadata pm = fm.getFieldMetadata("name"); 
        assertNotNull(pm);
        assertEquals(Boolean.TRUE, pm.getUserMetadata("org.springframework.binding.support.NoValueAnnotation"));
        NoValueAnnotation nva = (NoValueAnnotation)pm.getUserMetadata("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        assertEquals(2, pm.getAllUserMetadata().size());
        
        pm = fm.getFieldMetadata("age");
        assertNotNull(pm);
        assertEquals(Boolean.TRUE, pm.getUserMetadata("org.springframework.binding.support.NoValueAnnotation"));
        assertEquals("The Age Method", pm.getUserMetadata("org.springframework.binding.support.SingleValueAnnotation"));
        nva = (NoValueAnnotation)pm.getUserMetadata("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        SingleValueAnnotation sva = (SingleValueAnnotation)pm.getUserMetadata("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("The Age Method", sva.value());
        assertEquals(4, pm.getAllUserMetadata().size());
        
        pm = fm.getFieldMetadata("rank");
        assertNotNull(pm);
        assertEquals(Boolean.TRUE, pm.getUserMetadata("org.springframework.binding.support.NoValueAnnotation"));
        assertEquals("The Rank Method", pm.getUserMetadata("org.springframework.binding.support.SingleValueAnnotation"));
        assertEquals(Boolean.TRUE, pm.getUserMetadata("org.springframework.binding.support.MultiValueAnnotation"));
        assertEquals("First Test Name", pm.getUserMetadata("org.springframework.binding.support.MultiValueAnnotation.name"));
        assertEquals(24.5, pm.getUserMetadata("org.springframework.binding.support.MultiValueAnnotation.age"));
        assertEquals(10, pm.getUserMetadata("org.springframework.binding.support.MultiValueAnnotation.rank"));
        nva = (NoValueAnnotation)pm.getUserMetadata("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
        sva = (SingleValueAnnotation)pm.getUserMetadata("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("The Rank Method", sva.value());
        MultiValueAnnotation mva = (MultiValueAnnotation)pm.getUserMetadata("@" + MultiValueAnnotation.class.getName());
        assertNotNull(mva);
        assertEquals("First Test Name", mva.name());
        assertEquals(24.5, mva.age());
        assertEquals(10, mva.rank());
        assertEquals(9, pm.getAllUserMetadata().size());
        
        pm = fm.getFieldMetadata("description");
        assertNotNull(pm);
        assertTrue(pm.getAllUserMetadata() == null || pm.getAllUserMetadata().size() == 0);
    }
    
    
    
    
    
    
    
    public static class NoAnnotationTestBean {
        private String name;
        private double age;
        private int rank;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public double getAge() {
            return age;
        }

        public void setAge(final double age) {
            this.age = age;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(final int rank) {
            this.rank = rank;
        }
    }
    
    
    
    
    
    
    public static class TestBean {
        private String name;
        private double age;
        private int rank;
        private String description;
        
        @NoValueAnnotation
        public String getName() {
            return name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        @SingleValueAnnotation("The Age Method")
        public double getAge() {
            return age;
        }

        @NoValueAnnotation
        public void setAge(final double age) {
            this.age = age;
        }

        @MultiValueAnnotation(name="Second Test Name", age=30, rank=5)
        @SingleValueAnnotation("The Rank Method")
        public void setRank(final int rank) {
            this.rank = rank;
        }

        @MultiValueAnnotation(name="First Test Name", age=24.5, rank=10)
        @NoValueAnnotation
        public int getRank() {
            return rank;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }
    }
}
