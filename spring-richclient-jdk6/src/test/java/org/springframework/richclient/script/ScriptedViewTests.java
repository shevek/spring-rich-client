package org.springframework.richclient.script;

import java.io.Reader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.JComponent;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.core.io.ByteArrayResource;

public class ScriptedViewTests extends TestCase {
    public void testScriptIsMandatory() throws Exception {
        ScriptedView scriptedView = new ScriptedView();

        try {
            scriptedView.afterPropertiesSet();
            fail("Must throw exception");
        }
        catch (IllegalArgumentException e) {
            // test passes
        }
    }

    public void testHappyPath() throws Exception {
        final ScriptEngine engine = EasyMock.createMock(ScriptEngine.class);

        ScriptedView scriptedView = new ScriptedView() {
            @Override
            protected ScriptEngine createScriptEngine() {
                return engine;
            }
        };

        EasyMock.expect(engine.createBindings()).andReturn(new SimpleBindings());
        engine.setContext((ScriptContext) EasyMock.anyObject());
        EasyMock.expect(engine.eval((Reader) EasyMock.anyObject())).andReturn(null);
        EasyMock.replay(engine);

        scriptedView.setEngineName("test-engine");
        scriptedView.setScript(new ByteArrayResource("test".getBytes(), "test script"));

        JComponent control = scriptedView.createControl();
        System.out.println(control);

        EasyMock.verify(engine);
    }

    public void testScriptThrowsException() throws Exception {
        final ScriptEngine engine = EasyMock.createMock(ScriptEngine.class);

        ScriptedView scriptedView = new ScriptedView() {
            @Override
            protected ScriptEngine createScriptEngine() {
                return engine;
            }
        };

        EasyMock.expect(engine.createBindings()).andReturn(new SimpleBindings());
        engine.setContext((ScriptContext) EasyMock.anyObject());
        EasyMock.expect(engine.eval((Reader) EasyMock.anyObject())).andThrow(new ScriptException("test exception"));
        EasyMock.replay(engine);

        scriptedView.setEngineName("test-engine");
        scriptedView.setScript(new ByteArrayResource("test".getBytes(), "test script"));

        try {
            scriptedView.createControl();
            fail("must throw ScriptExecutionException");
        }
        catch (ScriptExecutionException e) {
            // test passes
        }

        EasyMock.verify(engine);
    }
}
