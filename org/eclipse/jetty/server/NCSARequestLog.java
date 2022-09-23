// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.OutputStreamWriter;
import java.util.TimeZone;
import java.io.IOException;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.io.Writer;
import java.io.OutputStream;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("NCSA standard format request log")
public class NCSARequestLog extends AbstractNCSARequestLog
{
    private String _filename;
    private boolean _append;
    private int _retainDays;
    private boolean _closeOut;
    private String _filenameDateFormat;
    private transient OutputStream _out;
    private transient OutputStream _fileOut;
    private transient Writer _writer;
    
    public NCSARequestLog() {
        this._filenameDateFormat = null;
        this.setExtended(true);
        this._append = true;
        this._retainDays = 31;
    }
    
    public NCSARequestLog(final String filename) {
        this._filenameDateFormat = null;
        this.setExtended(true);
        this._append = true;
        this._retainDays = 31;
        this.setFilename(filename);
    }
    
    public void setFilename(String filename) {
        if (filename != null) {
            filename = filename.trim();
            if (filename.length() == 0) {
                filename = null;
            }
        }
        this._filename = filename;
    }
    
    @ManagedAttribute("file of log")
    public String getFilename() {
        return this._filename;
    }
    
    public String getDatedFilename() {
        if (this._fileOut instanceof RolloverFileOutputStream) {
            return ((RolloverFileOutputStream)this._fileOut).getDatedFilename();
        }
        return null;
    }
    
    @Override
    protected boolean isEnabled() {
        return this._fileOut != null;
    }
    
    public void setRetainDays(final int retainDays) {
        this._retainDays = retainDays;
    }
    
    @ManagedAttribute("number of days that log files are kept")
    public int getRetainDays() {
        return this._retainDays;
    }
    
    public void setAppend(final boolean append) {
        this._append = append;
    }
    
    @ManagedAttribute("existing log files are appends to the new one")
    public boolean isAppend() {
        return this._append;
    }
    
    public void setFilenameDateFormat(final String logFileDateFormat) {
        this._filenameDateFormat = logFileDateFormat;
    }
    
    public String getFilenameDateFormat() {
        return this._filenameDateFormat;
    }
    
    @Override
    public void write(final String requestEntry) throws IOException {
        synchronized (this) {
            if (this._writer == null) {
                return;
            }
            this._writer.write(requestEntry);
            this._writer.write(StringUtil.__LINE_SEPARATOR);
            this._writer.flush();
        }
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        if (this._filename != null) {
            this._fileOut = new RolloverFileOutputStream(this._filename, this._append, this._retainDays, TimeZone.getTimeZone(this.getLogTimeZone()), this._filenameDateFormat, null);
            this._closeOut = true;
            NCSARequestLog.LOG.info("Opened " + this.getDatedFilename(), new Object[0]);
        }
        else {
            this._fileOut = System.err;
        }
        this._out = this._fileOut;
        synchronized (this) {
            this._writer = new OutputStreamWriter(this._out);
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        synchronized (this) {
            super.doStop();
            try {
                if (this._writer != null) {
                    this._writer.flush();
                }
            }
            catch (IOException e) {
                NCSARequestLog.LOG.ignore(e);
            }
            if (this._out != null && this._closeOut) {
                try {
                    this._out.close();
                }
                catch (IOException e) {
                    NCSARequestLog.LOG.ignore(e);
                }
            }
            this._out = null;
            this._fileOut = null;
            this._closeOut = false;
            this._writer = null;
        }
    }
}
