/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.util;

import java.awt.Window;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 * An abstract superclass for an executor of a GUI Action that takes a long time to complete.  
 * <p>
 * The action worker uses the SwingWorker class to run the action in its own thread.  If the
 * operation executing in the worker's doInvoke() thread throws an exception, the ActionWorker
 * will display a formatted error message.
 * <p>
 * Subclasses must override the doInvoke() operation which should perform the time-consuming
 * operation.
 *
 * @author  Keith Donald
 * @see     SwingWorker
 */
public abstract class ActionWorker {
	private SwingWorker worker;
	private Exception error;

	/**
	 * Construct an action worker in the context of a parent window with a localized
	 * resource bundle.
	 *
	 * @param action The swing Action that controls when the worker should be started.
	 * @param parent the parent window.
	 * @param resources the resource bundle.
	 */
	public ActionWorker(
		final Action action,
		final Window parent,
		final ResourceBundle resources) {
		this.worker = new SwingWorker() {
			/* Note: this method is executed in a spawned background thread */
			public Object construct() {
				try {
					return doInvoke();
				} catch (Exception e) {
					error = e;
					return e;
				}
			}
			/* Note: this method is automatically executed in the GUI thread */
			public void finished() {
				if (error != null) {
					JOptionPane.showMessageDialog(
						parent,
						MessageFormat.format(
							resources.getString(
								"error.operation.execute.message"),
							new Object[] {
								action.getValue(Action.NAME),
								error }),
						resources.getString("error.operation.execute.title"),
						JOptionPane.ERROR_MESSAGE);
					action.setEnabled(true);
				}
			}
		};
	}

    /**
     * Returns the SwingWorker for if the caller needs access to the action return object.
     * @return the enclosed swing worker.
     * @see SwingWorker#get
     */
    public SwingWorker getSwingWorker() {
        return worker;
    }
    
	/**
	 * Cause the action worker to execute the time-consuming operation.
	 */
	public void start() {
		this.worker.start();
	}

	/**
	 * Template method: performs the time consuming operation.
	 * <p>
	 * Subclasses must implement this method.  It is automatically called in another thread
     * by the enclosed SwingWorker.
	 *
	 * @return a return object (if any)
	 * @throws Exception if the action failed to execute.
	 */
	public abstract Object doInvoke() throws Exception;

}
