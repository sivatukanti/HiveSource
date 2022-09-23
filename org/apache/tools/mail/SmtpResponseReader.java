// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.mail;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;

public class SmtpResponseReader
{
    protected BufferedReader reader;
    private StringBuffer result;
    
    public SmtpResponseReader(final InputStream in) {
        this.reader = null;
        this.result = new StringBuffer();
        this.reader = new BufferedReader(new InputStreamReader(in));
    }
    
    public String getResponse() throws IOException {
        this.result.setLength(0);
        String line = this.reader.readLine();
        if (line != null && line.length() >= 3) {
            this.result.append(line.substring(0, 3));
            this.result.append(" ");
        }
        while (line != null) {
            this.append(line);
            if (!this.hasMoreLines(line)) {
                break;
            }
            line = this.reader.readLine();
        }
        return this.result.toString().trim();
    }
    
    public void close() throws IOException {
        this.reader.close();
    }
    
    protected boolean hasMoreLines(final String line) {
        return line.length() > 3 && line.charAt(3) == '-';
    }
    
    private void append(final String line) {
        if (line.length() > 4) {
            this.result.append(line.substring(4));
            this.result.append(" ");
        }
    }
}
