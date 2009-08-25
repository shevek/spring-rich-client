package org.springframework.richclient.util;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.binding.form.FormModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.support.DataEditorWidgetViewCommand;
import org.springframework.richclient.exceptionhandling.JdicEmailNotifierErrorReporter;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.DefaultButtonFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class RcpSupport
{
    public static final String ERROR_KEY = "error";

    // standard message keys
    public static final String LABEL = "label";
    public static final String HEADER = "header";
    public static final String TEXT = "text";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String DETAIL = "detail";
    public static final String CAPTION = "caption";
    public static final String ICON = "icon";

    public static final int YES_OPTION = 0;
    public static final int OK_OPTION = 0;
    public static final int NO_OPTION = 1;
    public static final int CANCEL_OPTION = 2;

    private static MessageSourceAccessor messageSourceAccessor;
    private static CommandConfigurer commandConfigurer;
    private static ApplicationContext applicationContext;
    private static ApplicationObjectConfigurer applicationObjectConfigurer;
    private static IconSource iconSource;

    private static Map<Integer, Object[]> optionsMap;

    static
    {
        optionsMap = new HashMap<Integer, Object[]>();
        String yes = getMessage(null, "OptionPane.yesButtonText", LABEL);
        String no = getMessage(null, "OptionPane.noButtonText", LABEL);
        String cancel = getMessage(null, "OptionPane.cancelButtonText", LABEL);
        String ok = getMessage(null, "OptionPane.okButtonText", LABEL);

        optionsMap.put(Integer.valueOf(JOptionPane.DEFAULT_OPTION), new Object[]{ok});
        optionsMap.put(Integer.valueOf(JOptionPane.YES_NO_OPTION), new Object[]{yes, no});
        optionsMap.put(Integer.valueOf(JOptionPane.YES_NO_CANCEL_OPTION), new Object[]{yes, no, cancel});
        optionsMap.put(Integer.valueOf(JOptionPane.OK_CANCEL_OPTION), new Object[]{ok, cancel});
    }

    public static String[] getMessageKeys(String id, String name, String type)
    {
        boolean idNotEmpty = (id == null) || id.trim().equals("") ? false : true;
        String[] keys = new String[idNotEmpty ? 3 : 2];
        int i = 0;
        if (idNotEmpty)
        {
            keys[i++] = id + "." + name + "." + type;
        }
        keys[i++] = name + "." + type;
        keys[i] = name;
        return keys;
    }

    public static String getMessage(String id, String name, String type)
    {
        String[] messageKeys = getMessageKeys(id, name, type);
        if (messageSourceAccessor == null)
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);

        return messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(messageKeys, null,
                messageKeys[messageKeys.length - 1]));
    }

    public static String getMessage(MessageSourceResolvable msr)
    {
        if (messageSourceAccessor == null)
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);

        return messageSourceAccessor.getMessage(msr);
    }

    public static String getMessage(String id)
    {
        if (messageSourceAccessor == null)
            messageSourceAccessor = (MessageSourceAccessor) ApplicationServicesLocator.services().getService(
                    MessageSourceAccessor.class);

        return messageSourceAccessor.getMessage(id, "");
    }

    public static String getMessage(String id, String name, String type, Object[] params)
    {
        String message = getMessage(id, name, type);
        if (params != null)
            return MessageFormat.format(message, params);
        return message;
    }

    public static void configure(AbstractCommand command)
    {
        if (commandConfigurer == null)
            commandConfigurer = (CommandConfigurer) ApplicationServicesLocator.services().getService(
                    CommandConfigurer.class);
        commandConfigurer.configure(command);
    }

    public static void configure(Object object, String id)
    {
        if (applicationObjectConfigurer == null)
            applicationObjectConfigurer = (ApplicationObjectConfigurer) ApplicationServicesLocator.services()
                    .getService(ApplicationObjectConfigurer.class);
        applicationObjectConfigurer.configure(object, id);
    }

    public static Icon getIcon(String key)
    {
        if (iconSource == null)
            iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        return iconSource.getIcon(key);
    }

    public static <T> T getBean(String id)
    {
        if (applicationContext == null)
            applicationContext = ((DefaultApplicationServices) ApplicationServicesLocator.services())
                    .getApplicationContext();
        return (T) applicationContext.getBean(id);
    }

    public static <T> T getCommand(String commandId)
    {
        return (T) Application.instance().getActiveWindow().getCommandManager().getCommand(commandId);
    }

    /**
     * Converts the incoming string to an escaped output string. This method is far from perfect, only
     * escaping &lt;, &gt; and &amp; characters
     */
    private static String escapeXml(String input)
    {
        return input == null ? "" : input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Creates and returns HTML representing the details of this incident info. This method is only called if
     * the details needs to be generated: ie: the detailed error message property of the incident info is
     * null.
     */
    private static String getDetailsAsHTML(String title, Level level, Throwable e)
    {
        if (e != null)
        {
            // convert the stacktrace into a more pleasent bit of HTML
            StringBuffer html = new StringBuffer("<html>");
            html.append("<h2>" + escapeXml(title) + "</h2>");
            html.append("<HR size='1' noshade>");
            html.append("<div></div>");
            html.append("<b>Message:</b>");
            html.append("<pre>");
            html.append("    " + escapeXml(e.toString()));
            html.append("</pre>");
            html.append("<b>Level:</b>");
            html.append("<pre>");
            html.append("    " + level);
            html.append("</pre>");
            html.append("<b>Stack Trace:</b>");
            html.append("<pre>");
            for (StackTraceElement el : e.getStackTrace())
            {
                html.append("    " + el.toString().replace("<init>", "&lt;init&gt;") + "\n");
            }
            if (e.getCause() != null)
            {
                html.append("</pre>");
                html.append("<b>Cause:</b>");
                html.append("<pre>");
                html.append(e.getCause().getMessage());
                html.append("</pre><pre>");
                for (StackTraceElement el : e.getCause().getStackTrace())
                {
                    html.append("    " + el.toString().replace("<init>", "&lt;init&gt;") + "\n");
                }
            }
            html.append("</pre></html>");
            return html.toString();
        }
        else
        {
            return null;
        }
    }

    public static void handleException(Throwable t)
    {
        Application.instance().getLifecycleAdvisor().getRegisterableExceptionHandler().uncaughtException(
                Thread.currentThread(), t);
    }

    public static void showErrorDialog(Throwable t)
    {
        String title = RcpSupport.getMessage(null, RcpSupport.ERROR_KEY, RcpSupport.TITLE);
        String shortMessage = RcpSupport.getMessage(t.getClass().getName() + "." + RcpSupport.MESSAGE);
        if (shortMessage == null || "".equals(shortMessage))
        {
            shortMessage = t.getMessage();
            if (shortMessage == null || "".equals(shortMessage))
            {
                shortMessage = RcpSupport.getMessage(null, RcpSupport.ERROR_KEY, RcpSupport.MESSAGE);
            }
        }
        showErrorDialog(null, new ErrorInfo(title, shortMessage, getDetailsAsHTML(title, Level.SEVERE, t),
                null, t, Level.SEVERE, null));
    }

    public static void showSQLExceptionErrorDialog(SQLException sqlException)
    {
        String title = RcpSupport.getMessage(null, RcpSupport.ERROR_KEY, RcpSupport.TITLE);
        String shortMessage = RcpSupport.getMessage(sqlException.getClass().getName() + "."
                + sqlException.getErrorCode() + "." + RcpSupport.MESSAGE);
        if (!StringUtils.hasText(shortMessage))
        {
            shortMessage = RcpSupport
                    .getMessage(sqlException.getClass().getName() + "." + RcpSupport.MESSAGE);
            shortMessage += "\nSQL error " + sqlException.getErrorCode();
        }
        if (shortMessage == null || "".equals(shortMessage))
        {
            shortMessage = sqlException.getMessage();
            if (shortMessage == null || "".equals(shortMessage))
            {
                shortMessage = RcpSupport.getMessage(null, RcpSupport.ERROR_KEY, RcpSupport.MESSAGE);
            }
        }
        showErrorDialog(null, new ErrorInfo(title, shortMessage, getDetailsAsHTML(title, Level.SEVERE,
                sqlException), null, sqlException, Level.SEVERE, null));
    }

    public static void showErrorDialogResolveMessages(String id)
    {
        String title = RcpSupport.getMessage(id, RcpSupport.ERROR_KEY, RcpSupport.TITLE);
        String message = RcpSupport.getMessage(id, RcpSupport.ERROR_KEY, RcpSupport.MESSAGE);
        String detail = RcpSupport.getMessage(id, RcpSupport.ERROR_KEY, RcpSupport.DETAIL);
        detail = detail == RcpSupport.ERROR_KEY ? "" : detail;
        showErrorDialog(title, message, detail);
    }

    public static void showErrorDialog(String message)
    {
        showErrorDialog(message, (String) null);
    }

    public static void showErrorDialog(String message, String detail)
    {
        showErrorDialog(RcpSupport.getMessage(null, RcpSupport.ERROR_KEY, RcpSupport.TITLE), message, detail);
    }

    public static void showErrorDialog(String id, Throwable cause)
    {
        String title = RcpSupport.getMessage(id, RcpSupport.ERROR_KEY, RcpSupport.TITLE);
        String message = RcpSupport.getMessage(id, RcpSupport.ERROR_KEY, RcpSupport.MESSAGE);
        showErrorDialog(null, new ErrorInfo(title, message, getDetailsAsHTML(title, Level.SEVERE, cause),
                null, cause, Level.SEVERE, null));
    }

    public static void showErrorDialog(String title, String message, String detail)
    {
        showErrorDialog(null, new ErrorInfo(title, message, detail, null, null, Level.SEVERE, null));
    }

    public static void showErrorDialog(Component parent, ErrorInfo errorInfo)
    {

        if (parent == null)
        {
            if (org.springframework.richclient.application.Application.isLoaded())
            {
                ApplicationWindow activeWindow = org.springframework.richclient.application.Application
                        .instance().getActiveWindow();
                if (activeWindow != null)
                    parent = activeWindow.getControl();
            }
        }

        JXErrorPane pane = new JXErrorPane();
        pane.setErrorInfo(errorInfo);
        pane.setErrorReporter(new JdicEmailNotifierErrorReporter());

        JXErrorPane.showDialog(parent, pane);
    }

    /**
     * @see RcpSupport#showWarningDialog(Component, String, int)
     */
    public static int showWarningDialog(String id, int optionType)
    {
        return showWarningDialog(Application.instance().getActiveWindow().getControl(), id, optionType);
    }

    /**
     * @see RcpSupport#showWarningDialog(Component, String, int)
     */
    public static int showWarningDialog(Component parent, String id, int optionType)
    {
        return showWarningDialog(parent, id, null, optionType);
    }

    public static int showWarningDialog(Component parent, String id, Object[] parameters, int optionType)
    {
        String message = getMessage(null, id, TEXT, parameters);
        String title = getMessage(null, id, TITLE);
        return JOptionPane.showConfirmDialog(parent, message, title, optionType, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * @see RcpSupport#showWarningDialog(Component, String, Object[], int, int)
     */
    public static int showWarningDialog(String id, int optionType, int initialValue)
    {
        return showWarningDialog(Application.instance().getActiveWindow().getControl(), id, null, optionType,
                initialValue);
    }

    public static int showWarningDialog(Component parent, String id, Object[] parameters, int optionType,
                                        int initialValue)
    {
        Object[] options = optionsMap.get(Integer.valueOf(optionType));
        String message = getMessage(null, id, TEXT, parameters);
        String title = getMessage(null, id, TITLE);

        if (optionType == JOptionPane.OK_CANCEL_OPTION && initialValue == CANCEL_OPTION)
            initialValue = 1;

        if (initialValue >= options.length)
            throw new IllegalArgumentException(
                    "De waarde van het argument initialValue is niet gekend door het gekozen optionType");

        return JOptionPane.showOptionDialog(parent, message, title, optionType, JOptionPane.WARNING_MESSAGE,
                null, options, options[initialValue]);
    }

    public static ActionCommand createDummyCommand(final String id, final String msg)
    {
        ActionCommand newCommand = new ActionCommand(id)
        {

            protected void doExecuteCommand()
            {
                System.out.println(msg);
            }
        };
        ((CommandConfigurer) ApplicationServicesLocator.services().getService(CommandConfigurer.class))
                .configure(newCommand);
        return newCommand;
    }

    public static void showWarningDialog(String id)
    {
        showWarningDialog(Application.instance().getActiveWindow().getControl(), id);
    }

    public static void showWarningDialog(Component parent, String id)
    {
        showWarningDialog(parent, id, null);
    }

    public static void showWarningDialog(Component parent, String id, Object[] parameters)
    {
        String message = getMessage(null, id, TEXT, parameters);
        String title = getMessage(null, id, TITLE);
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static int showConfirmationDialog(String id)
    {
        return showConfirmationDialog(Application.instance().getActiveWindow().getControl(), id);
    }

    public static int showConfirmationDialog(Component parent, String id)
    {
        return showConfirmationDialog(parent, id, null);
    }

    public static int showConfirmationDialog(Component parent, String id, Object[] parameters)
    {
        return showConfirmationDialog(parent, id, parameters, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public static int showConfirmationDialog(Component parent, String id, Object[] parameters, int optionType)
    {
        String message = getMessage(null, id, TEXT, parameters);
        String title = getMessage(null, id, TITLE);
        return JOptionPane.showConfirmDialog(parent, message, title, optionType);
    }

    public static void showMessageDialog(Component parent, String id, Object[] parameters, int optionType)
    {
        String message = getMessage(null, id, TEXT, parameters);
        String title = getMessage(null, id, TITLE);
        JOptionPane.showMessageDialog(parent, message, title, optionType);
    }

    /**
     * This method tries to map the values of the given object on the valueModels of the formModel. Instead of
     * setting the object as a backing object, all valueModels are processed one by one and the corresponding
     * property value is fetched from the objectToMap and set on that valueModel. This triggers the usual
     * buffering etc. just as if the user entered the values.
     *
     * @param formModel
     * @param objectToMap
     */
    public static void mapObjectOnFormModel(FormModel formModel, Object objectToMap)
    {
        BeanWrapper beanWrapper = new BeanWrapperImpl(objectToMap);
        for (String fieldName : (Set<String>) formModel.getFieldNames())
        {
            try
            {
                formModel.getValueModel(fieldName).setValue(beanWrapper.getPropertyValue(fieldName));
            }
            catch (BeansException be)
            {
                // silently ignoring, just mapping values, so if there's one missing, don't bother
            }
        }
    }

    private static Object clonePublicCloneableObject(Object value)
    {
        try
        {
            return ((PublicCloneable) value).clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException("Clone not support for object " + value.getClass());
        }
    }

    public static Object getClone(Object value)
    {
        if (value instanceof PublicCloneable)
        {
            return clonePublicCloneableObject(value);
        }
        else if (value instanceof Collection)
        {
            Collection valueCollection = (Collection) value;
            Collection clonedCollection;
            if (valueCollection instanceof java.util.List)
            {
                clonedCollection = (valueCollection instanceof LinkedList)
                        ? new LinkedList()
                        : new ArrayList();
            }
            else if (valueCollection instanceof Set)
            {
                clonedCollection = (valueCollection instanceof SortedSet)
                        ? new TreeSet()
                        : (valueCollection instanceof LinkedHashSet) ? new LinkedHashSet() : new HashSet();
            }
            else
            {
                clonedCollection = (Collection) BeanUtils.instantiateClass(value.getClass());
            }
            for (Object valueElement : valueCollection)
            {
                if (valueElement instanceof PublicCloneable)
                {
                    clonedCollection.add(clonePublicCloneableObject(valueElement));
                }
                else
                {
                    // there isn't much else we can do?
                    clonedCollection.add(valueElement);
                }
            }
            return clonedCollection;
        }
        else if (value instanceof Map)
        {
            Map valueMap = (Map) value;
            Map clonedMap = (valueMap instanceof SortedMap)
                    ? new TreeMap()
                    : (valueMap instanceof LinkedHashMap) ? new LinkedHashMap() : new HashMap();
            for (Iterator entryIter = valueMap.entrySet().iterator(); entryIter.hasNext();)
            {
                Map.Entry entry = (Map.Entry) entryIter.next();
                if (entry.getValue() instanceof PublicCloneable)
                {
                    clonedMap.put(entry.getKey(), clonePublicCloneableObject(entry.getValue()));
                }
                else
                {
                    // there isn't much else we can do?
                    clonedMap.put(entry.getKey(), entry.getValue());
                }
            }
            return clonedMap;
        }
        return value;
    }

    public static JPanel createIconButtonPanel(List<? extends AbstractCommand> commands)
    {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonFactory factory = new DefaultButtonFactory();
        CommandButtonConfigurer configurer = new CommandButtonConfigurer()
        {

            public void configure(AbstractButton button, AbstractCommand command,
                    CommandFaceDescriptor faceDescriptor)
            {
                faceDescriptor.configureIcon(button);
                button.setToolTipText(faceDescriptor.getCaption());
            }
        };
        for (AbstractCommand command : commands)
        {
                buttons.add(command.createButton(factory, configurer));
        }
        return buttons;
    }

    public static JComponent createDummyPanel(String vakske)
    {
        JPanel dummy = new JPanel();
        dummy.add(new JLabel(vakske));
        return dummy;
    }

        /**
     * Toon een dataeditor in de huidige applicationwindow op basis van een command
     *
     * @param command
     *            De command die de dataeditor toont (mag niet null zijn)
     * @param filter
     *            Eventueel initieel filterobject voor de dataeditor
     * @param defaultSelectedObject
     *            Eventueel initieel geselecteerd item in de lijst
     * 
     * @author ldo
     * @since 0.4.4
     */
    public static void executeViewDataEditorCommand(DataEditorWidgetViewCommand command, Object filter,
            Object defaultSelectedObject)
    {
        org.springframework.util.Assert.notNull(command, "Command mag niet null zijn!");
        Map<String, Object> dataEditorParameters = new HashMap<String, Object>(2);
        dataEditorParameters.put(DefaultDataEditorWidget.PARAMETER_FILTER, filter);
        dataEditorParameters.put(DefaultDataEditorWidget.PARAMETER_DEFAULT_SELECTED_OBJECT,
                defaultSelectedObject);
        Map<String, Object> commandParameters = new HashMap<String, Object>(1);
        commandParameters.put(DefaultDataEditorWidget.PARAMETER_MAP, dataEditorParameters);
        command.execute(commandParameters);
    }
}
