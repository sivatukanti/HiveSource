// 
// Decompiled by Procyon v0.5.36
// 

package antlr.build;

import java.io.IOException;
import java.io.File;
import antlr.Utils;

public class Tool
{
    public String os;
    
    public Tool() {
        this.os = null;
        this.os = System.getProperty("os.name");
    }
    
    public static void main(final String[] array) {
        if (array.length != 1) {
            System.err.println("usage: java antlr.build.Tool action");
            return;
        }
        new Tool().perform("antlr.build.ANTLR", array[0]);
    }
    
    public void perform(final String s, final String name) {
        if (s == null || name == null) {
            this.error("missing app or action");
            return;
        }
        Class loadClass = null;
        Object instance = null;
        try {
            instance = Utils.createInstanceOf(s);
        }
        catch (Exception ex) {
            try {
                if (!s.startsWith("antlr.build.")) {
                    loadClass = Utils.loadClass("antlr.build." + s);
                }
                this.error("no such application " + s, ex);
            }
            catch (Exception ex2) {
                this.error("no such application " + s, ex2);
            }
        }
        if (loadClass == null || instance == null) {
            return;
        }
        try {
            loadClass.getMethod(name, Tool.class).invoke(instance, this);
        }
        catch (Exception ex3) {
            this.error("no such action for application " + s, ex3);
        }
    }
    
    public void system(final String s) {
        final Runtime runtime = Runtime.getRuntime();
        try {
            this.log(s);
            Process process;
            if (!this.os.startsWith("Windows")) {
                process = runtime.exec(new String[] { "sh", "-c", s });
            }
            else {
                process = runtime.exec(s);
            }
            final StreamScarfer streamScarfer = new StreamScarfer(process.getErrorStream(), "stderr", this);
            final StreamScarfer streamScarfer2 = new StreamScarfer(process.getInputStream(), "stdout", this);
            streamScarfer.start();
            streamScarfer2.start();
            process.waitFor();
        }
        catch (Exception ex) {
            this.error("cannot exec " + s, ex);
        }
    }
    
    public void antlr(final String str) {
        String s = null;
        try {
            s = new File(str).getParent();
            if (s != null) {
                s = new File(s).getCanonicalPath();
            }
        }
        catch (IOException ex) {
            this.error("Invalid grammar file: " + str);
        }
        if (s != null) {
            this.log("java antlr.Tool -o " + s + " " + str);
            new antlr.Tool().doEverything(new String[] { "-o", s, str });
        }
    }
    
    public void stdout(final String x) {
        System.out.println(x);
    }
    
    public void stderr(final String x) {
        System.err.println(x);
    }
    
    public void error(final String str) {
        System.err.println("antlr.build.Tool: " + str);
    }
    
    public void log(final String str) {
        System.out.println("executing: " + str);
    }
    
    public void error(final String str, final Exception ex) {
        System.err.println("antlr.build.Tool: " + str);
        ex.printStackTrace(System.err);
    }
}
