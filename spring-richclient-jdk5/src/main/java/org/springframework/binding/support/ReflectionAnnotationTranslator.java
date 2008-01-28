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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Uses reflection to translate annotations into property level user metadata.
 * This implementation will translate annotations in the following manner:
 * <ul>
 * <li>The Annotation instance itself will be placed into a key in the form of
 * the '@' character followed by the annotation class name.  For example:
 * <code><pre>
 *   import com.mypkg.SomeAnnotation;
 *   ...
 *   &#64;SomeAnnotation("This is a test")
 *   public String getMyProperty()
 *   ...
 * </pre></code>
 * Will store the "SomeAnnotation" instance in the key
 * <code>@com.mypkg.SomeAnnotation</code>.
 * This allows 1.5 code easier access to annotation data.
 * <li>If the Annotation does not define any value or properties, then
 * <code>Boolean.TRUE</code> will be stored against the fully qualified
 * classname of the Annotation as the key.  For example:
 * <code><pre>
 *   import com.mypkg.MyAnnotation;
 *   ...
 *   &#64;MyAnnotation
 *   public String getMyProperty()
 *   ...
 * </pre></code>
 * Will store <code>Boolean.TRUE</code> in the key
 * <code>com.mypkg.MyAnnotation</code>.
 * <li>If the Annotation defines a single "value", then the annotation value
 * will be stored using the fully qualified classname of the Annotation as the
 * key and single value of the annotation as the key's value.  For example:
 * <code><pre>
 *   import com.mypkg.SomeAnnotation;
 *   ...
 *   &#64;SomeAnnotation("Hello, World!")
 *   public String getMyProperty()
 *   ...
 * </pre></code>
 * Will store the value <code>"Hello, World!"</code> in the key
 * <code>com.mypkg.SomeAnnotation</code>.
 * <li>If the Annotation has multiple values, then <code>Boolean.TRUE</code>
 * will be stored against the fully qualified annotation class name and each
 * value will be stored using the fully qualified classname of the Annotation
 * with a "." and the property name of the annotation as the key value.  For
 * example:
 * <code><pre>
 *   import com.mypkg.OtherAnnotation;
 *   ...
 *   &#64;OtherAnnotation(aprop1="Something", aprop2=100)
 *   public String getMyProperty()
 *   ...
 * </pre></code>
 * Will create three key + value pairs:
 * <table>
 * <tr><td><b>Key</b></td><td/><td><b>Value</b></td></tr>
 * <tr><td>com.mypkg.OtherAnnotation</td><td>=</td><td>Boolean.TRUE</td></tr>
 * <tr><td>com.mypkg.OtherAnnotation.aprop1</td><td>=</td><td>"Something"</td></tr>
 * <tr><td>com.mypkg.OtherAnnotation.aprop2</td><td>=</td><td>100</td></tr>
 * </table>
 * </ul>
 * 
 * @author andy
 * @since May 8, 2006 4:08:53 PM
 */
public class ReflectionAnnotationTranslator implements AnnotationTranslator {
    public final static String SINGLE_VALUE_METHOD_NAME = "value";
    
    public void translate(final Annotation annotation, final Map<String, Object> result) {
        try {
            final Class<? extends Annotation> type = annotation.annotationType();
            final Method[] methods = type.getDeclaredMethods();
            final String name = type.getName();
            
            result.put("@" + name, annotation);
            
            if(methods.length == 0) {
                result.put(name, Boolean.TRUE);
            } else if(methods.length == 1 && SINGLE_VALUE_METHOD_NAME.equals(methods[0].getName())) {
                result.put(name, methods[0].invoke(annotation));
            } else {
                result.put(name, Boolean.TRUE);
                for(final Method method : methods) {
                    if(Modifier.isPublic(method.getModifiers())) {
                        result.put(name + "." + method.getName(), method.invoke(annotation));
                    }
                }
            }
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch(InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
