/*
 * Copyright (c) 2016, Stein Eldar Johnsen
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.morimekta.providence.config;

import net.morimekta.providence.util.TypeRegistry;
import net.morimekta.test.config.Service;
import net.morimekta.util.io.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for the providence config parser.
 */
public class ProvidenceConfigTest {
    private TemporaryFolder temp;
    private TypeRegistry registry;

    @Before
    public void setUp() throws IOException {
        temp = new TemporaryFolder();
        temp.create();

        registry = new TypeRegistry();
        registry.registerRecursively(Service.kDescriptor);
    }

    @After
    public void tearDown() {
        temp.delete();
    }

    @Test
    public void testParseSimple() throws IOException {
        addConfig("/net/morimekta/providence/config/base_service.cfg");
        addConfig("/net/morimekta/providence/config/prod_db.cfg");
        addConfig("/net/morimekta/providence/config/stage_db.cfg");

        File prod = addConfig("/net/morimekta/providence/config/prod.cfg");
        File stage = addConfig("/net/morimekta/providence/config/stage.cfg");

        Map<String,String> params = new HashMap<>();
        params.put("admin_port", "14256");

        ProvidenceConfig config = new ProvidenceConfig(registry, params);
        Service stage_service = config.load(stage);
        Service prod_service = config.load(prod);

        assertEquals("stage", stage_service.getName());
        assertEquals("prod", prod_service.getName());

        assertNotNull(prod_service.getAdmin());
        assertNull(stage_service.getAdmin());

        assertEquals((short) 8080, prod_service.getHttp().getPort());
        assertEquals((short) 14256, prod_service.getAdmin().getPort());
        assertEquals((short) 8080, stage_service.getHttp().getPort());
    }

    private File addConfig(String resource) throws IOException {
        File file = temp.newFile(new File(resource).getName());

        try (OutputStream out = new FileOutputStream(file);
             InputStream in = ProvidenceConfigTest.class.getResourceAsStream(resource)) {
            IOUtils.copy(in, out);
        }

        return file;
    }

    private File addConfig(String resource, File folder) throws IOException {
        File file = new File(folder, new File(resource).getName());

        try (OutputStream out = new FileOutputStream(file);
             InputStream in = ProvidenceConfigTest.class.getResourceAsStream(resource)) {
            IOUtils.copy(in, out);
        }

        return file;
    }

}
