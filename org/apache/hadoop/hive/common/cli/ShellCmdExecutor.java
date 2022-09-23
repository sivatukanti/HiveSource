// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.cli;

import java.io.IOException;
import org.apache.hive.common.util.StreamPrinter;
import java.io.PrintStream;

public class ShellCmdExecutor
{
    private String cmd;
    private PrintStream out;
    private PrintStream err;
    
    public ShellCmdExecutor(final String cmd, final PrintStream out, final PrintStream err) {
        this.cmd = cmd;
        this.out = out;
        this.err = err;
    }
    
    public int execute() throws Exception {
        try {
            final Process executor = Runtime.getRuntime().exec(this.cmd);
            final StreamPrinter outPrinter = new StreamPrinter(executor.getInputStream(), null, this.out);
            final StreamPrinter errPrinter = new StreamPrinter(executor.getErrorStream(), null, this.err);
            outPrinter.start();
            errPrinter.start();
            final int ret = executor.waitFor();
            outPrinter.join();
            errPrinter.join();
            return ret;
        }
        catch (IOException ex) {
            throw new Exception("Failed to execute " + this.cmd, ex);
        }
    }
}
