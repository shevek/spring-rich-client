package org.springframework.richclient.application.support;

import java.util.HashMap;

import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.support.DefaultValueChangeDetector;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.application.ServiceNotFoundException;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.test.SpringRichTestCase;
import org.springframework.rules.RulesSource;

/**
 * Test cases for {@link DefaultApplicationServices}
 * 
 * @author Larry Streepy
 * 
 */
public class DefaultApplicationServicesTests extends SpringRichTestCase {

    public void testRegisteredServiceIsReturned() {
        ValueChangeDetector vcd = new DefaultValueChangeDetector();
        getApplicationServices().setValueChangeDetector(vcd);
        assertSame("Expected same object back", vcd, getApplicationServices().getService(ValueChangeDetector.class));

        MessageSource msrc = new StaticMessageSource();
        getApplicationServices().setMessageSource(msrc);
        assertSame("Expected same object back", msrc, getApplicationServices().getService(MessageSource.class));
    }

    public void testUnknownServiceFails() {
        try {
            getApplicationServices().getService(getClass());
            fail("Unknown service should have caused an exception");
        } catch( ServiceNotFoundException e ) {
            ; // expected
        }
    }

    public void testSetRegistryEntries() {
        ValueChangeDetector vcd = new DefaultValueChangeDetector();
        MessageSource msrc = new StaticMessageSource();

        HashMap entries = new HashMap();
        entries.put("org.springframework.binding.value.ValueChangeDetector", vcd);
        entries.put("org.springframework.context.MessageSource", msrc);

        getApplicationServices().setRegistryEntries(entries);

        assertSame("Expected same object back", vcd, getApplicationServices().getService(ValueChangeDetector.class));
        assertSame("Expected same object back", msrc, getApplicationServices().getService(MessageSource.class));
    }

    public void testDefaultServicesImplementInterface() {
        Object rulesSource = getApplicationServices().getService(RulesSource.class);
        assertTrue("Returned service must implement service type", rulesSource instanceof RulesSource);

        Object iconSource = getApplicationServices().getService(IconSource.class);
        assertTrue("Returned service must implement service type", iconSource instanceof IconSource);
    }
}
