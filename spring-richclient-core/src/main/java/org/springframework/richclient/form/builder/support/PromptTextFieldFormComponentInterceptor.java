package org.springframework.richclient.form.builder.support;

import org.jdesktop.xswingx.PromptSupport;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.richclient.text.TextComponentInterceptor;
import org.springframework.util.StringUtils;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Locale;

public class PromptTextFieldFormComponentInterceptor extends TextComponentInterceptor
{
    private MessageSource messageSource;
    private FormModel formModel;
    private static final String DEFAULT_PROMPT_KEY = "prompt";
    private String promptKey;

    public String getPromptKey()
    {
        if(promptKey == null)
            return DEFAULT_PROMPT_KEY;
        return promptKey;
    }

    public void setPromptKey(String promptKey)
    {
        this.promptKey = promptKey;
    }

    public PromptTextFieldFormComponentInterceptor(FormModel formModel, MessageSource messageSource) {
        this.formModel = formModel;
        this.messageSource = messageSource;
    }

    protected void processComponent(String propertyName, JTextComponent textComponent) {
        String prompt = messageSource.getMessage(new DefaultMessageSourceResolvable(getMessageKeys(formModel,
                propertyName), ""), Locale.getDefault());

        if (StringUtils.hasText(prompt)) {
            PromptSupport.setFontStyle(Font.ITALIC, textComponent);
            PromptSupport.setPrompt(prompt, textComponent);
        }
    }

    protected String[] getMessageKeys(FormModel formModel, String propertyName) {
        return new String[] { formModel.getId() + "." + propertyName + "." + getPromptKey(),
                              propertyName + "." + getPromptKey()};
    }
}
