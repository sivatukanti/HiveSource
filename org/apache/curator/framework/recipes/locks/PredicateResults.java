// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

public class PredicateResults
{
    private final boolean getsTheLock;
    private final String pathToWatch;
    
    public PredicateResults(final String pathToWatch, final boolean getsTheLock) {
        this.pathToWatch = pathToWatch;
        this.getsTheLock = getsTheLock;
    }
    
    public String getPathToWatch() {
        return this.pathToWatch;
    }
    
    public boolean getsTheLock() {
        return this.getsTheLock;
    }
}
