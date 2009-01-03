/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbmaintain.dbsupport;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 3-jan-2009
 */
public class DbMaintainDataSourceInvocationHandlerTest {

    @Test
    public void testGetConnection() throws SQLException {
        String url = "jdbc:hsqldb:mem:dbmaintain";
        DataSource dbmaintainDataSource = DbMaintainDataSource.createDataSource("org.hsqldb.jdbcDriver", url, "sa", "");
        Connection conn = dbmaintainDataSource.getConnection();
        assertEquals(url, conn.getMetaData().getURL());        
    }
}
