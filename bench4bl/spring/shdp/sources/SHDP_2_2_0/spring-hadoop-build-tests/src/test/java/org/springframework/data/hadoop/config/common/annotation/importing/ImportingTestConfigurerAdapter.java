/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.config.common.annotation.importing;

import org.springframework.data.hadoop.config.common.annotation.AnnotationBuilder;

public class ImportingTestConfigurerAdapter implements ImportingTestConfigurer {

	@Override
	public void init(ImportingTestConfigBuilder builder) throws Exception {
	}

	@Override
	public void configure(ImportingTestConfigBuilder builder) throws Exception {
	}

	@Override
	public boolean isAssignable(AnnotationBuilder<ImportingTestConfig> builder) {
		return builder instanceof ImportingTestConfigBuilder;
	}

}
