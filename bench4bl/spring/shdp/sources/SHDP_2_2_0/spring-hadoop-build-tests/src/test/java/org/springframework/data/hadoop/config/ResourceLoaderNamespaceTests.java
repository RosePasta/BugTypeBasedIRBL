/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.data.hadoop.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.hadoop.HadoopSystemConstants;
import org.springframework.data.hadoop.TestUtils;
import org.springframework.data.hadoop.fs.HdfsResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for 'resource-loader' element.
 *
 * @author Janne Valkealahti
 *
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ResourceLoaderNamespaceTests {

	@Resource(name = HadoopSystemConstants.DEFAULT_ID_RESOURCE_LOADER)
	private HdfsResourceLoader loader;

	@Resource(name = "loaderWithUser")
	private HdfsResourceLoader loaderWithUser;

	@Test
	public void testWithDefault() throws Exception {
		String user = TestUtils.readField("impersonatedUser", loader);
		assertThat(user, nullValue());
	}

	@Test
	public void testWithUser() throws Exception {
		String user = TestUtils.readField("impersonatedUser", loaderWithUser);
		assertThat(user, notNullValue());
		assertThat(user, is("usernotexist"));
	}

}
