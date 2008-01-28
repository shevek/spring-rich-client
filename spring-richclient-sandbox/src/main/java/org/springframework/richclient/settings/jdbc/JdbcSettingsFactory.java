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
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsException;
import org.springframework.richclient.settings.SettingsFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author Peter De Bruycker
 */
public class JdbcSettingsFactory implements SettingsFactory, InitializingBean {
    private DataSource dataSource;
    private UserNameProvider userNameProvider;

    public JdbcSettingsFactory() {
    }

    public void setDataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * TODO: somehow make the key unique by adding a user name or login or something
     */
    public Settings createSettings( String key ) throws SettingsException {
        try {
            JdbcTemplate template = new JdbcTemplate( dataSource );
            Map result = template.queryForMap( "SELECT * FROM SETTINGS WHERE KEY=? AND USER=?", new Object[] { key,
                    userNameProvider.getUser() } );

            JdbcSettings settings = new JdbcSettings( dataSource, userNameProvider.getUser(), (Integer) result
                    .get( "ID" ), key );
            settings.load();
            return settings;
        } catch( IncorrectResultSizeDataAccessException e ) {
            return new JdbcSettings( dataSource, userNameProvider.getUser(), null, key );
        } catch( IOException e ) {
            throw new SettingsException( "Unable to create settings with name " + key, e );
        }
    }

    public void setUserNameProvider( UserNameProvider userNameProvider ) {
        this.userNameProvider = userNameProvider;
    }

    public UserNameProvider getUserNameProvider() {
        return userNameProvider;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull( userNameProvider, "UserNameProvider must be set" );
        Assert.notNull( dataSource, "DataSource must be set" );
    }
}
