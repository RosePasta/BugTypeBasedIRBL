package org.springframework.yarn.support.statemachine;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.yarn.support.statemachine.action.Action;
import org.springframework.yarn.support.statemachine.state.EnumState;
import org.springframework.yarn.support.statemachine.state.State;
import org.springframework.yarn.support.statemachine.transition.DefaultExternalTransition;
import org.springframework.yarn.support.statemachine.transition.DefaultInternalTransition;
import org.springframework.yarn.support.statemachine.transition.Transition;

public class EnumStateMachineTests extends AbstractStateMachineTests {

	@Test
	public void testSimpleStateSwitch() {

		State<TestStates,TestEvents> stateSI = new EnumState<TestStates,TestEvents>(TestStates.SI);
		State<TestStates,TestEvents> stateS1 = new EnumState<TestStates,TestEvents>(TestStates.S1);
		State<TestStates,TestEvents> stateS2 = new EnumState<TestStates,TestEvents>(TestStates.S2);
		State<TestStates,TestEvents> stateS3 = new EnumState<TestStates,TestEvents>(TestStates.S3);

		Collection<State<TestStates,TestEvents>> states = new ArrayList<State<TestStates,TestEvents>>();
		states.add(stateSI);
		states.add(stateS1);
		states.add(stateS2);
		states.add(stateS3);

		Collection<Transition<TestStates,TestEvents>> transitions = new ArrayList<Transition<TestStates,TestEvents>>();

		Collection<Action> actionsFromSIToS1 = new ArrayList<Action>();
		actionsFromSIToS1.add(new LoggingAction("actionsFromSIToS1"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromSIToS1 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateSI, stateS1, actionsFromSIToS1, TestEvents.E1);

		Collection<Action> actionsFromS1ToS2 = new ArrayList<Action>();
		actionsFromS1ToS2.add(new LoggingAction("actionsFromS1ToS2"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromS1ToS2 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateS1, stateS2, actionsFromS1ToS2, TestEvents.E2);

		Collection<Action> actionsFromS2ToS3 = new ArrayList<Action>();
		actionsFromS1ToS2.add(new LoggingAction("actionsFromS2ToS3"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromS2ToS3 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateS2, stateS3, actionsFromS2ToS3, TestEvents.E3);

		transitions.add(transitionFromSIToS1);
		transitions.add(transitionFromS1ToS2);
		transitions.add(transitionFromS2ToS3);

		SyncTaskExecutor taskExecutor = new SyncTaskExecutor();
		EnumStateMachine<TestStates, TestEvents> machine = new EnumStateMachine<TestStates, TestEvents>(states, transitions, stateSI);
		machine.setTaskExecutor(taskExecutor);
		machine.start();

		State<TestStates,TestEvents> initialState = machine.getInitialState();
		assertThat(initialState, is(stateSI));

		State<TestStates,TestEvents> state = machine.getState();
		assertThat(state, is(stateSI));

		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
		state = machine.getState();
		assertThat(state, is(stateS1));

		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
		state = machine.getState();
		assertThat(state, is(stateS2));

		// not processed
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
		state = machine.getState();
		assertThat(state, is(stateS2));

		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
		state = machine.getState();
		assertThat(state, is(stateS3));
	}

	@Test
	public void testDeferredEvents() {

		Collection<TestEvents> deferred = new ArrayList<TestEvents>();
		deferred.add(TestEvents.E2);
		deferred.add(TestEvents.E3);

		// states
		State<TestStates,TestEvents> stateSI = new EnumState<TestStates,TestEvents>(TestStates.SI, deferred);
		State<TestStates,TestEvents> stateS1 = new EnumState<TestStates,TestEvents>(TestStates.S1);
		State<TestStates,TestEvents> stateS2 = new EnumState<TestStates,TestEvents>(TestStates.S2);
		State<TestStates,TestEvents> stateS3 = new EnumState<TestStates,TestEvents>(TestStates.S3);

		Collection<State<TestStates,TestEvents>> states = new ArrayList<State<TestStates,TestEvents>>();
		states.add(stateSI);
		states.add(stateS1);
		states.add(stateS2);
		states.add(stateS3);

		// transitions
		Collection<Transition<TestStates,TestEvents>> transitions = new ArrayList<Transition<TestStates,TestEvents>>();

		Collection<Action> actionsFromSIToS1 = new ArrayList<Action>();
		actionsFromSIToS1.add(new LoggingAction("actionsFromSIToS1"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromSIToS1 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateSI, stateS1, actionsFromSIToS1, TestEvents.E1);

		Collection<Action> actionsFromS1ToS2 = new ArrayList<Action>();
		actionsFromS1ToS2.add(new LoggingAction("actionsFromS1ToS2"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromS1ToS2 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateS1, stateS2, actionsFromS1ToS2, TestEvents.E2);

		Collection<Action> actionsFromS2ToS3 = new ArrayList<Action>();
		actionsFromS1ToS2.add(new LoggingAction("actionsFromS2ToS3"));
		DefaultExternalTransition<TestStates,TestEvents> transitionFromS2ToS3 =
				new DefaultExternalTransition<TestStates,TestEvents>(stateS2, stateS3, actionsFromS2ToS3, TestEvents.E3);

		transitions.add(transitionFromSIToS1);
		transitions.add(transitionFromS1ToS2);
		transitions.add(transitionFromS2ToS3);

		// create machine
		SyncTaskExecutor taskExecutor = new SyncTaskExecutor();
		EnumStateMachine<TestStates, TestEvents> machine = new EnumStateMachine<TestStates, TestEvents>(states, transitions, stateSI);
//		StateMachine<State<TestStates, TestEvents>, TestEvents> machine2 = new EnumStateMachine<TestStates, TestEvents>(states, transitions, stateSI);
		machine.setTaskExecutor(taskExecutor);
		machine.start();

		State<TestStates,TestEvents> initialState = machine.getInitialState();
		assertThat(initialState, is(stateSI));

		State<TestStates,TestEvents> state = machine.getState();
		assertThat(state, is(stateSI));


		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E2).build());
		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E3).build());
		state = machine.getState();
		assertThat(state, is(stateSI));


		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
		state = machine.getState();
		assertThat(state, is(stateS3));
	}

	@Test
	public void testInternalTransitions() {
		State<TestStates,TestEvents> stateSI = new EnumState<TestStates,TestEvents>(TestStates.SI);

		Collection<State<TestStates,TestEvents>> states = new ArrayList<State<TestStates,TestEvents>>();
		states.add(stateSI);

		Collection<Action> actionsInSI = new ArrayList<Action>();
		actionsInSI.add(new LoggingAction("actionsInSI"));
		DefaultInternalTransition<TestStates,TestEvents> transitionInternalSI =
				new DefaultInternalTransition<TestStates,TestEvents>(stateSI, actionsInSI, TestEvents.E1);

		// transitions
		Collection<Transition<TestStates,TestEvents>> transitions = new ArrayList<Transition<TestStates,TestEvents>>();
		transitions.add(transitionInternalSI);

		SyncTaskExecutor taskExecutor = new SyncTaskExecutor();
		EnumStateMachine<TestStates, TestEvents> machine = new EnumStateMachine<TestStates, TestEvents>(states, transitions, stateSI);
		machine.setTaskExecutor(taskExecutor);
		machine.start();

		machine.sendEvent(MessageBuilder.withPayload(TestEvents.E1).build());
	}

	private static class LoggingAction implements Action {

		private static final Log log = LogFactory.getLog(LoggingAction.class);

		private String message;

		public LoggingAction(String message) {
			this.message = message;
		}

		@Override
		public void execute(MessageHeaders headers) {
			log.info("Hello from LoggingAction " + message);
		}

	}

}
