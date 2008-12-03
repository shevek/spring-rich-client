/*
 * Copyright 2005 the original author or authors.
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
package com.jidesoft.spring.richclient.googledemo.command;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Command that asks the user for a query string and then launches
 * a thread that searches google for that query string
 * 
 * @author Jonny Wray
 *
 */
public class SearchGoogleCommand extends ApplicationWindowAwareCommand {
	
	 protected void doExecuteCommand() {
		SearchBean searchBean = new SearchBean();
	    FormModel formModel = FormModelHelper.createFormModel(searchBean);
	    final SearchForm form = new SearchForm(formModel);
    	FormBackedDialogPage page = new FormBackedDialogPage(form);

    	TitledPageApplicationDialog dialog = 
    			new TitledPageApplicationDialog(page, getParentWindowControl()) {
    		
    	    protected boolean onFinish() {
    	        form.commit();
    	        SearchBean bean = (SearchBean)form.getFormObject();
    	        try{
	    			SearchWorker worker = new SearchWorker(bean.getQueryString());
	    			worker.start();
	    			return true;
    	        }
    	        catch(IllegalArgumentException e){
    	        	return false;
    	        }
    	    }
    	};
    	dialog.showDialog();
	 }
}
