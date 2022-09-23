// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.InputStream;

public class StreamPrinter extends Thread
{
    InputStream is;
    String type;
    PrintStream os;
    
    public StreamPrinter(final InputStream is, final String type, final PrintStream os) {
        this.is = is;
        this.type = type;
        this.os = os;
    }
    
    @Override
    public void run() {
        BufferedReader br = null;
        try {
            final InputStreamReader isr = new InputStreamReader(this.is);
            br = new BufferedReader(isr);
            String line = null;
            if (this.type != null) {
                while ((line = br.readLine()) != null) {
                    this.os.println(this.type + ">" + line);
                }
            }
            else {
                while ((line = br.readLine()) != null) {
                    this.os.println(line);
                }
            }
            br.close();
            br = null;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            IOUtils.closeStream(br);
        }
    }
}
