// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.File;
import org.apache.tools.ant.ProjectComponent;

public class Message extends ProjectComponent
{
    private File messageSource;
    private StringBuffer buffer;
    private String mimeType;
    private boolean specified;
    private String charset;
    
    public Message() {
        this.messageSource = null;
        this.buffer = new StringBuffer();
        this.mimeType = "text/plain";
        this.specified = false;
        this.charset = null;
    }
    
    public Message(final String text) {
        this.messageSource = null;
        this.buffer = new StringBuffer();
        this.mimeType = "text/plain";
        this.specified = false;
        this.charset = null;
        this.addText(text);
    }
    
    public Message(final File file) {
        this.messageSource = null;
        this.buffer = new StringBuffer();
        this.mimeType = "text/plain";
        this.specified = false;
        this.charset = null;
        this.messageSource = file;
    }
    
    public void addText(final String text) {
        this.buffer.append(text);
    }
    
    public void setSrc(final File src) {
        this.messageSource = src;
    }
    
    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
        this.specified = true;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public void print(final PrintStream ps) throws IOException {
        BufferedWriter out = null;
        try {
            BufferedWriter bufferedWriter;
            if (this.charset != null) {
                final OutputStreamWriter out2;
                bufferedWriter = new BufferedWriter(out2);
                out2 = new OutputStreamWriter(ps, this.charset);
            }
            else {
                final OutputStreamWriter out3;
                bufferedWriter = new BufferedWriter(out3);
                out3 = new OutputStreamWriter(ps);
            }
            out = bufferedWriter;
            if (this.messageSource != null) {
                final FileReader freader = new FileReader(this.messageSource);
                try {
                    final BufferedReader in = new BufferedReader(freader);
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        out.write(this.getProject().replaceProperties(line));
                        out.newLine();
                    }
                }
                finally {
                    freader.close();
                }
            }
            else {
                out.write(this.getProject().replaceProperties(this.buffer.substring(0)));
                out.newLine();
            }
            out.flush();
        }
        finally {}
    }
    
    public boolean isMimeTypeSpecified() {
        return this.specified;
    }
    
    public void setCharset(final String charset) {
        this.charset = charset;
    }
    
    public String getCharset() {
        return this.charset;
    }
}
