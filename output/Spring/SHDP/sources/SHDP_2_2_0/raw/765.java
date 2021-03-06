/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.yarn.am.grid.support;

import org.apache.hadoop.yarn.api.records.Container;
import org.springframework.yarn.am.grid.GridMember;

/**
 * Default implementation of {@link GridMember}.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultGridMember extends AbstractGridMember {

	/**
	 * Instantiates a new default grid member.
	 *
	 * @param container the container
	 */
	public DefaultGridMember(Container container) {
		super(container);
	}

}
