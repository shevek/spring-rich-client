package org.springframework.richclient.command.support;

import javax.swing.AbstractButton;

import org.springframework.binding.value.support.EqualsValueChangeDetector;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.test.SpringRichTestCase;


public class ButtonEnablingTests extends SpringRichTestCase
{
    
    /**
     * May be implemented in subclasses that need to register services with the global
     * application services instance.
     */
    protected void registerAdditionalServices( DefaultApplicationServices applicationServices ) {
        applicationServices.setValueChangeDetector(new EqualsValueChangeDetector());
    }

    public void testButtonEnabling()
    {
        TestCommand testCommand = new TestCommand();
        AbstractButton button = testCommand.createButton();
        AbstractButton button2 = testCommand.createButton();
        enableTests(testCommand, new Object[]{button, button2});
    }
    
    public void enableTests(AbstractCommand command, Object[] buttons)
    {
        enableTest(command, buttons, false);
        enableTest(command, buttons, true);
    }
    
    public void enableTest(AbstractCommand command, Object[] buttons, boolean enable)
    {
        command.setEnabled(enable);
        assertEquals(command.isEnabled(), enable);
        for (int i = 0; i < buttons.length; ++i)
        {
            assertEquals(((AbstractButton)buttons[i]).isEnabled(), enable);    
        }        
    }
    
    // testing different sequences because error was in sequence of hashmap:
    public void testMultipleFaceDescriptorsEnabling()
    {
        multipleFaceDescriptorSequence("face1", "face2", "face3");
        multipleFaceDescriptorSequence("face1", "face3", "face2");

        multipleFaceDescriptorSequence("face2", "face1", "face3");
        multipleFaceDescriptorSequence("face2", "face3", "face1");

        multipleFaceDescriptorSequence("face3", "face2", "face1");
        multipleFaceDescriptorSequence("face3", "face1", "face2");
    }

    private void multipleFaceDescriptorSequence(String face1, String face2, String face3)
    {
        TestCommand testCommand = new TestCommand();
        registerCommandFaceDescriptor(face1, testCommand);
        registerCommandFaceDescriptor(face2, testCommand);
        registerCommandFaceDescriptor(face3, testCommand);

        AbstractButton face1button = testCommand.createButton(face1);
        enableTests(testCommand, new Object[]{face1button});
        
        AbstractButton face2button = testCommand.createButton(face2);
        enableTests(testCommand, new Object[]{face1button, face2button});
        
        AbstractButton face3button = testCommand.createButton(face3);
        enableTests(testCommand, new Object[]{face1button, face2button, face3button});
    }
    
    private void registerCommandFaceDescriptor(String faceId, AbstractCommand command)
    {
        CommandFaceDescriptor face = new CommandFaceDescriptor();
        ApplicationObjectConfigurer configurer = (ApplicationObjectConfigurer)ApplicationServicesLocator.services().getService(ApplicationObjectConfigurer.class);
        configurer.configure(face, faceId);
        command.setFaceDescriptor(faceId, face);        
    }
}
