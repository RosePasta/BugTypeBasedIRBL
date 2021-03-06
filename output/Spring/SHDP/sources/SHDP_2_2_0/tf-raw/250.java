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
package org.springframework.yarn.config.annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.yarn.YarnSystemConstants;
import org.springframework.yarn.config.annotation.EnableYarn.Enable;
import org.springframework.yarn.config.annotation.builders.YarnEnvironmentConfigurer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ClasspathMixedEnvironmentAnnotationTests {

	@Autowired
	private ApplicationContext ctx;

	@Resource(name = YarnSystemConstants.DEFAULT_ID_ENVIRONMENT)
	private Map<String, String> defaultEnvironment;

	@Test
	public void testClasspathMixedEnvironment() throws Exception {
		assertThat(defaultEnvironment, notNullValue());

		assertThat(defaultEnvironment.get("foo"), is("jee"));
		assertThat(defaultEnvironment.get("test.foo.2"), nullValue());
		assertThat(defaultEnvironment.get("myvar1"), is("myvalue1"));

		String classpath = defaultEnvironment.get("CLASSPATH");
		assertThat(classpath, notNullValue());

		// check that there's no extra or empty elements
		assertThat(false, is(classpath.contains("::")));
		assertThat(true, is(classpath.charAt(0) != ':'));
		assertThat(true, is(classpath.charAt(classpath.length()-1) != ':'));
	}

	@Configuration
	@EnableYarn(enable=Enable.BASE)
	static class Config extends SpringYarnConfigurerAdapter {

		@Bean(name="props")
		public Properties getProperties() throws IOException {
			PropertiesFactoryBean fb = new PropertiesFactoryBean();
			fb.setLocation(new ClassPathResource("props.properties"));
			fb.afterPropertiesSet();
			return fb.getObject();
		}

		@Override
		public void configure(YarnEnvironmentConfigurer environment) throws Exception {
				environment
					.withProperties()
						.and()
					.withClasspath()
						.entry("./*")
						.useYarnAppClasspath(true)
						.and()
					.entry("myvar1", "myvalue1")
					.entry("foo", "jee");
		}

	}

}
