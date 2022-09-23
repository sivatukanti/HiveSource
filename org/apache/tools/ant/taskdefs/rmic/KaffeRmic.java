// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;

public class KaffeRmic extends DefaultRmicAdapter
{
    private static final String[] RMIC_CLASSNAMES;
    public static final String COMPILER_NAME = "kaffe";
    
    public boolean execute() throws BuildException {
        this.getRmic().log("Using Kaffe rmic", 3);
        final Commandline cmd = this.setupRmicCommand();
        final Class c = getRmicClass();
        if (c == null) {
            final StringBuffer buf = new StringBuffer("Cannot use Kaffe rmic, as it is not available.  None of ");
            for (int i = 0; i < KaffeRmic.RMIC_CLASSNAMES.length; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                buf.append(KaffeRmic.RMIC_CLASSNAMES[i]);
            }
            buf.append(" have been found. A common solution is to set the environment variable JAVA_HOME or CLASSPATH.");
            throw new BuildException(buf.toString(), this.getRmic().getLocation());
        }
        cmd.setExecutable(c.getName());
        if (!c.getName().equals(KaffeRmic.RMIC_CLASSNAMES[KaffeRmic.RMIC_CLASSNAMES.length - 1])) {
            cmd.createArgument().setValue("-verbose");
            this.getRmic().log(Commandline.describeCommand(cmd));
        }
        final ExecuteJava ej = new ExecuteJava();
        ej.setJavaCommand(cmd);
        return ej.fork(this.getRmic()) == 0;
    }
    
    public static boolean isAvailable() {
        return getRmicClass() != null;
    }
    
    private static Class getRmicClass() {
        int i = 0;
        while (i < KaffeRmic.RMIC_CLASSNAMES.length) {
            try {
                return Class.forName(KaffeRmic.RMIC_CLASSNAMES[i]);
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
        RMIC_CLASSNAMES = new String[] { "gnu.classpath.tools.rmi.rmic.RMIC", "gnu.java.rmi.rmic.RMIC", "kaffe.rmi.rmic.RMIC" };
    }
}
