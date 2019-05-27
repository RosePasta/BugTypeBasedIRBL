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
package org.springframework.data.hadoop.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.hadoop.fs.HdfsResourceLoader;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Costin Leau
 */
class HadoopResourceLoaderParser extends AbstractImprovedSimpleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return HdfsResourceLoader.class;
	}

	@Override
	protected String defaultId(ParserContext context, Element element) {
		return "hadoopResourceLoader";
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		// set depends-on
		String depends = element.getAttribute(BeanDefinitionParserDelegate.DEPENDS_ON_ATTRIBUTE);
		if (StringUtils.hasText(depends)) {
			builder.getRawBeanDefinition().setDependsOn(StringUtils.tokenizeToStringArray(depends, BeanDefinitionParserDelegate.BEAN_NAME_DELIMITERS));
		}

		String fs = element.getAttribute("file-system-ref");
		// get configuration
		String config = element.getAttribute("configuration-ref");
		// get uri (if available)
		String uri = element.getAttribute("uri");

		if (StringUtils.hasText(fs)) {
			if (StringUtils.hasText(uri)) {
				parserContext.getReaderContext().error("cannot specify both 'uri' and a the file system; use only one",
						element);
			}
			builder.addConstructorArgReference(fs.trim());
		}
		else {
			builder.addConstructorArgReference(config.trim());

			if (StringUtils.hasText(uri)) {
				builder.addConstructorArgValue(uri);
			}
		}

		String useCodecs = element.getAttribute("use-codecs");
		if (StringUtils.hasText(useCodecs)) {
			builder.addPropertyValue("useCodecs", useCodecs);
		}

		postProcess(builder, element);
	}
}
