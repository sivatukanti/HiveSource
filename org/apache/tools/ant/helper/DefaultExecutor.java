// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Executor;

public class DefaultExecutor implements Executor
{
    private static final SingleCheckExecutor SUB_EXECUTOR;
    
    public void executeTargets(final Project project, final String[] targetNames) throws BuildException {
        BuildException thrownException = null;
        for (int i = 0; i < targetNames.length; ++i) {
            try {
                project.executeTarget(targetNames[i]);
            }
            catch (BuildException ex) {
                if (!project.isKeepGoingMode()) {
                    throw ex;
                }
                thrownException = ex;
            }
        }
        if (thrownException != null) {
            throw thrownException;
        }
    }
    
    public Executor getSubProjectExecutor() {
        return DefaultExecutor.SUB_EXECUTOR;
    }
    
    static {
        SUB_EXECUTOR = new SingleCheckExecutor();
    }
}
