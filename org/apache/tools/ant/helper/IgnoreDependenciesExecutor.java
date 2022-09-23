// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.helper;

import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Executor;

public class IgnoreDependenciesExecutor implements Executor
{
    private static final SingleCheckExecutor SUB_EXECUTOR;
    
    public void executeTargets(final Project project, final String[] targetNames) throws BuildException {
        final Hashtable<String, Target> targets = project.getTargets();
        BuildException thrownException = null;
        for (int i = 0; i < targetNames.length; ++i) {
            try {
                final Target t = targets.get(targetNames[i]);
                if (t == null) {
                    throw new BuildException("Unknown target " + targetNames[i]);
                }
                t.performTasks();
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
        return IgnoreDependenciesExecutor.SUB_EXECUTOR;
    }
    
    static {
        SUB_EXECUTOR = new SingleCheckExecutor();
    }
}
