/*
 * $Header: /usr/local/cvs/module/src/java/File.java,v 1.7 2004/01/16 22:23:11
 * keith Exp $ $Revision$ $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.application.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandRegistry;
import org.springframework.richclient.command.CommandServices;
import org.springframework.richclient.command.support.DefaultCommandManager;

public class ApplicationWindowCommandManager extends DefaultCommandManager {
	private List sharedCommands;

	public ApplicationWindowCommandManager() {
		super();
	}

	public ApplicationWindowCommandManager(CommandRegistry parent) {
		super(parent);
	}

	public ApplicationWindowCommandManager(CommandServices commandServices) {
		super(commandServices);
	}

	public void setSharedCommandIds(String[] sharedCommandIds) {
		if (sharedCommandIds.length == 0) {
			sharedCommands = Collections.EMPTY_LIST;
		}
		else {
			this.sharedCommands = new ArrayList(sharedCommandIds.length);
			for (int i = 0; i < sharedCommandIds.length; i++) {
				ActionCommand globalCommand = createTargetableActionCommand(sharedCommandIds[i], null);
				sharedCommands.add(globalCommand);
			}
		}
	}

	public Iterator getSharedCommands() {
		if (sharedCommands == null) {
			return Collections.EMPTY_LIST.iterator();
		}
		return sharedCommands.iterator();
	}

}