/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.springframework.binding.form;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

import junit.framework.TestCase;

import org.springframework.binding.form.support.CompoundFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;

/**
 * @author Keith Donald
 */
public class FormModelTest extends TestCase {

    public void setUp() {
        Application.load(null);
        new Application(new DefaultApplicationLifecycleAdvisor());
        Application.services().setApplicationContext(new StaticApplicationContext());
    }

    public static class Employee {
        private String name;

        private Employee supervisor;

        private Address address = new Address();

        private int age;

        private Date hireDate;

        private Map editors = new HashMap();

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getHireDate() {
            return hireDate;
        }

        public void setHireDate(Date hireDate) {
            this.hireDate = hireDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Employee getSupervisor() {
            return supervisor;
        }

        public void setSupervisor(Employee supervisor) {
            this.supervisor = supervisor;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address {
        private String streetAddress1;

        private String streetAddress2;

        private String city;

        private String state;

        private String zipCode;

        private Country country;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStreetAddress1() {
            return streetAddress1;
        }

        public void setStreetAddress1(String streetAddress1) {
            this.streetAddress1 = streetAddress1;
        }

        public String getStreetAddress2() {
            return streetAddress2;
        }

        public void setStreetAddress2(String streetAddress2) {
            this.streetAddress2 = streetAddress2;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public Country getCountry() {
            return country;
        }

        public void setCountry(Country country) {
            this.country = country;
        }
    }

    private static class Country {
        private String name;

        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public FormModelTest() {
        super();
    }

    public void testValueHolder() {
        ValueHolder v = new ValueHolder();
        assertTrue(v.getValue() == null);
        Object object = new Object();
        v.setValue(object);
        assertTrue(v.getValue() == object);
    }

    public void testBufferedValueModelCommit() {
        ValueHolder v = new ValueHolder("Keith");
        assertTrue(v.getValue().equals("Keith"));
        BufferedValueModel buf = new BufferedValueModel(v);
        buf.setValue("Keri");
        assertTrue(v.getValue().equals("Keith"));
        buf.commit();
        assertTrue(v.getValue().equals("Keri"));
    }

    public void testCompoundFormModel() {
        CompoundFormModel formModel = new CompoundFormModel(new Employee());
        ConfigurableFormModel supervisorModel = formModel.createChild("supervisorPage", "supervisor");
        
        JTextField field = (JTextField)createBoundControl(supervisorModel, "name");
        assertTrue(field.getText().equals(""));
        field.setText("Don");
        ValueModel name = supervisorModel.getValueModel("name");
        assertTrue(name.getValue().equals("Don"));
        supervisorModel.setEnabled(true);
        supervisorModel.commit();
        Employee emp = (Employee)formModel.getFormObject();
        assertTrue(emp.getSupervisor().getName().equals("Don"));
    }

    public void testNestedCompoundFormModel() {
        CompoundFormModel formModel = new CompoundFormModel(new Employee());
        NestingFormModel address = formModel.createCompoundChild("AddressForm", "address");
        ConfigurableFormModel countryPage = address.createChild("CountryForm", "country");
        JTextField field = (JTextField)createBoundControl(countryPage, "name");
        assertTrue(field.getText().equals(""));
        field.setText("USA");
        ValueModel name = countryPage.getValueModel("name");
        assertTrue(name.getValue().equals("USA"));
        countryPage.setEnabled(true);
        countryPage.commit();
        address.commit();
        Employee emp = (Employee)formModel.getFormObject();
        assertTrue(emp.getAddress().getCountry().getName().equals("USA"));
    }

    public void testChildModelControlsEditabled() {
        final NestingFormModel formModel = FormModelHelper.createCompoundFormModel(new Employee(), "EmployeeForm");
        final ConfigurableFormModel addressFormModel = formModel.createChild("AddressPage", "address");
        
        final JTextField cityField = (JTextField)createBoundControl(addressFormModel, "city");
        assertFalse(addressFormModel.getFormPropertyState("city").isReadOnly());
        assertTrue(cityField.isEditable());
    }
    
    public void testPageFormModel() {
        ConfigurableFormModel employeePage = FormModelHelper.createFormModel(new Employee());
        JTextField field = (JTextField)createBoundControl(employeePage, "address.streetAddress1");
        field.setText("12345 Some Lane");
        employeePage.commit();
        Employee emp = (Employee)employeePage.getFormObject();
        assertTrue(emp.getAddress().getStreetAddress1().equals("12345 Some Lane"));
    }

//    // TODO: this fails right now - we can't exactly instantiate supervisor employee
//    // arbitrarily by default on all employees - stack overflow!
//    public void testOptionalPageFormModel() {
//        fail("this fails right now - we can't exactly instantiate supervisor employee arbitrarily by default on all employees - stack overflow!");
//
//        ConfigurableFormModel employeePage = FormModelHelper.createFormModel(new Employee());
//        JTextField field = (JTextField)createBoundControl(employeePage, "supervisor.name");
//        field.setText("Don");
//        employeePage.commit();
//        Employee emp = (Employee)employeePage.getFormObject();
//        assertTrue(emp.getSupervisor().getName().equals("Don"));
//    }

    private JComponent createBoundControl(ConfigurableFormModel formModel, String property) {
        return new SwingBindingFactory(formModel).createBinding(property).getControl();
    }

//    public void testCustomPropertyEditorRegistration() {
//        DefaultPropertyEditorRegistry per = (DefaultPropertyEditorRegistry)Application.services()
//                .getPropertyEditorRegistry();
//        Employee employee = new Employee();
//        ValidatingFormModel fm = new ValidatingFormModel(employee);
////        ValueModel vm = fm.add("age");
////        assertHasNoPropertyEditor(vm);
//
//        per.setPropertyEditor(int.class, PropertyEditorA.class);
//        fm = new ValidatingFormModel(employee);
//        assertTrue(getPropertyEditor(fm.add("age")).getClass() == PropertyEditorA.class);
//
//        per.setPropertyEditor(Employee.class, "age", PropertyEditorB.class);
//        fm = new ValidatingFormModel(employee);
//        assertTrue(getPropertyEditor(fm.add("age")).getClass() == PropertyEditorB.class);
//
//        PropertyEditor pe1 = new PropertyEditorA();
//        fm = new ValidatingFormModel(employee);
//        fm.getPropertyAccessStrategy().registerCustomEditor(int.class, pe1);
//        assertTrue(getPropertyEditor(fm.add("age")) == pe1);
//
//        PropertyEditor pe2 = new PropertyEditorA();
//        fm = new ValidatingFormModel(employee);
//        fm.getPropertyAccessStrategy().registerCustomEditor("age", pe2);
//        assertTrue(getPropertyEditor(fm.add("age")) == pe2);
//
//        PropertyEditor pe3 = new PropertyEditorA();
//        employee.setPropertyEditor("age", pe3);
//        fm = new ValidatingFormModel(employee);
//        fm.getPropertyAccessStrategy().registerCustomEditor("age", pe3);
//        assertTrue(getPropertyEditor(fm.add("age")) == pe3);
//    }
//
//    private void assertHasNoPropertyEditor(ValueModel vm) {
//        ValueModel wrappedModel = (ValueModel)getFieldValue(vm, "wrappedModel");
//        assertTrue(wrappedModel instanceof BufferedValueModel);
//    }
//
//    private PropertyEditor getPropertyEditor(ValueModel vm) {
//        ValueModel wrappedModel = (ValueModel)getFieldValue(vm, "wrappedModel");
//        assertTrue(wrappedModel instanceof TypeConverter);
//        Closure c = (Closure)getFieldValue(wrappedModel, "convertFrom");
//        PropertyEditor pe = (PropertyEditor)getFieldValue(c, "val$propertyEditor");
//        return pe;
//    }
//
//    private Object getFieldValue(Object object, String fieldName) {
//        Class clazz = object.getClass();
//        Field field = null;
//        do {
//            try {
//                field = clazz.getDeclaredField(fieldName);
//            }
//            catch (NoSuchFieldException e) {
//                clazz = clazz.getSuperclass();
//            }
//        }
//        while (field == null && clazz != null);
//        assertNotNull("unable to find field", field);
//        try {
//            field.setAccessible(true);
//            return field.get(object);
//        }
//        catch (Exception e) {
//            fail("unable to access field [" + fieldName + "]. " + e.getMessage());
//            return null;
//        }
//    }

    public static class PropertyEditorA extends PropertyEditorSupport {
    }

    public static class PropertyEditorB extends PropertyEditorSupport {
    }
}