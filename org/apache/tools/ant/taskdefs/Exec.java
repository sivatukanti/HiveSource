// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import java.io.PrintWriter;
import java.io.File;
import org.apache.tools.ant.Task;

public class Exec extends Task
{
    private String os;
    private String out;
    private File dir;
    private String command;
    protected PrintWriter fos;
    private boolean failOnError;
    
    public Exec() {
        this.fos = null;
        this.failOnError = false;
        System.err.println("As of Ant 1.2 released in October 2000, the Exec class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
    }
    
    @Override
    public void execute() throws BuildException {
        this.run(this.command);
    }
    
    protected int run(String command) throws BuildException {
        int err = -1;
        final String myos = System.getProperty("os.name");
        this.log("Myos = " + myos, 3);
        if (this.os != null && this.os.indexOf(myos) < 0) {
            this.log("Not found in " + this.os, 3);
            return 0;
        }
        if (this.dir == null) {
            this.dir = this.getProject().getBaseDir();
        }
        if (myos.toLowerCase(Locale.ENGLISH).indexOf("windows") >= 0) {
            if (!this.dir.equals(this.getProject().resolveFile("."))) {
                if (myos.toLowerCase(Locale.ENGLISH).indexOf("nt") >= 0) {
                    command = "cmd /c cd " + this.dir + " && " + command;
                }
                else {
                    final String ant = this.getProject().getProperty("ant.home");
                    if (ant == null) {
                        throw new BuildException("Property 'ant.home' not found", this.getLocation());
                    }
                    final String antRun = this.getProject().resolveFile(ant + "/bin/antRun.bat").toString();
                    command = antRun + " " + this.dir + " " + command;
                }
            }
        }
        else {
            final String ant = this.getProject().getProperty("ant.home");
            if (ant == null) {
                throw new BuildException("Property 'ant.home' not found", this.getLocation());
            }
            final String antRun = this.getProject().resolveFile(ant + "/bin/antRun").toString();
            command = antRun + " " + this.dir + " " + command;
        }
        try {
            this.log(command, 3);
            final Process proc = Runtime.getRuntime().exec(command);
            if (this.out != null) {
                this.fos = new PrintWriter(new FileWriter(this.out));
                this.log("Output redirected to " + this.out, 3);
            }
            final StreamPumper inputPumper = new StreamPumper(proc.getInputStream(), 2);
            final StreamPumper errorPumper = new StreamPumper(proc.getErrorStream(), 1);
            inputPumper.start();
            errorPumper.start();
            proc.waitFor();
            inputPumper.join();
            errorPumper.join();
            proc.destroy();
            this.logFlush();
            err = proc.exitValue();
            if (err != 0) {
                if (this.failOnError) {
                    throw new BuildException("Exec returned: " + err, this.getLocation());
                }
                this.log("Result: " + err, 0);
            }
        }
        catch (IOException ioe) {
            throw new BuildException("Error exec: " + command, ioe, this.getLocation());
        }
        catch (InterruptedException ex) {}
        return err;
    }
    
    public void setDir(final String d) {
        this.dir = this.getProject().resolveFile(d);
    }
    
    public void setOs(final String os) {
        this.os = os;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public void setOutput(final String out) {
        this.out = out;
    }
    
    public void setFailonerror(final boolean fail) {
        this.failOnError = fail;
    }
    
    protected void outputLog(final String line, final int messageLevel) {
        if (this.fos == null) {
            this.log(line, messageLevel);
        }
        else {
            this.fos.println(line);
        }
    }
    
    protected void logFlush() {
        if (this.fos != null) {
            this.fos.close();
        }
    }
    
    class StreamPumper extends Thread
    {
        private BufferedReader din;
        private int messageLevel;
        private boolean endOfStream;
        private static final int SLEEP_TIME = 5;
        
        public StreamPumper(final InputStream is, final int messageLevel) {
            this.endOfStream = false;
            this.din = new BufferedReader(new InputStreamReader(is));
            this.messageLevel = messageLevel;
        }
        
        public void pumpStream() throws IOException {
            if (!this.endOfStream) {
                final String line = this.din.readLine();
                if (line != null) {
                    Exec.this.outputLog(line, this.messageLevel);
                }
                else {
                    this.endOfStream = true;
                }
            }
        }
        
        @Override
        public void run() {
            try {
                try {
                    while (!this.endOfStream) {
                        this.pumpStream();
                        Thread.sleep(5L);
                    }
                }
                catch (InterruptedException ex) {}
                this.din.close();
            }
            catch (IOException ex2) {}
        }
    }
}
