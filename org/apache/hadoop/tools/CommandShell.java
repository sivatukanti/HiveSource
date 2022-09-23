// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools;

import java.io.PrintStream;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

public abstract class CommandShell extends Configured implements Tool
{
    private PrintStream out;
    private PrintStream err;
    private SubCommand subcommand;
    
    public CommandShell() {
        this.out = System.out;
        this.err = System.err;
        this.subcommand = null;
    }
    
    public abstract String getCommandUsage();
    
    public void setSubCommand(final SubCommand cmd) {
        this.subcommand = cmd;
    }
    
    public void setOut(final PrintStream p) {
        this.out = p;
    }
    
    public PrintStream getOut() {
        return this.out;
    }
    
    public void setErr(final PrintStream p) {
        this.err = p;
    }
    
    public PrintStream getErr() {
        return this.err;
    }
    
    @Override
    public int run(final String[] args) throws Exception {
        int exitCode = 0;
        try {
            exitCode = this.init(args);
            if (exitCode != 0 || this.subcommand == null) {
                this.printShellUsage();
                return exitCode;
            }
            if (this.subcommand.validate()) {
                this.subcommand.execute();
            }
            else {
                this.printShellUsage();
                exitCode = 1;
            }
        }
        catch (Exception e) {
            this.printShellUsage();
            this.printException(e);
            return 1;
        }
        return exitCode;
    }
    
    protected abstract int init(final String[] p0) throws Exception;
    
    protected final void printShellUsage() {
        if (this.subcommand != null) {
            this.out.println(this.subcommand.getUsage());
        }
        else {
            this.out.println(this.getCommandUsage());
        }
        this.out.flush();
    }
    
    protected void printException(final Exception ex) {
        ex.printStackTrace(this.err);
    }
    
    protected abstract class SubCommand
    {
        public boolean validate() {
            return true;
        }
        
        public abstract void execute() throws Exception;
        
        public abstract String getUsage();
    }
}
