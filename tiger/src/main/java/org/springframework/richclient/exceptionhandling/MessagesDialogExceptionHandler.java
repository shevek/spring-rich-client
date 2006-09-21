package org.springframework.richclient.exceptionhandling;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Geoffrey De Smet
 */
public class MessagesDialogExceptionHandler extends AbstractDialogExceptionHandler {

    private int wrapLength = 120;
    private int identLength = 2;

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
        Class clazz = throwable.getClass();
        while (clazz != Object.class) {
            messageDescriptionKeyList.add(clazz.getName() + ".description");
            clazz = clazz.getSuperclass();
        }
        String[] codes = messageDescriptionKeyList.toArray(new String[messageDescriptionKeyList.size()]);
        String[] parameters = new String[]{formatMessage(throwable.getMessage())};
        return messageSourceAccessor.getMessage(new DefaultMessageSourceResolvable(
                codes, parameters, codes[0]));
    }

    public String formatMessage(String message) {
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
