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
package org.springframework.richclient.application;

import java.util.Locale;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.progress.NullProgressMonitor;
import org.springframework.richclient.progress.ProgressMonitor;

/**
 * This class provides a suite of unit tests for the
 * {@link ProgressMonitoringBeanFactoryPostProcessor}.
 * 
 * @author Kevin Stembridge
 * @since 0.3.0
 * 
 */
public class ProgressMonitoringBeanFactoryPostProcessorTests extends TestCase {

	/**
	 * Confirms that the post-processor's constructor throws an
	 * IllegalArgumentException if a ProgressMonitor is not provided, but allows
	 * a null MessageSource.
	 */
	public void testConstructor() {

		try {
			new ProgressMonitoringBeanFactoryPostProcessor(null, null);
			fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

		new ProgressMonitoringBeanFactoryPostProcessor(new NullProgressMonitor(), null);

	}

	/**
	 * Confirms that the post processor correctly notifies a given progress
	 * monitor as the bean factory is loaded, providing the expected localized
	 * messages. The following assertions are made:
	 * 
	 * <ul>
	 * <li>The {@link ProgressMonitor#taskStarted(String, int)} method is
	 * called exactly once with a localized message, provided by the key
	 * {@link ProgressMonitoringBeanPostProcessor#LOADING_APP_CONTEXT_KEY}, and
	 * the number of singleton beans in the bean factory.</li>
	 * <li>The {@link ProgressMonitor#subTaskStarted(String)} method is called,
	 * with the localized message provided by
	 * {@link ProgressMonitoringBeanPostProcessor#LOADING_BEAN_KEY}, for each
	 * singleton bean defined in the bean factory being loaded.</li>
	 * <li>The {@link ProgressMonitor#worked(int)} method is called with the
	 * argument '1' the same number of times as there are singleton beans
	 * defined in the bean factory.</li>
	 * </ul>
	 */
	public void testLoadingBeansWithMessageSource() {
		String loadingAppCtxMessage = "Loading Application Context Message Test";
		int expectedSingletonBeanCount = 2;
		String beanName1 = "beanName1";
		String beanName2 = "beanName2";
		String beanName3 = "beanName3";
		String loadingBeanMessage = "LoadBeanTestMessage {0}";
		String expectedLoadBean1Message = "LoadBeanTestMessage beanName1";
		String expectedLoadBean2Message = "LoadBeanTestMessage beanName2";

		StaticApplicationContext appCtx = new StaticApplicationContext();
		appCtx.registerSingleton(beanName1, Object.class);
		appCtx.registerSingleton(beanName2, Object.class);
		appCtx.registerPrototype(beanName3, Object.class);

		StaticMessageSource messageSource = new StaticMessageSource();

		messageSource.addMessage(ProgressMonitoringBeanFactoryPostProcessor.LOADING_APP_CONTEXT_KEY, Locale
				.getDefault(), loadingAppCtxMessage);

		messageSource.addMessage(ProgressMonitoringBeanFactoryPostProcessor.LOADING_BEAN_KEY, Locale.getDefault(),
				loadingBeanMessage);

		ProgressMonitor mockProgressMonitor = (ProgressMonitor) EasyMock.createStrictMock(ProgressMonitor.class);
		mockProgressMonitor.taskStarted(loadingAppCtxMessage, expectedSingletonBeanCount);
		mockProgressMonitor.subTaskStarted(expectedLoadBean1Message);
		mockProgressMonitor.worked(1);
		mockProgressMonitor.subTaskStarted(expectedLoadBean2Message);
		mockProgressMonitor.worked(1);
		EasyMock.replay(mockProgressMonitor);

		ProgressMonitoringBeanFactoryPostProcessor processor = new ProgressMonitoringBeanFactoryPostProcessor(
				mockProgressMonitor, messageSource);

		appCtx.addBeanFactoryPostProcessor(processor);

		appCtx.refresh();

		EasyMock.verify(mockProgressMonitor);
	}

	/**
	 * Confirms that the post processor correctly notifies a given progress
	 * monitor as the bean factory is loaded. The following assertions are made:
	 * 
	 * <ul>
	 * <li>The {@link ProgressMonitor#taskStarted(String, int)} method is
	 * called exactly once with any message and the number of singleton beans in
	 * the bean factory.</li>
	 * <li>The {@link ProgressMonitor#subTaskStarted(String)} method is called,
	 * with the localized message provided by
	 * {@link ProgressMonitoringBeanPostProcessor#LOADING_BEAN_KEY}, for each
	 * singleton bean defined in the bean factory being loaded.</li>
	 * <li>The {@link ProgressMonitor#worked(int)} method is called with the
	 * argument '1' the same number of times as there are singleton beans
	 * defined in the bean factory.</li>
	 * </ul>
	 */
	public void testLoadingBeansWithoutMessageSource() {
		int expectedSingletonBeanCount = 2;
		String beanName1 = "beanName1";
		String beanName2 = "beanName2";
		String beanName3 = "beanName3";

		StaticApplicationContext appCtx = new StaticApplicationContext();
		appCtx.registerSingleton(beanName1, Object.class);
		appCtx.registerSingleton(beanName2, Object.class);
		appCtx.registerPrototype(beanName3, Object.class);

		ProgressMonitor mockProgressMonitor = (ProgressMonitor) EasyMock.createStrictMock(ProgressMonitor.class);
		mockProgressMonitor.taskStarted("Loading Application Context ...", expectedSingletonBeanCount);
		mockProgressMonitor.subTaskStarted("Loading " + beanName1 + " ...");
		mockProgressMonitor.worked(1);
		mockProgressMonitor.subTaskStarted("Loading " + beanName2 + " ...");
		mockProgressMonitor.worked(1);
		EasyMock.replay(mockProgressMonitor);

		ProgressMonitoringBeanFactoryPostProcessor processor = new ProgressMonitoringBeanFactoryPostProcessor(
				mockProgressMonitor, null);
		appCtx.addBeanFactoryPostProcessor(processor);

		appCtx.refresh();

		EasyMock.verify(mockProgressMonitor);
	}

}
