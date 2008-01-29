/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.script;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link View} implementation that uses {@link ScriptEngine} to build its control.
 * 
 * @author Peter De Bruycker
 */
public class ScriptedView extends AbstractView implements InitializingBean {
    private Resource script;
    private String engineName;
    private Map<String, Object> scriptBindings;
    private String viewBindingName;
    private String containerBindingName;

    protected JComponent createControl() {
        JPanel container = new JPanel(new BorderLayout());

        ScriptEngine engine = createScriptEngine();

        Bindings bindings = engine.createBindings();
        populateBindings(bindings, container);

        ScriptContext context = new SimpleScriptContext();
        // Bug workaround
        context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        engine.setContext(context);

        try {
            engine.eval(new InputStreamReader(script.getInputStream()));
        }
        catch (ScriptException e) {
            throw new ScriptExecutionException("error running script", e);
        }
        catch (IOException e) {
            throw new ScriptIOException("error reading script", e);
        }

        return container;
    }

    /**
     * Creates the <code>ScriptEngine</code>, by using the {@link #engineName} if provided. If no engine name is set,
     * the extension of the file name of the {@link #script} is used.
     * 
     * @return the <code>ScriptEngine</code>
     * 
     * @see ScriptEngineManager#getEngineByName(String)
     * @see ScriptEngineManager#getEngineByExtension(String)
     */
    protected ScriptEngine createScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager(getClass().getClassLoader());

        if (StringUtils.hasText(engineName)) {
            return manager.getEngineByName(engineName);
        }

        return manager.getEngineByExtension(getExtension(script.getFilename()));
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Populates the bindings that will be passed to the script.
     * <p>
     * If the {@link #containerBindingName} is set, the container instance will be included in the bindings.
     * <p>
     * If the {@link #viewBindingName} is set, the view instance will be included in the bindings.
     * <p>
     * All the variables in the {@link #scriptBindings} will also be included in the bindings.
     * 
     * @param bindings
     *            the bindings
     * @param container
     *            the compontent that will be passed into the script
     * 
     * @see #setContainerBindingName(String)
     * @see #setViewBindingName(String)
     * @see #setScriptBindings(Map)
     */
    protected void populateBindings(Bindings bindings, JComponent container) {
        if (StringUtils.hasText(containerBindingName)) {
            bindings.put(containerBindingName, container);
        }
        if (StringUtils.hasText(viewBindingName)) {
            bindings.put(viewBindingName, this);
        }

        if (this.scriptBindings != null) {
            for (String key : scriptBindings.keySet()) {
                bindings.put(key, scriptBindings.get((key)));
            }
        }
    }

    public void setScript(Resource script) {
        this.script = script;
    }

    /**
     * Sets the name of the engine to be created. This name will be used to create the engine.
     * 
     * @param name
     *            the name
     * 
     * @see ScriptEngineManager#getEngineByName(String)
     */
    public void setEngineName(String name) {
        engineName = name;
    }

    /**
     * Set the bindings to be passed to the script.
     * 
     * @param bindings
     *            the bindings
     */
    public void setScriptBindings(Map<String, Object> bindings) {
        // be nice and take a copy
        this.scriptBindings = new HashMap<String, Object>(bindings);
    }

    /**
     * Sets the view binding name.
     * 
     * @param viewBindingName
     *            the name
     * @see #populateBindings(Bindings, JComponent) for more details
     */
    public void setViewBindingName(String viewBindingName) {
        this.viewBindingName = viewBindingName;
    }

    /**
     * Sets the container binding name.
     * 
     * @param containerBindingName
     *            the name
     * @see #populateBindings(Bindings, JComponent) for more details
     */
    public void setContainerBindingName(String containerBindingName) {
        this.containerBindingName = containerBindingName;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(script, "script must be set");
    }
}
