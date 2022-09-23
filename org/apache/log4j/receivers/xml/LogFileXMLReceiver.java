// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.xml;

import org.apache.log4j.component.ULogger;
import java.util.Iterator;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import org.apache.log4j.rule.ExpressionRule;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.io.Reader;
import org.apache.log4j.receivers.spi.Decoder;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.component.plugins.Receiver;

public class LogFileXMLReceiver extends Receiver
{
    private String fileURL;
    private Rule expressionRule;
    private String filterExpression;
    private String decoder;
    private boolean tailing;
    private Decoder decoderInstance;
    private Reader reader;
    private static final String FILE_KEY = "file";
    private String host;
    private String path;
    private boolean useCurrentThread;
    
    public LogFileXMLReceiver() {
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
        this.tailing = false;
    }
    
    public String getFileURL() {
        return this.fileURL;
    }
    
    public void setFileURL(final String fileURL) {
        this.fileURL = fileURL;
    }
    
    public String getDecoder() {
        return this.decoder;
    }
    
    public void setDecoder(final String _decoder) {
        this.decoder = _decoder;
    }
    
    public String getFilterExpression() {
        return this.filterExpression;
    }
    
    public boolean isTailing() {
        return this.tailing;
    }
    
    public void setTailing(final boolean tailing) {
        this.tailing = tailing;
    }
    
    public void setFilterExpression(final String filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    private boolean passesExpression(final LoggingEvent event) {
        return event == null || this.expressionRule == null || this.expressionRule.evaluate(event, null);
    }
    
    public static void main(final String[] args) {
    }
    
    public void shutdown() {
        try {
            if (this.reader != null) {
                this.reader.close();
                this.reader = null;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void activateOptions() {
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    final URL url = new URL(LogFileXMLReceiver.this.fileURL);
                    LogFileXMLReceiver.this.host = url.getHost();
                    if (LogFileXMLReceiver.this.host != null && LogFileXMLReceiver.this.host.equals("")) {
                        LogFileXMLReceiver.this.host = "file";
                    }
                    LogFileXMLReceiver.this.path = url.getPath();
                }
                catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                try {
                    if (LogFileXMLReceiver.this.filterExpression != null) {
                        LogFileXMLReceiver.this.expressionRule = ExpressionRule.getRule(LogFileXMLReceiver.this.filterExpression);
                    }
                }
                catch (Exception e2) {
                    ComponentBase.this.getLogger().warn("Invalid filter expression: " + LogFileXMLReceiver.this.filterExpression, e2);
                }
                try {
                    final Class c = Class.forName(LogFileXMLReceiver.this.decoder);
                    final Object o = c.newInstance();
                    if (o instanceof Decoder) {
                        LogFileXMLReceiver.this.decoderInstance = (Decoder)o;
                    }
                }
                catch (ClassNotFoundException e3) {
                    e3.printStackTrace();
                }
                catch (InstantiationException e4) {
                    e4.printStackTrace();
                }
                catch (IllegalAccessException e5) {
                    e5.printStackTrace();
                }
                try {
                    LogFileXMLReceiver.this.reader = new InputStreamReader(new URL(LogFileXMLReceiver.this.getFileURL()).openStream());
                    LogFileXMLReceiver.this.process(LogFileXMLReceiver.this.reader);
                }
                catch (FileNotFoundException fnfe) {
                    ComponentBase.this.getLogger().info("file not available");
                }
                catch (IOException ioe) {
                    ComponentBase.this.getLogger().warn("unable to load file", ioe);
                }
            }
        };
        if (this.useCurrentThread) {
            runnable.run();
        }
        else {
            final Thread thread = new Thread(runnable, "LogFileXMLReceiver-" + this.getName());
            thread.start();
        }
    }
    
    private void process(final Reader unbufferedReader) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(unbufferedReader);
        final char[] content = new char[10000];
        this.getLogger().debug("processing starting: " + this.fileURL);
        int length = 0;
        do {
            System.out.println("in do loop-about to process");
            while ((length = bufferedReader.read(content)) > -1) {
                this.processEvents(this.decoderInstance.decodeEvents(String.valueOf(content, 0, length)));
            }
            if (this.tailing) {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (this.tailing);
        this.getLogger().debug("processing complete: " + this.fileURL);
        this.shutdown();
    }
    
    private void processEvents(final Collection c) {
        if (c == null) {
            return;
        }
        for (final LoggingEvent evt : c) {
            if (this.passesExpression(evt)) {
                if (evt.getProperty("hostname") != null) {
                    evt.setProperty("hostname", this.host);
                }
                if (evt.getProperty("application") != null) {
                    evt.setProperty("application", this.path);
                }
                this.doPost(evt);
            }
        }
    }
    
    public final boolean isUseCurrentThread() {
        return this.useCurrentThread;
    }
    
    public final void setUseCurrentThread(final boolean useCurrentThread) {
        this.useCurrentThread = useCurrentThread;
    }
}
