/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.binding.form.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.ReflectiveVisitorHelper;
import org.springframework.core.closure.Constraint;
import org.springframework.core.style.StylerUtils;
import org.springframework.rules.constraint.And;
import org.springframework.rules.constraint.ClosureResultConstraint;
import org.springframework.rules.constraint.Not;
import org.springframework.rules.constraint.Or;
import org.springframework.rules.constraint.ParameterizedBinaryConstraint;
import org.springframework.rules.constraint.Range;
import org.springframework.rules.constraint.StringLengthConstraint;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.constraint.property.ParameterizedPropertyConstraint;
import org.springframework.rules.constraint.property.PropertiesConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;
import org.springframework.rules.reporting.DefaultMessageTranslator;
import org.springframework.rules.reporting.PropertyResults;
import org.springframework.rules.reporting.TypeResolvable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * A validation message translator that is aware of form property names info exposed through
 * the FormPropertyFaceDescriptor class.
 * 
 * TODO: This class is an almost exact copy of DefaultMessageTranslator ideally we
 * would just be able to extend that class but I can't modify DefaultMessageTranslator
 * as it's part of Spring. OH. 
 */
public class FormModelAwareMessageTranslator {

    protected static final Log logger = LogFactory.getLog(DefaultMessageTranslator.class);

    private final ReflectiveVisitorHelper visitorSupport = new ReflectiveVisitorHelper();

    private boolean appendValue = false;

    PropertyResults results;

    private final List args = new ArrayList();

    private MessageSource messages;

    private final FormModel formModel;

    public FormModelAwareMessageTranslator(FormModel formModel, MessageSource messages) {
        setMessageSource(messages);
        this.formModel = formModel;
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource is required");
        this.messages = messageSource;
    }

    public String getMessage(PropertyResults results) {
        Assert.notNull(results, "No property results specified");
        args.clear();
        return buildMessage(results.getPropertyName(), results.getRejectedValue(), results.getViolatedConstraint(),
                Locale.getDefault());
    }

    private String buildMessage(String propertyName, Object rejectedValue, Constraint constraint, Locale locale) {
        StringBuffer buf = new StringBuffer(255);
        MessageSourceResolvable[] args = resolveArguments(constraint);
        if (logger.isDebugEnabled()) {
            logger.debug(StylerUtils.style(args));
        }
        if (propertyName != null) {
            buf.append(getDisplayName(propertyName));
            buf.append(' ');
            if (appendValue) {
                if (rejectedValue != null) {
                    buf.append("'" + rejectedValue + "'");
                    buf.append(' ');
                }
            }
        }
        for (int i = 0; i < args.length - 1; i++) {
            MessageSourceResolvable arg = args[i];
            buf.append(messages.getMessage(arg, locale));
            buf.append(' ');
        }
        buf.append(messages.getMessage(args[args.length - 1], locale));
        buf.append(".");
        return buf.toString();
    }

    private String getDisplayName(String propertyName) {
        return formModel.getFormPropertyFaceDescriptor(propertyName).getDisplayName();
    }

    private MessageSourceResolvable[] resolveArguments(Constraint constraint) {
        visitorSupport.invokeVisit(this, constraint);
        return (MessageSourceResolvable[])args.toArray(new MessageSourceResolvable[0]);
    }

    void visit(CompoundPropertyConstraint rule) {
        visitorSupport.invokeVisit(this, rule.getPredicate());
    }

    void visit(PropertiesConstraint e) {
        add(getMessageCode(e.getConstraint()), new Object[] {getDisplayName(e.getOtherPropertyName())},
                e.toString());
    }

    void visit(ParameterizedPropertyConstraint e) {
        add(getMessageCode(e.getConstraint()), new Object[] {e.getParameter()}, e.toString());
    }

    private void add(String code, Object[] args, String defaultMessage) {
        MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(new String[] {code}, args,
                defaultMessage);
        if (logger.isDebugEnabled()) {
            logger.debug("Adding resolvable: " + resolvable);
        }
        this.args.add(resolvable);
    }

    void visit(PropertyValueConstraint valueConstraint) {
        visitorSupport.invokeVisit(this, valueConstraint.getConstraint());
    }

    void visit(And and) {
        Iterator it = and.iterator();
        while (it.hasNext()) {
            Constraint p = (Constraint)it.next();
            visitorSupport.invokeVisit(this, p);
            if (it.hasNext()) {
                add("and", null, "add");
            }
        }
    }

    void visit(Or or) {
        Iterator it = or.iterator();
        while (it.hasNext()) {
            Constraint p = (Constraint)it.next();
            visitorSupport.invokeVisit(this, p);
            if (it.hasNext()) {
                add("or", null, "or");
            }
        }
    }

    void visit(Not not) {
        add("not", null, "not");
        visitorSupport.invokeVisit(this, not.getConstraint());
    }

    //@TODO - consider standard visitor here...
    void visit(StringLengthConstraint constraint) {
        ClosureResultConstraint c = (ClosureResultConstraint)constraint.getPredicate();
        Object p = c.getPredicate();
        MessageSourceResolvable resolvable;
        if (p instanceof ParameterizedBinaryConstraint) {
            resolvable = handleParameterizedBinaryPredicate((ParameterizedBinaryConstraint)p);
        }
        else {
            resolvable = handleRange((Range)p);
        }
        Object[] args = new Object[] {resolvable};
        add(getMessageCode(constraint), args, constraint.toString());
    }

    void visit(ClosureResultConstraint c) {
        visitorSupport.invokeVisit(this, c.getPredicate());
    }

    private MessageSourceResolvable handleParameterizedBinaryPredicate(ParameterizedBinaryConstraint p) {
        MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(
                new String[] {getMessageCode(p.getConstraint())}, new Object[] {p.getParameter()}, p.toString());
        return resolvable;
    }

    private MessageSourceResolvable handleRange(Range r) {
        MessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(new String[] {getMessageCode(r)},
                new Object[] {r.getMin(), r.getMax()}, r.toString());
        return resolvable;
    }

    void visit(Constraint constraint) {
        if (constraint instanceof Range) {
            this.args.add(handleRange((Range)constraint));
        }
        else {
            add(getMessageCode(constraint), null, constraint.toString());
        }
    }

    private String getMessageCode(Object o) {
        if (o instanceof TypeResolvable) {
            String type = ((TypeResolvable)o).getType();
            if (type != null) {
                return type;
            }
        }
        return ClassUtils.getShortNameAsProperty(o.getClass());
    }
}