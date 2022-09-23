// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class StateMachineFactory<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT>
{
    private final TransitionsListNode transitionsListNode;
    private Map<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>> stateMachineTable;
    private STATE defaultInitialState;
    private final boolean optimized;
    
    public StateMachineFactory(final STATE defaultInitialState) {
        this.transitionsListNode = null;
        this.defaultInitialState = defaultInitialState;
        this.optimized = false;
        this.stateMachineTable = null;
    }
    
    private StateMachineFactory(final StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that, final ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> t) {
        this.defaultInitialState = that.defaultInitialState;
        this.transitionsListNode = new TransitionsListNode(t, that.transitionsListNode);
        this.optimized = false;
        this.stateMachineTable = null;
    }
    
    private StateMachineFactory(final StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that, final boolean optimized) {
        this.defaultInitialState = that.defaultInitialState;
        this.transitionsListNode = that.transitionsListNode;
        this.optimized = optimized;
        if (optimized) {
            this.makeStateMachineTable();
        }
        else {
            this.stateMachineTable = null;
        }
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(final STATE preState, final STATE postState, final EVENTTYPE eventType) {
        return this.addTransition(preState, postState, eventType, null);
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(final STATE preState, final STATE postState, final Set<EVENTTYPE> eventTypes) {
        return this.addTransition(preState, postState, eventTypes, null);
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(final STATE preState, final STATE postState, final Set<EVENTTYPE> eventTypes, final SingleArcTransition<OPERAND, EVENT> hook) {
        StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> factory = null;
        for (final EVENTTYPE event : eventTypes) {
            if (factory == null) {
                factory = this.addTransition(preState, postState, event, hook);
            }
            else {
                factory = factory.addTransition(preState, postState, event, hook);
            }
        }
        return factory;
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(final STATE preState, final STATE postState, final EVENTTYPE eventType, final SingleArcTransition<OPERAND, EVENT> hook) {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this, new ApplicableSingleOrMultipleTransition<OPERAND, STATE, EVENTTYPE, EVENT>(preState, eventType, new SingleInternalArc(postState, hook)));
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(final STATE preState, final Set<STATE> postStates, final EVENTTYPE eventType, final MultipleArcTransition<OPERAND, EVENT, STATE> hook) {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this, new ApplicableSingleOrMultipleTransition<OPERAND, STATE, EVENTTYPE, EVENT>(preState, eventType, new MultipleInternalArc(postStates, hook)));
    }
    
    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> installTopology() {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this, true);
    }
    
    private STATE doTransition(final OPERAND operand, final STATE oldState, final EVENTTYPE eventType, final EVENT event) throws InvalidStateTransitonException {
        final Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> transitionMap = this.stateMachineTable.get(oldState);
        if (transitionMap != null) {
            final Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition = transitionMap.get(eventType);
            if (transition != null) {
                return transition.doTransition(operand, oldState, event, eventType);
            }
        }
        throw new InvalidStateTransitonException(oldState, eventType);
    }
    
    private synchronized void maybeMakeStateMachineTable() {
        if (this.stateMachineTable == null) {
            this.makeStateMachineTable();
        }
    }
    
    private void makeStateMachineTable() {
        final Stack<ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT>> stack = new Stack<ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT>>();
        final Map<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>> prototype = new HashMap<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>>();
        prototype.put(this.defaultInitialState, null);
        this.stateMachineTable = new EnumMap<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>>(prototype);
        for (TransitionsListNode cursor = this.transitionsListNode; cursor != null; cursor = cursor.next) {
            stack.push(cursor.transition);
        }
        while (!stack.isEmpty()) {
            stack.pop().apply(this);
        }
    }
    
    public StateMachine<STATE, EVENTTYPE, EVENT> make(final OPERAND operand, final STATE initialState) {
        return new InternalStateMachine(operand, initialState);
    }
    
    public StateMachine<STATE, EVENTTYPE, EVENT> make(final OPERAND operand) {
        return new InternalStateMachine(operand, this.defaultInitialState);
    }
    
    public Graph generateStateGraph(final String name) {
        this.maybeMakeStateMachineTable();
        final Graph g = new Graph(name);
        for (final STATE startState : this.stateMachineTable.keySet()) {
            final Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> transitions = this.stateMachineTable.get(startState);
            for (final Map.Entry<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> entry : transitions.entrySet()) {
                final Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition = entry.getValue();
                if (transition instanceof SingleInternalArc) {
                    final SingleInternalArc sa = (SingleInternalArc)transition;
                    final Graph.Node fromNode = g.getNode(startState.toString());
                    final Graph.Node toNode = g.getNode(sa.postState.toString());
                    fromNode.addEdge(toNode, entry.getKey().toString());
                }
                else {
                    if (!(transition instanceof MultipleInternalArc)) {
                        continue;
                    }
                    final MultipleInternalArc ma = (MultipleInternalArc)transition;
                    final Iterator iter = ma.validPostStates.iterator();
                    while (iter.hasNext()) {
                        final Graph.Node fromNode2 = g.getNode(startState.toString());
                        final Graph.Node toNode2 = g.getNode(iter.next().toString());
                        fromNode2.addEdge(toNode2, entry.getKey().toString());
                    }
                }
            }
        }
        return g;
    }
    
    private class TransitionsListNode
    {
        final ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> transition;
        final TransitionsListNode next;
        
        TransitionsListNode(final ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> transition, final TransitionsListNode next) {
            this.transition = transition;
            this.next = next;
        }
    }
    
    private static class ApplicableSingleOrMultipleTransition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT> implements ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT>
    {
        final STATE preState;
        final EVENTTYPE eventType;
        final Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition;
        
        ApplicableSingleOrMultipleTransition(final STATE preState, final EVENTTYPE eventType, final Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition) {
            this.preState = preState;
            this.eventType = eventType;
            this.transition = transition;
        }
        
        @Override
        public void apply(final StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> subject) {
            Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> transitionMap = ((StateMachineFactory<Object, Enum, Enum, Object>)subject).stateMachineTable.get(this.preState);
            if (transitionMap == null) {
                transitionMap = new HashMap<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>();
                ((StateMachineFactory<Object, Enum, Enum, Object>)subject).stateMachineTable.put(this.preState, transitionMap);
            }
            transitionMap.put(this.eventType, this.transition);
        }
    }
    
    private class SingleInternalArc implements Transition<OPERAND, STATE, EVENTTYPE, EVENT>
    {
        private STATE postState;
        private SingleArcTransition<OPERAND, EVENT> hook;
        
        SingleInternalArc(final STATE postState, final SingleArcTransition<OPERAND, EVENT> hook) {
            this.postState = postState;
            this.hook = hook;
        }
        
        @Override
        public STATE doTransition(final OPERAND operand, final STATE oldState, final EVENT event, final EVENTTYPE eventType) {
            if (this.hook != null) {
                this.hook.transition(operand, event);
            }
            return this.postState;
        }
    }
    
    private class MultipleInternalArc implements Transition<OPERAND, STATE, EVENTTYPE, EVENT>
    {
        private Set<STATE> validPostStates;
        private MultipleArcTransition<OPERAND, EVENT, STATE> hook;
        
        MultipleInternalArc(final Set<STATE> postStates, final MultipleArcTransition<OPERAND, EVENT, STATE> hook) {
            this.validPostStates = postStates;
            this.hook = hook;
        }
        
        @Override
        public STATE doTransition(final OPERAND operand, final STATE oldState, final EVENT event, final EVENTTYPE eventType) throws InvalidStateTransitonException {
            final STATE postState = this.hook.transition(operand, event);
            if (!this.validPostStates.contains(postState)) {
                throw new InvalidStateTransitonException(oldState, eventType);
            }
            return postState;
        }
    }
    
    private class InternalStateMachine implements StateMachine<STATE, EVENTTYPE, EVENT>
    {
        private final OPERAND operand;
        private STATE currentState;
        
        InternalStateMachine(final OPERAND operand, final STATE initialState) {
            this.operand = operand;
            this.currentState = initialState;
            if (!StateMachineFactory.this.optimized) {
                StateMachineFactory.this.maybeMakeStateMachineTable();
            }
        }
        
        @Override
        public synchronized STATE getCurrentState() {
            return this.currentState;
        }
        
        @Override
        public synchronized STATE doTransition(final EVENTTYPE eventType, final EVENT event) throws InvalidStateTransitonException {
            return this.currentState = (STATE)StateMachineFactory.this.doTransition(this.operand, this.currentState, eventType, event);
        }
    }
    
    private interface Transition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT>
    {
        STATE doTransition(final OPERAND p0, final STATE p1, final EVENT p2, final EVENTTYPE p3);
    }
    
    private interface ApplicableTransition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT>
    {
        void apply(final StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> p0);
    }
}
