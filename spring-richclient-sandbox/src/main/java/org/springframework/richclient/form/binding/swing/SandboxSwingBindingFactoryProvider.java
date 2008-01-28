package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.BindingFactoryProvider;

/**
 * This provider constructs instances of {@link SandboxSwingBindingFactory} on
 * demand.
 * 
 * @author Larry Streepy
 * @see org.springframework.richclient.application.ApplicationServices#getBindingFactory(FormModel)
 * @see org.springframework.richclient.application.ApplicationServices#getBindingFactoryProvider()
 * 
 * <p>
 * In order to use this factory, specify the
 * {@link SandboxSwingBindingFactoryProvider} as the binding factory provider in
 * the application context. Like this:
 * 
 * <pre>
 *      &lt;bean id=&quot;bindingFactoryProvider&quot; class=&quot;org.springframework.richclient.form.binding.swing.SandboxSwingBindingFactoryProvider&quot; /&gt;
 * </pre>
 * 
 * @author Larry Streepy
 */
public class SandboxSwingBindingFactoryProvider implements BindingFactoryProvider {

    /**
     * Produce a BindingFactory using the provided form model.
     * 
     * @param formModel Form model on which to construct the BindingFactory
     * @return BindingFactory
     */
    public BindingFactory getBindingFactory( FormModel formModel ) {
        return new SandboxSwingBindingFactory((ConfigurableFormModel) formModel);
    }

}
