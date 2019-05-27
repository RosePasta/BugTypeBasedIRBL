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
package org.springframework.yarn.support.statemachine;

import java.util.Collection;

import org.springframework.yarn.support.statemachine.state.State;
import org.springframework.yarn.support.statemachine.transition.Transition;

public class EnumStateMachine<S extends Enum<S>, E extends Enum<E>> extends AbstractStateMachine<S, E> {

	public EnumStateMachine(Collection<State<S,E>> states, Collection<Transition<S,E>> transitions, State<S,E> initialState) {
		super(states, transitions, initialState);
	}


}
