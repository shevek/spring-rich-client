/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.binding.form;

import java.util.Date;

import javax.swing.JTextField;

import junit.framework.TestCase;

import org.springframework.binding.form.support.CompoundFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.DefaultPropertyEditorRegistry;
import org.springframework.richclient.application.config.BeanFactoryApplicationAdvisor;
import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.util.ToStringBuilder;

/**
 * 
 * @author HP
 */
public class FormModelTest extends TestCase {

    static {
        Application application = new Application(new ApplicationServices(),
                new BeanFactoryApplicationAdvisor());
        Application.services().setPropertyEditorRegistry(
                new DefaultPropertyEditorRegistry());
        Application.services().setApplicationContext(new StaticApplicationContext());
    }

    public static class Employee {
        private String name;

        private Employee supervisor;

        private Address address = new Address();

        private int age;

        private Date hireDate;

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

        public String toString() {
            return new ToStringBuilder(this).appendProperties().toString();
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

        public String toString() {
            return new ToStringBuilder(this).appendProperties().toString();
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
        ConfigurableFormModel supervisorModel = formModel.createChild(
                "supervisorPage", "supervisor");
        SwingFormModel supervisorPage = new SwingFormModel(supervisorModel);
        JTextField field = (JTextField)supervisorPage
                .createBoundControl("name");
        assertTrue(field.getText().equals(""));
        field.setText("Don");
        ValueModel name = supervisorPage.getValueModel("name");
        assertTrue(name.getValue().equals("Don"));
        supervisorPage.setEnabled(true);
        supervisorPage.commit();
        Employee emp = (Employee)formModel.getFormObject();
        assertTrue(emp.getSupervisor().getName().equals("Don"));
    }

    public void testNestedCompoundFormModel() {
        CompoundFormModel formModel = new CompoundFormModel(new Employee());
        NestingFormModel address = formModel.createCompoundChild("AddressForm",
                "address");
        SwingFormModel countryPage = new SwingFormModel(address.createChild(
                "CountryForm", "country"));
        JTextField field = (JTextField)countryPage.createBoundControl("name");
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

    public void testPageFormModel() {
        SwingFormModel employeePage = SwingFormModel
                .createFormModel(new Employee());
        JTextField field = (JTextField)employeePage
                .createBoundControl("address.streetAddress1");
        field.setText("12345 Some Lane");
        employeePage.commit();
        Employee emp = (Employee)employeePage.getFormObject();
        assertTrue(emp.getAddress().getStreetAddress1().equals(
                "12345 Some Lane"));
    }

    // this fails right now - we can't exactly instantiate supervisor employee
    // abitrarily by default on all employees - stack overflow!
    public void testOptionalPageFormModel() {
        fail("this fails right now - we can't exactly instantiate supervisor employee abitrarily by default on all employees - stack overflow!");
        
        SwingFormModel employeePage = SwingFormModel
                .createFormModel(new Employee());
        JTextField field = (JTextField)employeePage
                .createBoundControl("supervisor.name");
        field.setText("Don");
        employeePage.commit();
        Employee emp = (Employee)employeePage.getFormObject();
        assertTrue(emp.getSupervisor().getName().equals("Don"));
    }

}