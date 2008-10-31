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
package org.springframework.rules.constraint;

import org.springframework.rules.constraint.Constraint;
import org.springframework.core.enums.StringCodedLabeledEnum;
import org.springframework.util.StringUtils;

/**
 * A like constraint, supporting "starts with%", "%ends with", and "%contains%".
 *
 * @author Keith Donald
 */
public class Like implements Constraint {

	public static final LikeType STARTS_WITH = new LikeType("startsWith");

	public static final LikeType ENDS_WITH = new LikeType("endsWith");

	public static final LikeType CONTAINS = new LikeType("contains");

	private LikeType type;

	private String stringToMatch;

	public Like(LikeType type, String likeString) {
		this.type = type;
		this.stringToMatch = likeString;
	}

	public Like(String encodedLikeString) {
		if (encodedLikeString.startsWith("%")) {
			if (encodedLikeString.endsWith("%")) {
				this.type = CONTAINS;
			}
			else {
				this.type = ENDS_WITH;
			}
		}
		else if (encodedLikeString.endsWith("%")) {
			this.type = STARTS_WITH;
		}
		else {
			this.type = CONTAINS;
		}
		stringToMatch = StringUtils.deleteAny(encodedLikeString, "%");
	}

	public boolean test(Object argument) {
		String value = String.valueOf(argument);
		if (type == STARTS_WITH) {
			return value.startsWith(stringToMatch);
		}
		else if (type == ENDS_WITH) {
			return value.endsWith(stringToMatch);
		}
		else {
			return value.indexOf(stringToMatch) != -1;
		}
	}

	public LikeType getType() {
		return type;
	}

	public String getString() {
		return stringToMatch;
	}
    
    public static class LikeType extends StringCodedLabeledEnum {
        private LikeType(String code) {
            super(code, null);
        }
    }

}