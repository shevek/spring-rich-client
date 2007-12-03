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
package org.springframework.richclient.application.config;

import java.awt.Image;
import java.util.Locale;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ServiceNotFoundException;
import org.springframework.richclient.command.config.CommandButtonIconInfo;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.richclient.command.config.CommandIconConfigurable;
import org.springframework.richclient.command.config.CommandLabelConfigurable;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.LabelConfigurable;
import org.springframework.richclient.core.LabelInfo;
import org.springframework.richclient.core.SecurityControllable;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;
import org.springframework.richclient.image.config.IconConfigurable;
import org.springframework.richclient.image.config.ImageConfigurable;
import org.springframework.richclient.security.SecurityController;
import org.springframework.richclient.security.SecurityControllerManager;
import org.springframework.richclient.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The default implementation of the {@link ApplicationObjectConfigurer}
 * interface.
 * 
 * This class makes use of several application services in order to determine
 * the property values to be applied to objects being configured. For example,
 * some string properties will be retrieved from the application's message
 * resource bundle using a {@link MessageSource}. To configure an object with
 * images and icons, an {@link ImageSource} and {@link IconSource} respectively
 * will be used. Subclasses can modify this behaviour by overriding the
 * {@link #configure(Object, String)} method but it may be more convenient to
 * override some of the various methods that deal specificly with objects that
 * implement certain 'configurable' interfaces, such as
 * {@link LabelConfigurable} or {@link TitleConfigurable}. See the javadoc of
 * the {@link #configure(Object, String)} method for more details.
 * 
 * 
 * @author Keith Donald
 * @author Kevin Stembridge
 */
public class DefaultApplicationObjectConfigurer implements ApplicationObjectConfigurer, BeanPostProcessor {

	/**
	 * The key fragment used to retrieve the <i>pressed</i> icon for a given
	 * object.
	 */
	public static final String PRESSED_ICON_KEY = "pressedIcon";

	/**
	 * The key fragment used to retrieve the <i>disabled</i> icon for a given
	 * object.
	 */
	public static final String DISABLED_ICON_KEY = "disabledIcon";

	/**
	 * The key fragment used to retrieve the <i>rollover</i> icon for a given
	 * object.
	 */
	public static final String ROLLOVER_ICON_KEY = "rolloverIcon";

	/**
	 * The key fragment used to retrieve the <i>selected</i> icon for a given
	 * object.
	 */
	public static final String SELECTED_ICON_KEY = "selectedIcon";

	/** The key fragment used to retrieve the icon for a given object. */
	public static final String ICON_KEY = "icon";

	/** The key fragment used to retrieve the image for a given object. */
	public static final String IMAGE_KEY = "image";

	/** The key fragment used to retrieve the description for a given object. */
	public static final String DESCRIPTION_KEY = "description";

	/** The key fragment used to retrieve the caption for a given object. */
	public static final String CAPTION_KEY = "caption";

	/** The key fragment used to retrieve the title for a given object. */
	public static final String TITLE_KEY = "title";

	/** Class logger, available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean loadOptionalIcons = true;

	private MessageSource messageSource;

	private ImageSource imageSource;

	private IconSource iconSource;

	private SecurityControllerManager securityControllerManager;

	/**
	 * Creates a new {@code DefaultApplicationObjectConfigurer} that will obtain
	 * required services from the application services locator.
	 */
	public DefaultApplicationObjectConfigurer() {
		// do nothing
	}

	/**
	 * Creates a new {@code DefaultApplicationObjectConfigurer} that will use
	 * the given message source. Other application services will be retrieved
	 * using the application services locator.
	 * 
	 * @param messageSource The message source. May be null.
	 */
	public DefaultApplicationObjectConfigurer(MessageSource messageSource) {
		this(messageSource, null, null, null);
	}

	/**
	 * Creates a new {@code DefaultApplicationObjectConfigurer} that will use
	 * the given message and image sources. Other application services will be
	 * retrieved using the application services locator.
	 * 
	 * @param messageSource The message source. May be null.
	 * @param imageSource The image source. May be null.
	 */
	public DefaultApplicationObjectConfigurer(MessageSource messageSource, ImageSource imageSource) {
		this(messageSource, imageSource, null, null);
	}

	/**
	 * Creates a new {@code DefaultApplicationObjectConfigurer} that will use
	 * the given message, image and icon sources. If any of these services are
	 * null, they will be retrieved using the application services locator.
	 * 
	 * @param messageSource The message source. May be null.
	 * @param imageSource The image source. May be null.
	 * @param iconSource The icon source. May be null.
	 * @param securityControllerManager The security controller manager. May be
	 * null.
	 */
	public DefaultApplicationObjectConfigurer(MessageSource messageSource, ImageSource imageSource,
			IconSource iconSource, SecurityControllerManager securityControllerManager) {

		this.messageSource = messageSource;
		this.imageSource = imageSource;
		this.iconSource = iconSource;
		this.securityControllerManager = securityControllerManager;

	}

	/**
	 * Sets the flag that determines if optional icons will be loaded for any
	 * {@link CommandIconConfigurable} objects that are configured by this
	 * instance. The default is true.
	 * 
	 * @param loadOptionalIcons The flag to load optional options.
	 */
	public void setLoadOptionalIcons(boolean loadOptionalIcons) {
		this.loadOptionalIcons = loadOptionalIcons;
	}

	/**
	 * Returns this instance's message source. If a source was not provided at
	 * construction, it will be retrieved by the application services locator.
	 * 
	 * @return The message source, never null.
	 * 
	 * @throws ServiceNotFoundException if a source was not provided at
	 * construction time and the application services locator cannot find an
	 * instance of a message source.
	 */
	protected MessageSource getMessageSource() {

		if (messageSource == null) {
			messageSource = (MessageSource) ApplicationServicesLocator.services().getService(MessageSource.class);
		}

		return messageSource;

	}

	/**
	 * Returns this instance's icon source. If a source was not provided at
	 * construction, it will be retrieved by the application services locator.
	 * 
	 * @return The icon source, never null.
	 * 
	 * @throws ServiceNotFoundException if a source was not provided at
	 * construction time and the application services locator cannot find an
	 * instance of an icon source.
	 */
	protected IconSource getIconSource() {

		if (iconSource == null) {
			iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
		}

		return iconSource;

	}

	/**
	 * Returns this instance's image source. If a source was not provided at
	 * construction, it will be retrieved by the application services locator.
	 * 
	 * @return The image source, never null.
	 * 
	 * @throws ServiceNotFoundException if a source was not provided at
	 * construction time and the application services locator cannot find an
	 * instance of an image source.
	 */
	protected ImageSource getImageSource() {

		if (imageSource == null) {
			imageSource = (ImageSource) ApplicationServicesLocator.services().getService(ImageSource.class);
		}

		return imageSource;

	}

	/**
	 * Returns this instance's security controller manager. If the security
	 * manager was not provided at construction, it will be retrieved by the
	 * application services locator.
	 * 
	 * @return The security controller manager, never null.
	 * 
	 * @throws ServiceNotFoundException if a security controller manager was not
	 * provided at construction time and the application services locator cannot
	 * find an instance of the service.
	 */
	protected SecurityControllerManager getSecurityControllerManager() {

		if (securityControllerManager == null) {
			securityControllerManager = (SecurityControllerManager) ApplicationServicesLocator.services().getService(
					SecurityControllerManager.class);
		}

		return securityControllerManager;

	}

	/**
	 * Configures the given object according to the interfaces that it
	 * implements.
	 * 
	 * <p>
	 * This implementation forwards the object to the following overridable
	 * methods in the order listed. Subclasses can use these methods as hooks to
	 * modify the default configuration behaviour without having to override
	 * this method entirely.
	 * 
	 * <ul>
	 * <li>{@link #configureTitle(TitleConfigurable, String)}</li>
	 * <li>{@link #configureLabel(LabelConfigurable, String)}</li>
	 * <li>{@link #configureCommandLabel(CommandLabelConfigurable, String)}</li>
	 * <li>{@link #configureDescription(DescriptionConfigurable, String)}</li>
	 * <li>{@link #configureImage(ImageConfigurable, String)}</li>
	 * <li>{@link #configureIcon(IconConfigurable, String)}</li>
	 * <li>{@link #configureCommandIcons(CommandIconConfigurable, String)}</li>
	 * <li>{@link #configureSecurityController(SecurityControllable, String)}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param object The object to be configured. May be null.
	 * @param objectName The name for the object, expected to be unique within
	 * the application. If {@code object} is not null, then {@code objectName}
	 * must also be non-null.
	 * 
	 * @throws IllegalArgumentException if {@code object} is not null, but
	 * {@code objectName} is null.
	 * 
	 */
	public void configure(Object object, String objectName) {

		if (object == null) {
			logger.debug("object to be configured is null");
			return;
		}

		Assert.required(objectName, "objectName");

		if (object instanceof TitleConfigurable) {
			configureTitle((TitleConfigurable) object, objectName);
		}

		if (object instanceof LabelConfigurable) {
			configureLabel((LabelConfigurable) object, objectName);
		}

		if (object instanceof CommandLabelConfigurable) {
			configureCommandLabel((CommandLabelConfigurable) object, objectName);
		}

		if (object instanceof DescriptionConfigurable) {
			configureDescription((DescriptionConfigurable) object, objectName);
		}

		if (object instanceof ImageConfigurable) {
			configureImage((ImageConfigurable) object, objectName);
		}

		if (object instanceof IconConfigurable) {
			configureIcon((IconConfigurable) object, objectName);
		}

		if (object instanceof CommandIconConfigurable) {
			configureCommandIcons((CommandIconConfigurable) object, objectName);
		}

		if (object instanceof SecurityControllable) {
			configureSecurityController((SecurityControllable) object, objectName);
		}

	}

	/**
	 * Sets the title of the given object. The title is loaded from this
	 * instance's {@link MessageSource} using a message code in the format
	 * 
	 * <pre>
	 *                   &lt;objectName&gt;.title
	 * </pre>
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureTitle(TitleConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		String title = loadMessage(objectName + "." + TITLE_KEY);

		if (StringUtils.hasText(title)) {
			configurable.setTitle(title);
		}
	}

	/**
	 * Sets the {@link LabelInfo} of the given object. The label info is created
	 * after loading the encoded label string from this instance's
	 * {@link MessageSource} using a message code in the format
	 * 
	 * <pre>
	 *                   &lt;objectName&gt;.label
	 * </pre>
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureLabel(LabelConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		String labelStr = loadMessage(objectName + ".label");

		if (StringUtils.hasText(labelStr)) {
			LabelInfo labelInfo = LabelInfo.valueOf(labelStr);
			configurable.setLabelInfo(labelInfo);
		}
	}

	/**
	 * Sets the {@link CommandButtonLabelInfo} of the given object. The label
	 * info is created after loading the encoded label string from this
	 * instance's {@link MessageSource} using a message code in the format
	 * 
	 * <pre>
	 * &lt;objectName&gt;.label
	 * </pre>
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureCommandLabel(CommandLabelConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		String labelStr = loadMessage(objectName + ".label");

		if (StringUtils.hasText(labelStr)) {
			CommandButtonLabelInfo labelInfo = CommandButtonLabelInfo.valueOf(labelStr);
			configurable.setLabelInfo(labelInfo);
		}
	}

	/**
	 * Sets the description and caption of the given object. These values are
	 * loaded from this instance's {@link MessageSource} using message codes in
	 * the format
	 * 
	 * <pre>
	 * &lt;objectName&gt;.description
	 * </pre>
	 * 
	 * and
	 * 
	 * <pre>
	 * &lt;objectName&gt;.caption
	 * </pre>
	 * 
	 * respectively.
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureDescription(DescriptionConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		String caption = loadMessage(objectName + "." + CAPTION_KEY);

		if (StringUtils.hasText(caption)) {
			configurable.setCaption(caption);
		}

		String description = loadMessage(objectName + "." + DESCRIPTION_KEY);

		if (StringUtils.hasText(description)) {
			configurable.setDescription(description);
		}

	}

	/**
	 * Sets the image of the given object. The image is loaded from this
	 * instance's {@link ImageSource} using a key in the format
	 * 
	 * <pre>
	 * &lt;objectName&gt;.image
	 * </pre>
	 * 
	 * If the image source cannot find an image under that key, the object's
	 * image will be set to null.
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureImage(ImageConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		Image image = loadImage(objectName, IMAGE_KEY);

		if (image != null) {
			configurable.setImage(image);
		}
	}

	/**
	 * Sets the icon of the given object. The icon is loaded from this
	 * instance's {@link IconSource} using a key in the format
	 * 
	 * <pre>
	 * &lt;objectName&gt;.icon
	 * </pre>
	 * 
	 * If the icon source cannot find an icon under that key, the object's icon
	 * will be set to null.
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureIcon(IconConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");

		Icon icon = loadIcon(objectName, ICON_KEY);

		if (icon != null) {
			configurable.setIcon(icon);
		}
	}

	/**
	 * Sets the icons of the given object. The icons are loaded from this
	 * instance's {@link IconSource}. using a key in the format
	 * 
	 * <pre>
	 * &lt;objectName&gt;.icon
	 * </pre>
	 * 
	 * If the icon source cannot find an icon under that key, the object's icon
	 * will be set to null.
	 * 
	 * @param configurable The object to be configured. Must not be null.
	 * @param objectName The name of the configurable object, unique within the
	 * application. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	protected void configureCommandIcons(CommandIconConfigurable configurable, String objectName) {
		Assert.required(configurable, "configurable");
		Assert.required(objectName, "objectName");
		setIconInfo(configurable, objectName);
		setLargeIconInfo(configurable, objectName);
	}

	/**
	 * Associates the given object with a security controller if it implements
	 * the {@link SecurityControllable} interface.
	 * @param object The object to be configured.
	 * @param objectName The name (id) of the object.
	 * @throws BeansException if a referenced security controller is not found
	 * or is of the wrong type
	 */
	protected void configureSecurityController(SecurityControllable controllable, String objectName) {
		Assert.required(controllable, "controllable");
		Assert.required(objectName, "objectName");

		String controllerId = controllable.getSecurityControllerId();

		if (controllerId != null) {
			// Find the referenced controller.

			if (logger.isDebugEnabled()) {
				logger.debug("Lookup SecurityController with id [" + controllerId + "]");
			}

			SecurityController controller = getSecurityControllerManager().getSecurityController(controllerId);

			// And add the object to the controlled object set
			if (controller != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("configuring SecurityControllable [" + objectName + "]; security controller id='"
							+ controllerId + "'");
				}
				controller.addControlledObject(controllable);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("configuring SecurityControllable [" + objectName
							+ "]; no security controller for id='" + controllerId + "'");
				}
			}
		}
		else {

			if (logger.isDebugEnabled()) {
				logger.debug("configuring SecurityControllable [" + objectName
						+ "]; no security controller Id specified");
			}

		}

	}

	/**
	 * Sets the icons for the given object by retrieving them from this
	 * instance's {@link IconSource} and adding them to the
	 * {@link CommandButtonIconInfo} object to be set on the configurable
	 * object.
	 * 
	 * <p>
	 * The keys used to retrieve icons from the icon source are created by
	 * concatenating the given {@code objectName} with a dot (.) and then an
	 * icon type like so:
	 * </p>
	 * 
	 * <pre>
	 *                       {@code myObjectName.someIconType}.
	 * </pre>
	 * 
	 * If the {@code loadOptionalIcons} flag is set to true (it is by default)
	 * all the following icon types will be used. If the flag is false, only the
	 * first will be used:
	 * 
	 * <ul>
	 * <li>{@value #ICON_KEY}</li>
	 * <li>{@value #SELECTED_ICON_KEY}</li>
	 * <li>{@value #ROLLOVER_ICON_KEY}</li>
	 * <li>{@value #DISABLED_ICON_KEY}</li>
	 * <li>{@value #PRESSED_ICON_KEY}</li>
	 * </ul>
	 * 
	 * @param object The object to be configured. Must not be null.
	 * @param objectName The name of the object. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	public void setIconInfo(CommandIconConfigurable object, String objectName) {

		Assert.required(object, "object");
		Assert.required(objectName, "objectName");

		Icon icon = loadIcon(objectName, ICON_KEY);

		if (icon == null) {
			return;
		}

		CommandButtonIconInfo iconInfo;

		if (loadOptionalIcons) {
			Icon selectedIcon = loadIcon(objectName, SELECTED_ICON_KEY);
			Icon rolloverIcon = loadIcon(objectName, ROLLOVER_ICON_KEY);
			Icon disabledIcon = loadIcon(objectName, DISABLED_ICON_KEY);
			Icon pressedIcon = loadIcon(objectName, PRESSED_ICON_KEY);
			iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
		}
		else {
			iconInfo = new CommandButtonIconInfo(icon);
		}

		object.setIconInfo(iconInfo);

	}

	/**
	 * Sets the large icons for the given object by retrieving them from this
	 * instance's {@link IconSource} and adding them to the
	 * {@link CommandButtonIconInfo} object to be set on the configurable
	 * object.
	 * 
	 * <p>
	 * The keys used to retrieve icons from the icon source are created by
	 * concatenating the given {@code objectName} with a dot (.), the text
	 * 'large' and then an icon type like so:
	 * </p>
	 * 
	 * <pre>
	 *                       {@code myObjectName.large.someIconType}.
	 * </pre>
	 * 
	 * If the {@code loadOptionalIcons} flag is set to true (it is by default)
	 * all the following icon types will be used. If the flag is false, only the
	 * first will be used:
	 * 
	 * <ul>
	 * <li>{@value #ICON_KEY}</li>
	 * <li>{@value #SELECTED_ICON_KEY}</li>
	 * <li>{@value #ROLLOVER_ICON_KEY}</li>
	 * <li>{@value #DISABLED_ICON_KEY}</li>
	 * <li>{@value #PRESSED_ICON_KEY}</li>
	 * </ul>
	 * 
	 * @param object The object to be configured. Must not be null.
	 * @param objectName The name of the object. Must not be null.
	 * 
	 * @throws IllegalArgumentException if either argument is null.
	 */
	public void setLargeIconInfo(CommandIconConfigurable object, String objectName) {

		Assert.required(object, "object");
		Assert.required(objectName, "objectName");

		Icon icon = loadLargeIcon(objectName, ICON_KEY);

		if (icon == null) {
			return;
		}

		CommandButtonIconInfo iconInfo;

		if (loadOptionalIcons) {
			Icon selectedIcon = loadLargeIcon(objectName, SELECTED_ICON_KEY);
			Icon rolloverIcon = loadLargeIcon(objectName, ROLLOVER_ICON_KEY);
			Icon disabledIcon = loadLargeIcon(objectName, DISABLED_ICON_KEY);
			Icon pressedIcon = loadLargeIcon(objectName, PRESSED_ICON_KEY);
			iconInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
		}
		else {
			iconInfo = new CommandButtonIconInfo(icon);
		}

		object.setLargeIconInfo(iconInfo);

	}

	/**
	 * Configures the given object.
	 * @see #configure(Object, String)
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		configure(bean, beanName);
		return bean;
	}

	/**
	 * Attempts to load the message corresponding to the given message code
	 * using this instance's {@link MessageSource} and locale.
	 * 
	 * @param messageCode The message code that will be used to retrieve the
	 * message. Must not be null.
	 * @return The message for the given code, or null if the message code could
	 * not be found.
	 * 
	 * @throws IllegalArgumentException if {@code messageCode} is null.
	 */
	private String loadMessage(String messageCode) {

		Assert.required(messageCode, "messageCode");

		if (logger.isDebugEnabled()) {
			logger.debug("Resolving label with code '" + messageCode + "'");
		}

		try {
			return getMessageSource().getMessage(messageCode, null, getLocale());
		}
		catch (NoSuchMessageException e) {

			if (logger.isInfoEnabled()) {
				logger.info("The message source is unable to find message code [" + messageCode
						+ "]. Ignoring and returning null.");
			}

			return null;
		}

	}

	/**
	 * Returns the system default locale.
	 * 
	 * @return The system default locale, never null.
	 */
	protected Locale getLocale() {
		return Locale.getDefault();
	}

	private Icon loadIcon(String objectName, String iconType) {
		String key = objectName + "." + iconType;
		return getIconSource().getIcon(key);
	}

	private Icon loadLargeIcon(String objectName, String iconType) {
		String key = objectName + ".large." + iconType;
		return getIconSource().getIcon(key);
	}

	private Image loadImage(String objectName, String imageType) {
		String key = objectName + "." + imageType;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Resolving optional image with code '" + key + "'");
			}
			return getImageSource().getImage(key);
		}
		catch (NoSuchImageResourceException e) {
			if (logger.isInfoEnabled()) {
				logger.info("Labelable object's image '" + key + "' does not exist in image bundle; continuing...");
			}
			return null;
		}
	}

	/**
	 * A default implemenation, performing no operation.
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
