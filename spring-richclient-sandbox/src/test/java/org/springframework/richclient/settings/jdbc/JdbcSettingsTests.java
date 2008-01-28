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

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsAbstractTests;

/**
 * @author Peter De Bruycker
 */
public class JdbcSettingsTests extends SettingsAbstractTests {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public JdbcSettingsTests() {
    }

    protected Settings createSettings() {
        return new JdbcSettings(dataSource, "user", Integer.valueOf(5), "test");
    }

    protected void doSetUp() throws Exception {
        dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);

        // setup the schema
        jdbcTemplate.execute("DROP TABLE SETTINGS_VALUES IF EXISTS");
        jdbcTemplate.execute("DROP TABLE SETTINGS IF EXISTS");

        jdbcTemplate.execute("CREATE TABLE SETTINGS (ID INTEGER IDENTITY, KEY VARCHAR(250) NOT NULL, PARENT INTEGER, USER VARCHAR(250) NOT NULL, CONSTRAINT SYS_CT_52 UNIQUE(KEY,USER))");
        jdbcTemplate.execute("CREATE TABLE SETTINGS_VALUES (SETTINGS_ID INTEGER NOT NULL, KEY VARCHAR(250) NOT NULL, VALUE VARCHAR(250), PRIMARY KEY(SETTINGS_ID,KEY), CONSTRAINT SYS_FK_48 FOREIGN KEY(SETTINGS_ID) REFERENCES SETTINGS(ID))");
    }

    /**
     * Creates a <code>DataSource</code> using hsqldb in memory-only mode
     * @return the <code>DataSource</code>
     */
    private static DataSource createDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:test-database");
        ds.setUsername("sa");

        return ds;
    }

    public void testLoadExistingSettings() throws Exception {
        jdbcTemplate.execute("INSERT INTO SETTINGS (ID, KEY, USER) VALUES (55, 'test-key', 'test-user')");
        jdbcTemplate.execute("INSERT INTO SETTINGS_VALUES (SETTINGS_ID, KEY, VALUE) VALUES (55, 'key0', 'true')");
        jdbcTemplate.execute("INSERT INTO SETTINGS_VALUES (SETTINGS_ID, KEY, VALUE) VALUES (55, 'key1', '25')");

        JdbcSettings settings = new JdbcSettings(dataSource, "test-user", Integer.valueOf(55), "test-key");
        settings.load();
    }

    public void testLoadHierarchy() throws Exception {

    }

    public void testSaveHierarchy() throws Exception {
        JdbcSettings settings = new JdbcSettings(dataSource, "test-user", null, "test-key");
        settings.setBoolean("boolean-value", true);

        JdbcSettings childSettings = (JdbcSettings)settings.getSettings("child");
        childSettings.setString("string", "test");
        childSettings.save();
        
        assertEquals(Integer.valueOf(0), settings.getId());
        assertEquals(Integer.valueOf(1), childSettings.getId());
    }

    public void testSaveNewSettings() throws Exception {
        JdbcSettings settings = new JdbcSettings(dataSource, "test-user", null, "test-key");

        assertEquals("name not set", "test-key", settings.getName());
        assertEquals("user not set", "test-user", settings.getUser());
        assertNull("id must be null until first save", settings.getId());

        settings.setBoolean("boolean-value", true);
        settings.setString("string-value", "value");

        settings.save();

        assertEquals(Integer.valueOf(0), settings.getId());

        assertEquals(1, jdbcTemplate.queryForInt("SELECT count(*) FROM SETTINGS"));
        Map map = jdbcTemplate.queryForMap("SELECT * FROM SETTINGS WHERE ID = 0");
        assertEquals(Integer.valueOf(0), map.get("ID"));
        assertEquals("test-key", map.get("KEY"));
        assertEquals(null, map.get("PARENT"));
        assertEquals("test-user", map.get("USER"));

        assertEquals(2, jdbcTemplate.queryForInt("SELECT count(*) FROM SETTINGS_VALUES"));
        List values = jdbcTemplate.queryForList("SELECT * FROM SETTINGS_VALUES");
        assertEquals(2, values.size());
        Map first = (Map) values.get(0);
        Map second = (Map) values.get(1);

        assertEquals(Integer.valueOf(0), first.get("SETTINGS_ID"));
        assertEquals(Integer.valueOf(0), second.get("SETTINGS_ID"));

        assertEquals("boolean-value", first.get("KEY"));
        assertEquals("true", first.get("VALUE"));

        assertEquals("string-value", second.get("KEY"));
        assertEquals("value", second.get("VALUE"));
    }
}
