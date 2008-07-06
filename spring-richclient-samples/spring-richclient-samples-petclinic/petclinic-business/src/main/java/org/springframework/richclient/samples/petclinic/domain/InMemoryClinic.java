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
package org.springframework.richclient.samples.petclinic.domain;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.jdbc.SimpleJdbcClinic;

/**
 * Provides an in-memory PetClinic business object.
 *
 * <P>
 * Leverages HSQL database's in-memory option and uses the Spring-supplied
 * <code>SimpleJdbcClinic</code>. This class simply inserts the schema and base
 * data into the in-memory instance at startup time. It also inserts data
 * required for security.
 *
 * @author Ben Alex
 */
public class InMemoryClinic extends SimpleJdbcClinic {

    private final Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource;

    /**
     * Note: the SimpleJdbcClinic uses autowiring, we could do the same here.
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource=dataSource;
        init();
    }

    public void init() {
        super.init(dataSource);
        JdbcTemplate template = new JdbcTemplate(dataSource);

        // Schema: Petclinic
        template
                .execute("CREATE TABLE vets (id INT NOT NULL IDENTITY PRIMARY KEY, first_name VARCHAR(30), last_name VARCHAR(30))");
        template.execute("CREATE TABLE specialties (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80))");

        template.execute("CREATE TABLE vet_specialties (vet_id INT NOT NULL, specialty_id INT NOT NULL)");
        template
                .execute("alter table vet_specialties add constraint fk_vet_specialties_vets foreign key (vet_id) references vets(id)");
        template
                .execute("alter table vet_specialties add constraint fk_vet_specialties_specialties foreign key (specialty_id) references specialties(id)");

        template.execute("CREATE TABLE types (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80))");
        template
                .execute("CREATE TABLE owners (id INT NOT NULL IDENTITY PRIMARY KEY, first_name VARCHAR(30), last_name VARCHAR(30), address VARCHAR(255), city VARCHAR(80), telephone VARCHAR(20))");

        template
                .execute("CREATE TABLE pets (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(30), birth_date DATE, type_id INT NOT NULL, owner_id INT NOT NULL)");
        template.execute("alter table pets add constraint fk_pets_owners foreign key (owner_id) references owners(id)");
        template.execute("alter table pets add constraint fk_pets_types foreign key (type_id) references types(id)");

        template
                .execute("CREATE TABLE visits (id INT NOT NULL IDENTITY PRIMARY KEY, pet_id INT NOT NULL, visit_date DATE, description VARCHAR(255))");
        template.execute("alter table visits add constraint fk_visits_pets foreign key (pet_id) references pets(id)");

        // Schema: Spring Security
        template
                .execute("CREATE TABLE users (username VARCHAR(50) NOT NULL PRIMARY KEY, password VARCHAR(50) NOT NULL, enabled BIT NOT NULL)");
        template.execute("CREATE TABLE authorities (username VARCHAR(50) NOT NULL, authority VARCHAR(50) NOT NULL)");
        template
                .execute("alter table authorities add constraint fk_authorities_users foreign key (username) references users(username)");

        // Data: Petclinic
        template.execute("INSERT INTO vets VALUES (1, 'James', 'Carter')");
        template.execute("INSERT INTO vets VALUES (2, 'Helen', 'Leary')");
        template.execute("INSERT INTO vets VALUES (3, 'Linda', 'Douglas')");
        template.execute("INSERT INTO vets VALUES (4, 'Rafael', 'Ortega')");
        template.execute("INSERT INTO vets VALUES (5, 'Henry', 'Stevens')");
        template.execute("INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins')");

        template.execute("INSERT INTO specialties VALUES (1, 'radiology')");
        template.execute("INSERT INTO specialties VALUES (2, 'surgery')");
        template.execute("INSERT INTO specialties VALUES (3, 'dentistry')");

        template.execute("INSERT INTO vet_specialties VALUES (2, 1);");
        template.execute("INSERT INTO vet_specialties VALUES (3, 2);");
        template.execute("INSERT INTO vet_specialties VALUES (3, 3);");
        template.execute("INSERT INTO vet_specialties VALUES (4, 2);");
        template.execute("INSERT INTO vet_specialties VALUES (5, 1);");

        template.execute("INSERT INTO types VALUES (1, 'cat');");
        template.execute("INSERT INTO types VALUES (2, 'dog');");
        template.execute("INSERT INTO types VALUES (3, 'lizard');");
        template.execute("INSERT INTO types VALUES (4, 'snake');");
        template.execute("INSERT INTO types VALUES (5, 'bird');");
        template.execute("INSERT INTO types VALUES (6, 'hamster');");

        template
                .execute("INSERT INTO owners VALUES (1, 'Keith', 'Donald', '110 W. Liberty St.', 'Madison', '6085551023');");
        template
                .execute("INSERT INTO owners VALUES (2, 'Keri', 'Donald', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');");
        template
                .execute("INSERT INTO owners VALUES (3, 'Ronald', 'McDonald', '2693 Commerce St.', 'McFarland', '6085558763');");
        template
                .execute("INSERT INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');");
        template
                .execute("INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');");
        template
                .execute("INSERT INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');");
        template.execute("INSERT INTO owners VALUES (7, 'Peter', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');");
        template
                .execute("INSERT INTO owners VALUES (8, 'Scott', 'Escobito', '345 Maple St.', 'Madison', '6085557683');");
        template
                .execute("INSERT INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');");
        template
                .execute("INSERT INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');");

        template.execute("INSERT INTO pets VALUES (1, 'Leo', '2000-09-07', 1, 1)");
        template.execute("INSERT INTO pets VALUES (2, 'Basil', '2002-08-06', 6, 2)");
        template.execute("INSERT INTO pets VALUES (3, 'Rosy', '2001-04-17', 2, 3)");
        template.execute("INSERT INTO pets VALUES (4, 'Jewel', '2000-03-07', 2, 3)");
        template.execute("INSERT INTO pets VALUES (5, 'Iggy', '2000-11-30', 3, 4)");
        template.execute("INSERT INTO pets VALUES (6, 'George', '2000-01-20', 4, 5)");
        template.execute("INSERT INTO pets VALUES (7, 'Samantha', '1995-09-04', 1, 6)");
        template.execute("INSERT INTO pets VALUES (8, 'Max', '1995-09-04', 1, 6)");
        template.execute("INSERT INTO pets VALUES (9, 'Lucky', '1999-08-06', 5, 7)");
        template.execute("INSERT INTO pets VALUES (10, 'Mulligan', '1997-02-24', 2, 8)");
        template.execute("INSERT INTO pets VALUES (11, 'Freddy', '2000-03-09', 5, 9)");
        template.execute("INSERT INTO pets VALUES (12, 'Lucky', '2000-06-24', 2, 10)");
        template.execute("INSERT INTO pets VALUES (13, 'Sly', '2002-06-08', 1, 10)");

        template.execute("INSERT INTO visits VALUES (1, 7, '1996-03-04', 'rabies shot')");
        template.execute("INSERT INTO visits VALUES (2, 8, '1996-03-04', 'rabies shot')");
        template.execute("INSERT INTO visits VALUES (3, 8, '1996-06-04', 'neutered')");
        template.execute("INSERT INTO visits VALUES (4, 7, '1996-09-04', 'spayed')");

        // Data: Spring Security
        template.execute("INSERT INTO users VALUES ('dianne', 'emu', true)");
        template.execute("INSERT INTO users VALUES ('marissa', 'koala', true)");
        template.execute("INSERT INTO users VALUES ('peter', 'opal', false)");
        template.execute("INSERT INTO users VALUES ('scott', 'wombat', true)");
        template.execute("INSERT INTO authorities VALUES ('marissa', 'ROLE_CLINIC_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('dianne', 'ROLE_CLINIC_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('peter', 'ROLE_CLINIC_CUSTOMER')");
        template.execute("INSERT INTO authorities VALUES ('scott', 'ROLE_CLINIC_CUSTOMER')");
    }
}