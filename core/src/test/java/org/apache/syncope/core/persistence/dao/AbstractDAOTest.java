/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.dao;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:syncopeContext.xml", "classpath:persistenceContext.xml",
    "classpath:schedulingContext.xml", "classpath:workflowContext.xml"})
public abstract class AbstractDAOTest {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractDAOTest.class);

    protected static String connidSoapVersion;

    protected static String bundlesDirectory;

    @BeforeClass
    public static void setUpIdentityConnectorsBundles() throws IOException {
        Properties props = new Properties();
        InputStream propStream = null;
        try {
            propStream = AbstractDAOTest.class.getResourceAsStream("/bundles.properties");
            props.load(propStream);
            connidSoapVersion = props.getProperty("connid.soap.version");
            bundlesDirectory = props.getProperty("bundles.directory");
        } catch (Exception e) {
            LOG.error("Could not load bundles.properties", e);
        } finally {
            if (propStream != null) {
                propStream.close();
            }
        }
        assertNotNull(connidSoapVersion);
        assertNotNull(bundlesDirectory);
    }
}
