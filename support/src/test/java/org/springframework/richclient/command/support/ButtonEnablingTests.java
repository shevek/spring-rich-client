package org.springframework.richclient.command.support;

import javax.swing.AbstractButton;

import org.springframework.binding.value.support.EqualsValueChangeDetector;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.config.CommandFaceDescriptor;

import junit.framework.TestCase;


public class ButtonEnablingTests extends TestCase
{
    
    protected void setUp() throws Exception
    {
        // load application
        Application.load(null);
        new Application(new DefaultApplicationLifecycleAdvisor());
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        Application.services().setApplicationContext(applicationContext);
        Application.services().setValueChangeDetector(new EqualsValueChangeDetector());
        applicationContext.refresh();    
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
        Application.services().configure(face, faceId);
        command.setFaceDescriptor(faceId, face);        
    }
}
