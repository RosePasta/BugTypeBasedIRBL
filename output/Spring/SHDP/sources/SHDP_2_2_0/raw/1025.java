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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.yarn.support.statemachine.action.Action;
import org.springframework.yarn.support.statemachine.config.EnableStateMachine;
import org.springframework.yarn.support.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.yarn.support.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.yarn.support.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.yarn.support.statemachine.listener.StateMachineListener;
import org.springframework.yarn.support.statemachine.state.State;

public class ListenerTests extends AbstractStateMachineTests {

	@Test
	public void testStateEvents() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		assertTrue(ctx.containsBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE));
		@SuppressWarnings("unchecked")
		EnumStateMachine<TestStates,TestEvents> machine =
				ctx.getBean(StateMachineSystemConstants.DEFAULT_ID_STATEMACHINE, EnumStateMachine.class);

		TestStateMachineListener listener = new TestStateMachineListener();
		machine.addStateListener(listener);

		assertThat(machine, notNullValue());
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).setHeader("foo", "jee1").build());
		assertThat(listener.states.size(), is(1));
		assertThat(listener.states.get(0).from.getId(), is(TestStates.S1));
		assertThat(listener.states.get(0).to.getId(), is(TestStates.S2));
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).setHeader("foo", "jee2").build());
		assertThat(listener.states.size(), is(2));
		assertThat(listener.states.get(1).from.getId(), is(TestStates.S2));
		assertThat(listener.states.get(1).to.getId(), is(TestStates.S3));
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E4).setHeader("foo", "jee2").build());
		assertThat(listener.states.size(), is(2));

		ctx.close();
	}

	private static class LoggingAction implements Action {

		private static final Log log = LogFactory.getLog(LoggingAction.class);

		private String message;

		public LoggingAction(String message) {
			this.message = message;
		}

		@Override
		public void execute(MessageHeaders headers) {
			log.info("Hello from LoggingAction " + message + " foo=" + headers.get("foo"));
		}

	}

	private static class TestStateMachineListener implements StateMachineListener<State<TestStates, TestEvents>, TestEvents> {

		ArrayList<Holder> states = new ArrayList<Holder>();

		@Override
		public void stateChanged(State<TestStates, TestEvents> from, State<TestStates, TestEvents> to) {
			states.add(new Holder(from, to));
		}

		static class Holder {
			State<TestStates, TestEvents> from;
			State<TestStates, TestEvents> to;
			public Holder(State<TestStates, TestEvents> from, State<TestStates, TestEvents> to) {
				this.from = from;
				this.to = to;
			}
		}

	}

	@Configuration
	@EnableStateMachine
	static class Config extends EnumStateMachineConfigurerAdapter<TestStates, TestEvents> {

		@Override
		public void configure(StateMachineStateConfigurer<TestStates, TestEvents> states) throws Exception {
			states
				.withStates()
					.initial(TestStates.S1)
					.state(TestStates.S1)
					.state(TestStates.S2)
					.state(TestStates.S3, TestEvents.E4)
					.state(TestStates.S4);
		}

		@Override
		public void configure(StateMachineTransitionConfigurer<TestStates, TestEvents> transitions) throws Exception {
			transitions
				.withExternal()
					.source(TestStates.S1)
					.target(TestStates.S2)
					.event(TestEvents.E1)
					.action(loggingAction())
					.action(loggingAction())
					.and()
				.withExternal()
					.source(TestStates.S2)
					.target(TestStates.S3)
					.event(TestEvents.E2)
					.action(loggingAction())
					.and()
				.withExternal()
					.source(TestStates.S3)
					.target(TestStates.S4)
					.event(TestEvents.E3)
					.action(loggingAction())
					.and()
				.withExternal()
					.source(TestStates.S4)
					.target(TestStates.S3)
					.event(TestEvents.E4)
					.action(loggingAction());
		}

		@Bean
		public LoggingAction loggingAction() {
			return new LoggingAction("as bean");
		}

		@Bean
		public TaskExecutor taskExecutor() {
			return new SyncTaskExecutor();
		}

	}

}
