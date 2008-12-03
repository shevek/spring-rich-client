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
package com.jidesoft.spring.richclient.googledemo.editor;

import org.springframework.richclient.application.docking.jide.editor.AbstractEditor;
import org.springframework.richclient.text.HtmlPane;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

/**
 * Simple editor that displays HTML in its content window.
 * 
 * @author Jonny Wray
 *
 */
public class HtmlPaneEditor extends AbstractEditor
{

	// Id generator so each instance is new editor
	private static Random rand = new Random();
	private URL htmlFile = null;
	private String id = null;
	
	private JScrollPane contentPane;
	private HtmlPane textPane;
	
	public HtmlPaneEditor(){
		
	}
	
	public void initialize(Object editorObject){
		if(!(editorObject instanceof URL)){
			throw new IllegalArgumentException("Editor object should be a URL");
		}
		htmlFile = (URL)editorObject;
	}
	
    public JComponent getControl() {
    	if(contentPane == null){
	    	textPane = new HtmlPane();
	    	textPane.setText(getHTML(getFileContents(htmlFile)));
	    	textPane.setEditable(false);
	    	contentPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	    			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	}
    	return contentPane;
	}

    private String getFileContents(URL file){
    	BufferedReader in = null;
    	try{
    		StringBuffer builder = new StringBuffer();
			in = new BufferedReader(new InputStreamReader(file.openStream()));
			String line;
			while((line = in.readLine()) != null){
				builder.append(line);
			}
			return builder.toString();
		}
		catch(IOException e){
			String message = "Unable to open file "+htmlFile.getFile()+". <br />"+e.getMessage();
			return message;
		}
		finally{
			if(in != null){
				try{
					in.close();
				}
				catch(IOException e){}
			}
		}
    }
    
    /**
     * Generates a unqiue id to ensure each time this editor is
     * requested it's a new instance
     */
	public String getId() {
		if(id == null){
			id = getDescriptor().getId() + rand.nextInt();
		}
		return id;
	}
	
	public String getDisplayName() {
		String fileName = htmlFile.getFile();
		int index = fileName.lastIndexOf("/");
		if(index > -1){
			return fileName.substring(index+1);
		}
		return fileName;
	}

	private StringBuffer wrapInTag(String tagName, StringBuffer original){
    	StringBuffer buffer = new StringBuffer("<"+tagName+">");
    	buffer.append(original);
    	buffer.append("</"+tagName+">");
    	return buffer;
    }
    
    private StringBuffer wrapInTag(String tagName, String original){
    	return wrapInTag(tagName, new StringBuffer(original));
    }
    
    private String getHTML(String message){
    	StringBuffer html = wrapInTag("body", message);
    	html = wrapInTag("center", html);
    	html = wrapInTag("h2", html);
    	return html.toString();
    }

    // Disable saving by always returning false for being dirty
	public boolean isDirty() {
		return false;
	}
}
