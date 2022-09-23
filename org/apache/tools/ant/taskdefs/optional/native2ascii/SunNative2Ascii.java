// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.lang.reflect.Method;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;

public final class SunNative2Ascii extends DefaultNative2Ascii
{
    public static final String IMPLEMENTATION_NAME = "sun";
    
    @Override
    protected void setup(final Commandline cmd, final Native2Ascii args) throws BuildException {
        if (args.getReverse()) {
            cmd.createArgument().setValue("-reverse");
        }
        super.setup(cmd, args);
    }
    
    @Override
    protected boolean run(final Commandline cmd, final ProjectComponent log) throws BuildException {
        try {
            final Class n2aMain = Class.forName("sun.tools.native2ascii.Main");
            final Class[] param = { String[].class };
            final Method convert = n2aMain.getMethod("convert", (Class[])param);
            if (convert == null) {
                throw new BuildException("Could not find convert() method in sun.tools.native2ascii.Main");
            }
            final Object o = n2aMain.newInstance();
            return (boolean)convert.invoke(o, cmd.getArguments());
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new BuildException("Error starting Sun's native2ascii: ", ex2);
        }
    }
}
