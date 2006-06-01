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
package org.springframework.richclient.settings.jdbc;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.richclient.settings.AbstractSettings;
import org.springframework.richclient.settings.Settings;

/**
 * 
 * @author Peter De Bruycker
 */
public class JdbcSettings extends AbstractSettings {
    private DataSource dataSource;

    private Integer id;
    private String user;

    private Map values = new HashMap();
    private Set remove = new HashSet();
    private Set add = new HashSet();
    private Set update = new HashSet();

    private String[] childKeys;

    public JdbcSettings( DataSource ds, String user, Integer id, String key ) {
        this( null, ds, user, id, key );
    }

    public JdbcSettings( JdbcSettings parent, DataSource ds, String user, Integer id, String key ) {
        super( parent, key );
        this.id = id;

        // TODO assert dataSource not null
        dataSource = ds;

        // TODO assert user not empty
        this.user = user;
    }

    protected boolean internalContains( String key ) {
        return values.containsKey( key );
    }

    protected String[] internalGetChildSettings() {
        if( childKeys == null ) {
            loadChildKeys();
        }
        return childKeys;
    }

    protected Settings internalCreateChild( String key ) {
        return new JdbcSettings( this, dataSource, user, null, key );
    }

    protected void internalSet( String key, String value ) {
        boolean isNew = !values.containsKey( key ) || add.contains( key );

        values.put( key, value );

        if( isNew ) {
            add.add( key );
        } else {
            update.add( key );
        }
        remove.remove( key );
    }

    protected String internalGet( String key ) {
        return (String) values.get( key );
    }

    protected void internalRemove( String key ) {
        values.remove( key );

        if( !add.contains( key ) ) {
            remove.add( key );
        }

        update.remove( key );
        add.remove( key );
    }

    public String[] getKeys() {
        return (String[]) values.keySet().toArray( new String[0] );
    }

    public Integer getId() {
        return id;
    }

    public void save() throws IOException {
        if( getParent() != null ) {
            getParent().save();
        }

        JdbcTemplate template = new JdbcTemplate( dataSource );

        // if this is a new node, insert it
        if( id == null ) {
            JdbcSettings parent = (JdbcSettings) getParent();
            template.update( "INSERT INTO SETTINGS (KEY, PARENT, USER) VALUES (?, ?, ?)", new Object[] { getName(),
                    parent == null ? null : parent.getId(), user } );
            id = Integer.valueOf( template.queryForInt( "SELECT MAX(ID) FROM SETTINGS" ) );
        } else {
            for( Iterator iter = remove.iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                template.update( "REMOVE FROM SETTINGS_VALUES WHERE SETTINGS_ID=? AND KEY=?", new Object[] { id, key } );
            }
            for( Iterator iter = update.iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                template.update( "UPDATE SETTINGS_VALUES SET VALUE=? WHERE SETTINGS_ID=? AND KEY=?", new Object[] {
                        values.get( key ), id, key } );
            }
        }

        for( Iterator iter = add.iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            template.update( "INSERT INTO SETTINGS_VALUES (SETTINGS_ID, KEY, VALUE) VALUES (?, ?, ?)", new Object[] {
                    id, key, values.get( key ) } );
        }

        remove.clear();
        update.clear();
        add.clear();
    }

    public void load() throws IOException {
        if( id == null ) {
            return;
        }

        JdbcTemplate template = new JdbcTemplate( dataSource );
        List entries = template.queryForList( "SELECT KEY, VALUE FROM SETTINGS_VALUES WHERE SETTINGS_ID=?",
                new Object[] { id } );
        for( Iterator iter = entries.iterator(); iter.hasNext(); ) {
            Map entry = (Map) iter.next();
            values.put(entry.get( "KEY" ), entry.get( "VALUE" ));
        }
    }

    private void loadChildKeys() {
        JdbcTemplate template = new JdbcTemplate( dataSource );
        List keys = template.queryForList( "SELECT KEY FROM SETTINGS WHERE PARENT=" + id, String.class );

        childKeys = (String[]) keys.toArray( new String[keys.size()] );
    }

    public String getUser() {
        return user;
    }

    public void internalRemoveSettings() {
        if( id != null ) {
            // first delete all children
            for( int i = 0; i < childKeys.length; i++ ) {
                getSettings(childKeys[i]).removeSettings();
            }

            // now delete all values
            JdbcTemplate template = new JdbcTemplate( dataSource );
            template.update( "DELETE FROM SETTINGS_VALUES WHERE SETTINGS_ID=?", new Object[] { id } );

            // now delete our own record
            template.update( "DELETE FROM SETTINGS WHERE ID=?", new Object[] { id } );

            id = null;
        }

        values.clear();
        remove.clear();
        add.clear();
        update.clear();
    }
}
