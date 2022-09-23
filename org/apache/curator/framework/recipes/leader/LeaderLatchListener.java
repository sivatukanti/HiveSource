// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

public interface LeaderLatchListener
{
    void isLeader();
    
    void notLeader();
}
