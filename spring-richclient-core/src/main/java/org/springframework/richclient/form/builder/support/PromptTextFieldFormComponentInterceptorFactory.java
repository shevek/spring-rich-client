package org.springframework.richclient.form.builder.support;

import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSource;

public class PromptTextFieldFormComponentInterceptorFactory implements FormComponentInterceptorFactory, MessageSourceAware
{
    private MessageSource messageSource;

    public FormComponentInterceptor getInterceptor(FormModel formModel)
    {
        return new PromptTextFieldFormComponentInterceptor(formModel, messageSource);
    }

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }
}
