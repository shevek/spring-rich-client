package org.springframework.richclient.exceptionhandling;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Displays a message to the user which is fetched from the I18N files
 * based on the class and superclasses of the throwable.
 *
 * For example if an IllegalArgumentException is thrown, it will search for
 * java.lang.IllegalArgumentException.caption and java.lang.IllegalArgumentException.description first,
 * and if it cant find that it will try in order:
 * java.lang.RuntimeException.caption/description, java.lang.Exception.caption/description and
 * java.lang.Throwable.caption/description.
 *
 * The exception message is passed as a parameter, but is idented and wrapped first.
 *
 * @author Geoffrey De Smet
 * @since 0.3
 */
public class MessagesDialogExceptionHandler extends AbstractDialogExceptionHandler {

    private int evaluatedChainedIndex = 0;
    private int wrapLength = 120;
    private int identLength = 2;

    /**
     * If this is bigger then 0, instead of finding a message for the giving throwable,
     * it will use a recursive chained exception.
     * @param evaluatedChainedIndex the number of times that should be recursed
     */
    public void setEvaluatedChainedIndex(int evaluatedChainedIndex) {
        this.evaluatedChainedIndex = evaluatedChainedIndex;
    }

    /**
     * Sets the wrap length applied on the exception message passed as a parameter.
     * Defaults to 120.
     * @param wrapLength
     */
    public void setWrapLength(int wrapLength) {
        this.wrapLength = wrapLength;
    }

    /**
     * Sets the identation applied on the exception message passed as a parameter.
     * Defaults to 2.
     * @param identLength
     */
    public void setIdentLength(int identLength) {
        this.identLength = identLength;
    }

    public String resolveExceptionCaption(Throwable throwable) {
        List<String> messageCaptionKeyList = new ArrayList<String>();
        Throwable evaluatedThrowable = determineEvaluatedThrowable(throwable);
        Class clazz = throwable.getClass();
        while (clazz != Object.class) {
            messageCaptionKeyList.add(clazz.getName() + ".caption");
            clazz = clazz.getSuperclass();
        }
        String[] codes = messageCaptionKeyList.toArray(new String[messageCaptionKeyList.size()]);
        return messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                codes, codes[0]));
    }

    public Object createExceptionContent(Throwable throwable) {
        List<String> messageDescriptionKeyList = new ArrayList<String>();
        Throwable evaluatedThrowable = determineEvaluatedThrowable(throwable);
        Class clazz = evaluatedThrowable.getClass();
        while (clazz != Object.class) {
            messageDescriptionKeyList.add(clazz.getName() + ".description");
            clazz = clazz.getSuperclass();
        }
        String[] codes = messageDescriptionKeyList.toArray(new String[messageDescriptionKeyList.size()]);
        String[] parameters = new String[]{formatMessage(evaluatedThrowable.getMessage())};
        return messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                codes, parameters, codes[0]));
    }

    private Throwable determineEvaluatedThrowable(Throwable throwable) {
        Throwable evaluatedThrowable = throwable;
        for (int i = 0; i < evaluatedChainedIndex; i++) {
            Throwable cause = evaluatedThrowable.getCause();
            if (cause == null || cause == evaluatedThrowable) {
                break;
            } else {
                evaluatedThrowable = cause;
            }
        }
        return evaluatedThrowable;
    }

    protected String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        String identString = StringUtils.leftPad("", identLength);
        String newLineWithIdentString = "\n" + identString;
        StringBuffer formattedMessageBuffer = new StringBuffer(identString);
        StringTokenizer messageTokenizer = new StringTokenizer(message, "\n");
        while (messageTokenizer.hasMoreTokens()) {
            String messageToken = messageTokenizer.nextToken();
            formattedMessageBuffer.append(WordUtils.wrap(messageToken, wrapLength, newLineWithIdentString, true));
            if (messageTokenizer.hasMoreTokens()) {
                formattedMessageBuffer.append(newLineWithIdentString);
            }
        }
        return formattedMessageBuffer.toString();
    }

}
