// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.types.Commandline;

public class XNewRmic extends ForkingSunRmic
{
    public static final String COMPILER_NAME = "xnew";
    
    @Override
    protected Commandline setupRmicCommand() {
        final String[] options = { "-Xnew" };
        final Commandline commandline = super.setupRmicCommand(options);
        return commandline;
    }
}
