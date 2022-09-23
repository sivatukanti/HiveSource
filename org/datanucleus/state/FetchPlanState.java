// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;

public class FetchPlanState
{
    protected List<String> memberNames;
    
    public FetchPlanState() {
        this.memberNames = new ArrayList<String>();
    }
    
    public void addMemberName(final String memberName) {
        this.memberNames.add(memberName);
    }
    
    public void removeLatestMemberName() {
        this.memberNames.remove(this.memberNames.size() - 1);
    }
    
    public int getCurrentFetchDepth() {
        return this.memberNames.size();
    }
    
    public int getObjectDepthForType(final String memberName) {
        return calculateObjectDepthForMember(this.memberNames, memberName);
    }
    
    protected static int calculateObjectDepthForMember(final List<String> memberNames, final String memberName) {
        final ListIterator iter = memberNames.listIterator(memberNames.size());
        int number = 0;
        while (iter.hasPrevious()) {
            final String field = iter.previous();
            if (!field.equals(memberName)) {
                break;
            }
            ++number;
        }
        return number;
    }
}
