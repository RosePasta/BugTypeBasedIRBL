/*
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.pig;

import java.util.Properties;

import javax.annotation.Resource;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.hadoop.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Costin Leau
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/org/springframework/data/hadoop/pig/basic.xml")
public class PigTest {

	@Resource(name = "pig-raw")
	private PigServer pig;

	@Autowired
	private ApplicationContext ctx;

	{
		TestUtils.hackHadoopStagingOnWin();
	}

	@After
	public void cleanup() throws Exception {
		pig.shutdown();
	}

	@Test
	public void testPig() throws Exception {
		pig.registerQuery("A = LOAD 'foo.txt' AS (key, value);");
		assertFalse(pig.isBatchOn());
	}

	@Test
	public void testServerNamespace() throws Exception {
		String defaultName = "pig";
		assertTrue(ctx.containsBean(defaultName));
		PigServer server = ctx.getBean(defaultName, PigServer.class);
		Properties props = server.getPigContext().getProperties();
		assertEquals("blue", props.get("ivy"));
		assertEquals(ExecType.LOCAL, server.getPigContext().getExecType());
	}

	@Test
	public void testPigProperties() throws Exception {
		PigServer pig = ctx.getBean("pig", PigServer.class);
		Properties props = pig.getPigContext().getProperties();
		assertEquals("blue", props.get("ivy"));

		assertEquals("chasing", props.get("star"));
		assertEquals("captain eo", props.get("return"));
		assertEquals("last", props.get("train"));
		assertEquals("the dream", props.get("dancing"));
		assertEquals("in the mirror", props.get("tears"));
		assertEquals("eo", props.get("captain"));
	}
}
