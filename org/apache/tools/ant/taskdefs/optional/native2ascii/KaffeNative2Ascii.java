// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;

public final class KaffeNative2Ascii extends DefaultNative2Ascii
{
    private static final String[] N2A_CLASSNAMES;
    public static final String IMPLEMENTATION_NAME = "kaffe";
    
    @Override
    protected void setup(final Commandline cmd, final Native2Ascii args) throws BuildException {
        if (args.getReverse()) {
            throw new BuildException("-reverse is not supported by Kaffe");
        }
        super.setup(cmd, args);
    }
    
    @Override
    protected boolean run(final Commandline cmd, final ProjectComponent log) throws BuildException {
        final ExecuteJava ej = new ExecuteJava();
        final Class c = getN2aClass();
        if (c == null) {
            throw new BuildException("Couldn't load Kaffe's Native2Ascii class");
        }
        cmd.setExecutable(c.getName());
        ej.setJavaCommand(cmd);
        ej.execute(log.getProject());
        return true;
    }
    
    private static Class getN2aClass() {
        int i = 0;
        while (i < KaffeNative2Ascii.N2A_CLASSNAMES.length) {
            try {
                return Class.forName(KaffeNative2Ascii.N2A_CLASSNAMES[i]);
            }
            catch (ClassNotFoundException cnfe) {
                ++i;
                continue;
            }
            break;
        }
        return null;
    }
    
    static {
        N2A_CLASSNAMES = new String[] { "gnu.classpath.tools.native2ascii.Native2ASCII", "kaffe.tools.native2ascii.Native2Ascii" };
    }
}
