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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author andy
 * @since May 8, 2006 4:18:44 PM
 */
public class ReflectionAnnotationTranslatorTests extends TestCase {
    @NoValueAnnotation
    public void testNoValueAnnotation() throws Exception {
        final Annotation annotation = getClass().getDeclaredMethod("testNoValueAnnotation").getAnnotation(NoValueAnnotation.class);
        assertNotNull(annotation);
        
        final ReflectionAnnotationTranslator t = new ReflectionAnnotationTranslator();
        final Map<String,Object> result = new HashMap<String,Object>();
        t.translate(annotation, result);
        assertEquals(2, result.size());
        assertSame(Boolean.TRUE, result.get("org.springframework.binding.support.NoValueAnnotation"));
        NoValueAnnotation nva = (NoValueAnnotation)result.get("@" + NoValueAnnotation.class.getName());
        assertNotNull(nva);
    }
    
    @SingleValueAnnotation("Hello, world!")
    public void testSingleValueAnnotation() throws Exception {
        final Annotation annotation = getClass().getDeclaredMethod("testSingleValueAnnotation").getAnnotation(SingleValueAnnotation.class);
        assertNotNull(annotation);
        
        final ReflectionAnnotationTranslator t = new ReflectionAnnotationTranslator();
        final Map<String,Object> result = new HashMap<String,Object>();
        t.translate(annotation, result);
        assertEquals(2, result.size());
        assertEquals("Hello, world!", result.get("org.springframework.binding.support.SingleValueAnnotation"));
        SingleValueAnnotation sva = (SingleValueAnnotation)result.get("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("Hello, world!", sva.value());
    }
    
    @SingleValueAnnotation
    public void testSingleValueAnnotationWithDefaultValue() throws Exception {
        final Annotation annotation = getClass().getDeclaredMethod("testSingleValueAnnotationWithDefaultValue").getAnnotation(SingleValueAnnotation.class);
        assertNotNull(annotation);
        
        final ReflectionAnnotationTranslator t = new ReflectionAnnotationTranslator();
        final Map<String,Object> result = new HashMap<String,Object>();
        t.translate(annotation, result);
        assertEquals(2, result.size());
        assertEquals("This is a test", result.get("org.springframework.binding.support.SingleValueAnnotation"));
        SingleValueAnnotation sva = (SingleValueAnnotation)result.get("@" + SingleValueAnnotation.class.getName());
        assertNotNull(sva);
        assertEquals("This is a test", sva.value());
    }
    
    @MultiValueAnnotation(name="John Doe", age=25.5, rank=10)
    public void testMultiValueAnnotation() throws Exception {
        final Annotation annotation = getClass().getDeclaredMethod("testMultiValueAnnotation").getAnnotation(MultiValueAnnotation.class);
        assertNotNull(annotation);
        
        final ReflectionAnnotationTranslator t = new ReflectionAnnotationTranslator();
        final Map<String,Object> result = new HashMap<String,Object>();
        t.translate(annotation, result);
        assertEquals(5, result.size());
        assertEquals(Boolean.TRUE, result.get("org.springframework.binding.support.MultiValueAnnotation"));
        assertEquals("John Doe", result.get("org.springframework.binding.support.MultiValueAnnotation.name"));
        assertEquals(25.5, result.get("org.springframework.binding.support.MultiValueAnnotation.age"));
        assertEquals(10, result.get("org.springframework.binding.support.MultiValueAnnotation.rank"));
        MultiValueAnnotation mva = (MultiValueAnnotation)result.get("@" + MultiValueAnnotation.class.getName());
        assertNotNull(mva);
        assertEquals("John Doe", mva.name());
        assertEquals(25.5, mva.age());
        assertEquals(10, mva.rank());
    }
}
