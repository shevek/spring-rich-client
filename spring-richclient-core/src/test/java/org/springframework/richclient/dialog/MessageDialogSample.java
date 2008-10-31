/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;

public class MessageDialogSample {

    public static void main( String[] args ) throws Exception {
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

        final JFrame frame = new JFrame( "test" );
        JButton openButton = new JButton( "open dialog" );
        final JTextArea textField = new JTextArea( 8, 40 );
        textField.setLineWrap( true );
        textField.setWrapStyleWord( true );
        textField.setText( "This is the first line.\n"
                + "This is the second line which is also much longer. "
                + "This is to check if the linewrapping occurs correctly. "
                + "Try resizing the frame to see how the MessageDialog behaves." );
        frame.add( new JScrollPane( textField ) );
        frame.add( openButton, BorderLayout.SOUTH );
        frame.pack();

        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        openButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                MessageDialog dialog = new MessageDialog( "Message", frame, textField.getText() );
                dialog.setMinimumWidth( 300 );
                dialog.showDialog();
            }
        } );

        // load dummy application
        Application.load( null );
        new Application( new DefaultApplicationLifecycleAdvisor() );
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        Application.instance().setApplicationContext( applicationContext );
        applicationContext.getStaticMessageSource().addMessage( "okCommand.label", Locale.getDefault(), "Ok" );
        applicationContext.refresh();
    }
}
