// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import org.slf4j.Logger;

class StreamPumper
{
    private final Logger log;
    final Thread thread;
    final String logPrefix;
    final StreamType type;
    private final InputStream stream;
    private boolean started;
    
    StreamPumper(final Logger log, final String logPrefix, final InputStream stream, final StreamType type) {
        this.started = false;
        this.log = log;
        this.logPrefix = logPrefix;
        this.stream = stream;
        this.type = type;
        (this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StreamPumper.this.pump();
                }
                catch (Throwable t) {
                    ShellCommandFencer.LOG.warn(logPrefix + ": Unable to pump output from " + type, t);
                }
            }
        }, logPrefix + ": StreamPumper for " + type)).setDaemon(true);
    }
    
    void join() throws InterruptedException {
        assert this.started;
        this.thread.join();
    }
    
    void start() {
        assert !this.started;
        this.thread.start();
        this.started = true;
    }
    
    protected void pump() throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(this.stream, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(inputStreamReader);
        String line = null;
        while ((line = br.readLine()) != null) {
            if (this.type == StreamType.STDOUT) {
                this.log.info(this.logPrefix + ": " + line);
            }
            else {
                this.log.warn(this.logPrefix + ": " + line);
            }
        }
    }
    
    enum StreamType
    {
        STDOUT, 
        STDERR;
    }
}
