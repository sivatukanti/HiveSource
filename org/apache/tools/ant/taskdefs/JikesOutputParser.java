// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import org.apache.tools.ant.Task;

public class JikesOutputParser implements ExecuteStreamHandler
{
    protected Task task;
    protected boolean errorFlag;
    protected int errors;
    protected int warnings;
    protected boolean error;
    protected boolean emacsMode;
    protected BufferedReader br;
    
    public void setProcessInputStream(final OutputStream os) {
    }
    
    public void setProcessErrorStream(final InputStream is) {
    }
    
    public void setProcessOutputStream(final InputStream is) throws IOException {
        this.br = new BufferedReader(new InputStreamReader(is));
    }
    
    public void start() throws IOException {
        this.parseOutput(this.br);
    }
    
    public void stop() {
    }
    
    protected JikesOutputParser(final Task task, final boolean emacsMode) {
        this.errorFlag = false;
        this.error = false;
        System.err.println("As of Ant 1.2 released in October 2000, the JikesOutputParser class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
        this.task = task;
        this.emacsMode = emacsMode;
    }
    
    protected void parseOutput(final BufferedReader reader) throws IOException {
        if (this.emacsMode) {
            this.parseEmacsOutput(reader);
        }
        else {
            this.parseStandardOutput(reader);
        }
    }
    
    private void parseStandardOutput(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            final String lower = line.toLowerCase();
            if (line.trim().equals("")) {
                continue;
            }
            if (lower.indexOf("error") != -1) {
                this.setError(true);
            }
            else if (lower.indexOf("warning") != -1) {
                this.setError(false);
            }
            else if (this.emacsMode) {
                this.setError(true);
            }
            this.log(line);
        }
    }
    
    private void parseEmacsOutput(final BufferedReader reader) throws IOException {
        this.parseStandardOutput(reader);
    }
    
    private void setError(final boolean err) {
        this.error = err;
        if (this.error) {
            this.errorFlag = true;
        }
    }
    
    private void log(final String line) {
        if (!this.emacsMode) {
            this.task.log("", this.error ? 0 : 1);
        }
        this.task.log(line, this.error ? 0 : 1);
    }
    
    protected boolean getErrorFlag() {
        return this.errorFlag;
    }
}
