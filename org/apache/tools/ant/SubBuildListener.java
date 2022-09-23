// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public interface SubBuildListener extends BuildListener
{
    void subBuildStarted(final BuildEvent p0);
    
    void subBuildFinished(final BuildEvent p0);
}
