package org.springframework.richclient.application.support;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.springframework.richclient.application.PageLayoutBuilder;

import junit.framework.TestCase;

public class MultiViewPageDescriptorTests extends TestCase {
    public void testBuildInitialLayout() {
        MultiViewPageDescriptor pageDescriptor = new MultiViewPageDescriptor();

        List descriptors = new ArrayList();
        descriptors.add("view0");
        descriptors.add("view1");
        descriptors.add("view2");
        descriptors.add("view3");

        pageDescriptor.setViewDescriptors(descriptors);
        assertSame(descriptors, pageDescriptor.getViewDescriptors());

        PageLayoutBuilder mockBuilder = (PageLayoutBuilder) EasyMock.createMock(PageLayoutBuilder.class);
        // expectations
        mockBuilder.addView("view0");
        mockBuilder.addView("view1");
        mockBuilder.addView("view2");
        mockBuilder.addView("view3");
        EasyMock.replay(mockBuilder);

        pageDescriptor.buildInitialLayout(mockBuilder);

        EasyMock.verify(mockBuilder);
    }
    
    public void testBeanAware() {
        MultiViewPageDescriptor pageDescriptor = new MultiViewPageDescriptor();
        
        pageDescriptor.setBeanName("bean name");
        
        assertEquals("the bean name must be set as id", "bean name", pageDescriptor.getId());
    }
}
