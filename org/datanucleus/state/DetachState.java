// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.datanucleus.util.StringUtils;
import java.util.HashMap;
import org.datanucleus.api.ApiAdapter;
import java.util.Map;

public class DetachState extends FetchPlanState
{
    private Map<Object, Entry> detachedObjectById;
    private ApiAdapter api;
    
    public DetachState(final ApiAdapter api) {
        this.detachedObjectById = new HashMap<Object, Entry>();
        this.api = api;
    }
    
    public void setDetachedCopyEntry(final Object pc, final Object detachedPC) {
        this.detachedObjectById.put(this.getKey(pc), new Entry(detachedPC));
    }
    
    public Entry getDetachedCopyEntry(final Object pc) {
        return this.detachedObjectById.get(this.getKey(pc));
    }
    
    private Object getKey(final Object pc) {
        final Object id = this.api.getIdForObject(pc);
        if (id == null) {
            return StringUtils.toJVMIDString(pc);
        }
        return id;
    }
    
    public class Entry
    {
        private Object detachedPC;
        private List<List<String>> detachStates;
        
        Entry(final Object detachedPC) {
            this.detachStates = new LinkedList<List<String>>();
            this.detachedPC = detachedPC;
            this.detachStates.add(this.getCurrentState());
        }
        
        public Object getDetachedCopyObject() {
            return this.detachedPC;
        }
        
        public boolean checkCurrentState() {
            final List<String> currentState = this.getCurrentState();
            final Iterator<List<String>> iter = this.detachStates.iterator();
            while (iter.hasNext()) {
                final List<String> detachState = iter.next();
                if (this.dominates(detachState, currentState)) {
                    return true;
                }
                if (!this.dominates(currentState, detachState)) {
                    continue;
                }
                iter.remove();
            }
            this.detachStates.add(currentState);
            return false;
        }
        
        private List<String> getCurrentState() {
            return new ArrayList<String>(DetachState.this.memberNames);
        }
        
        private boolean dominates(final List<String> candidate, final List<String> target) {
            if (candidate.size() == 0) {
                return true;
            }
            if (candidate.size() > target.size()) {
                return false;
            }
            final String fieldName = target.get(target.size() - 1);
            return FetchPlanState.calculateObjectDepthForMember(candidate, fieldName) <= FetchPlanState.calculateObjectDepthForMember(target, fieldName);
        }
    }
}
