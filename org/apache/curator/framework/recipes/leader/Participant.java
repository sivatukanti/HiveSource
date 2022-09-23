// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

public class Participant
{
    private final String id;
    private final boolean isLeader;
    
    public Participant(final String id, final boolean leader) {
        this.id = id;
        this.isLeader = leader;
    }
    
    Participant() {
        this("", false);
    }
    
    public String getId() {
        return this.id;
    }
    
    public boolean isLeader() {
        return this.isLeader;
    }
    
    @Override
    public String toString() {
        return "Participant{id='" + this.id + '\'' + ", isLeader=" + this.isLeader + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Participant that = (Participant)o;
        return this.isLeader == that.isLeader && this.id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + (this.isLeader ? 1 : 0);
        return result;
    }
}
