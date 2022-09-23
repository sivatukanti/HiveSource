// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public interface Executor
{
    void executeTargets(final Project p0, final String[] p1) throws BuildException;
    
    Executor getSubProjectExecutor();
}
