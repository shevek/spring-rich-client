/*
 * $Header:
 * /usr/local/cvs/product/project/src/java/com/csi/product/project/Type.java,v
 * 1.1 2004/01/26 23:10:32 keith Exp $ $Revision$ $Date: 2004/01/26
 * 23:10:32 $
 *
 * Copyright Computer Science Innovations (CSI), 2003. All rights reserved.
 */
package org.springframework.rules.reporting;

import org.springframework.context.MessageSourceResolvable;

/**
 * @author Keith Donald
 */
public class TypeResolvableSupport implements TypeResolvable,
		MessageSourceResolvable {

	private String type;

	public TypeResolvableSupport() {

	}

	public TypeResolvableSupport(String type) {
		setType(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object[] getArguments() {
		return null;
	}

	public String[] getCodes() {
		return new String[]{type};
	}

	public String getDefaultMessage() {
		return type;
	}

}