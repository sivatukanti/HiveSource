// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.OutputStreamWriter;
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import java.io.IOException;
import org.mortbay.log.Log;
import org.mortbay.util.TypeUtil;
import org.mortbay.util.StringUtil;
import org.mortbay.util.Utf8StringBuffer;
import org.mortbay.util.RolloverFileOutputStream;
import java.util.ArrayList;
import java.io.Writer;
import org.mortbay.jetty.servlet.PathMap;
import org.mortbay.util.DateCache;
import java.io.OutputStream;
import java.util.Locale;
import org.mortbay.component.AbstractLifeCycle;

public class NCSARequestLog extends AbstractLifeCycle implements RequestLog
{
    private String _filename;
    private boolean _extended;
    private boolean _append;
    private int _retainDays;
    private boolean _closeOut;
    private boolean _preferProxiedForAddress;
    private String _logDateFormat;
    private String _filenameDateFormat;
    private Locale _logLocale;
    private String _logTimeZone;
    private String[] _ignorePaths;
    private boolean _logLatency;
    private boolean _logCookies;
    private boolean _logServer;
    private transient OutputStream _out;
    private transient OutputStream _fileOut;
    private transient DateCache _logDateCache;
    private transient PathMap _ignorePathMap;
    private transient Writer _writer;
    private transient ArrayList _buffers;
    private transient char[] _copy;
    
    public NCSARequestLog() {
        this._logDateFormat = "dd/MMM/yyyy:HH:mm:ss Z";
        this._filenameDateFormat = null;
        this._logLocale = Locale.getDefault();
        this._logTimeZone = "GMT";
        this._logLatency = false;
        this._logCookies = false;
        this._logServer = false;
        this._extended = true;
        this._append = true;
        this._retainDays = 31;
    }
    
    public NCSARequestLog(final String filename) {
        this._logDateFormat = "dd/MMM/yyyy:HH:mm:ss Z";
        this._filenameDateFormat = null;
        this._logLocale = Locale.getDefault();
        this._logTimeZone = "GMT";
        this._logLatency = false;
        this._logCookies = false;
        this._logServer = false;
        this._extended = true;
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
    
    public String getFilename() {
        return this._filename;
    }
    
    public String getDatedFilename() {
        if (this._fileOut instanceof RolloverFileOutputStream) {
            return ((RolloverFileOutputStream)this._fileOut).getDatedFilename();
        }
        return null;
    }
    
    public void setLogDateFormat(final String format) {
        this._logDateFormat = format;
    }
    
    public String getLogDateFormat() {
        return this._logDateFormat;
    }
    
    public void setLogLocale(final Locale logLocale) {
        this._logLocale = logLocale;
    }
    
    public Locale getLogLocale() {
        return this._logLocale;
    }
    
    public void setLogTimeZone(final String tz) {
        this._logTimeZone = tz;
    }
    
    public String getLogTimeZone() {
        return this._logTimeZone;
    }
    
    public void setRetainDays(final int retainDays) {
        this._retainDays = retainDays;
    }
    
    public int getRetainDays() {
        return this._retainDays;
    }
    
    public void setExtended(final boolean extended) {
        this._extended = extended;
    }
    
    public boolean isExtended() {
        return this._extended;
    }
    
    public void setAppend(final boolean append) {
        this._append = append;
    }
    
    public boolean isAppend() {
        return this._append;
    }
    
    public void setIgnorePaths(final String[] ignorePaths) {
        this._ignorePaths = ignorePaths;
    }
    
    public String[] getIgnorePaths() {
        return this._ignorePaths;
    }
    
    public void setLogCookies(final boolean logCookies) {
        this._logCookies = logCookies;
    }
    
    public boolean getLogCookies() {
        return this._logCookies;
    }
    
    public boolean getLogServer() {
        return this._logServer;
    }
    
    public void setLogServer(final boolean logServer) {
        this._logServer = logServer;
    }
    
    public void setLogLatency(final boolean logLatency) {
        this._logLatency = logLatency;
    }
    
    public boolean getLogLatency() {
        return this._logLatency;
    }
    
    public void setPreferProxiedForAddress(final boolean preferProxiedForAddress) {
        this._preferProxiedForAddress = preferProxiedForAddress;
    }
    
    public void log(final Request request, final Response response) {
        if (!this.isStarted()) {
            return;
        }
        try {
            if (this._ignorePathMap != null && this._ignorePathMap.getMatch(request.getRequestURI()) != null) {
                return;
            }
            if (this._fileOut == null) {
                return;
            }
            final Utf8StringBuffer u8buf;
            final StringBuffer buf;
            synchronized (this._writer) {
                final int size = this._buffers.size();
                u8buf = ((size == 0) ? new Utf8StringBuffer(160) : this._buffers.remove(size - 1));
                buf = u8buf.getStringBuffer();
            }
            synchronized (buf) {
                if (this._logServer) {
                    buf.append(request.getServerName());
                    buf.append(' ');
                }
                String addr = null;
                if (this._preferProxiedForAddress) {
                    addr = request.getHeader("X-Forwarded-For");
                }
                if (addr == null) {
                    addr = request.getRemoteAddr();
                }
                buf.append(addr);
                buf.append(" - ");
                final String user = request.getRemoteUser();
                buf.append((user == null) ? " - " : user);
                buf.append(" [");
                if (this._logDateCache != null) {
                    buf.append(this._logDateCache.format(request.getTimeStamp()));
                }
                else {
                    buf.append(request.getTimeStampBuffer().toString());
                }
                buf.append("] \"");
                buf.append(request.getMethod());
                buf.append(' ');
                request.getUri().writeTo(u8buf);
                buf.append(' ');
                buf.append(request.getProtocol());
                buf.append("\" ");
                int status = response.getStatus();
                if (status <= 0) {
                    status = 404;
                }
                buf.append((char)(48 + status / 100 % 10));
                buf.append((char)(48 + status / 10 % 10));
                buf.append((char)(48 + status % 10));
                final long responseLength = response.getContentCount();
                if (responseLength >= 0L) {
                    buf.append(' ');
                    if (responseLength > 99999L) {
                        buf.append(Long.toString(responseLength));
                    }
                    else {
                        if (responseLength > 9999L) {
                            buf.append((char)(48L + responseLength / 10000L % 10L));
                        }
                        if (responseLength > 999L) {
                            buf.append((char)(48L + responseLength / 1000L % 10L));
                        }
                        if (responseLength > 99L) {
                            buf.append((char)(48L + responseLength / 100L % 10L));
                        }
                        if (responseLength > 9L) {
                            buf.append((char)(48L + responseLength / 10L % 10L));
                        }
                        buf.append((char)(48L + responseLength % 10L));
                    }
                    buf.append(' ');
                }
                else {
                    buf.append(" - ");
                }
            }
            if (!this._extended && !this._logCookies && !this._logLatency) {
                synchronized (this._writer) {
                    buf.append(StringUtil.__LINE_SEPARATOR);
                    int l = buf.length();
                    if (l > this._copy.length) {
                        l = this._copy.length;
                    }
                    buf.getChars(0, l, this._copy, 0);
                    this._writer.write(this._copy, 0, l);
                    this._writer.flush();
                    u8buf.reset();
                    this._buffers.add(u8buf);
                }
            }
            else {
                synchronized (this._writer) {
                    int l = buf.length();
                    if (l > this._copy.length) {
                        l = this._copy.length;
                    }
                    buf.getChars(0, l, this._copy, 0);
                    this._writer.write(this._copy, 0, l);
                    u8buf.reset();
                    this._buffers.add(u8buf);
                    if (this._extended) {
                        this.logExtended(request, response, this._writer);
                    }
                    if (this._logCookies) {
                        final Cookie[] cookies = request.getCookies();
                        if (cookies == null || cookies.length == 0) {
                            this._writer.write(" -");
                        }
                        else {
                            this._writer.write(" \"");
                            for (int i = 0; i < cookies.length; ++i) {
                                if (i != 0) {
                                    this._writer.write(59);
                                }
                                this._writer.write(cookies[i].getName());
                                this._writer.write(61);
                                this._writer.write(cookies[i].getValue());
                            }
                            this._writer.write(34);
                        }
                    }
                    if (this._logLatency) {
                        this._writer.write(32);
                        this._writer.write(TypeUtil.toString(System.currentTimeMillis() - request.getTimeStamp()));
                    }
                    this._writer.write(StringUtil.__LINE_SEPARATOR);
                    this._writer.flush();
                }
            }
        }
        catch (IOException e) {
            Log.warn(e);
        }
    }
    
    protected void logExtended(final Request request, final Response response, final Writer writer) throws IOException {
        final String referer = request.getHeader("Referer");
        if (referer == null) {
            writer.write("\"-\" ");
        }
        else {
            writer.write(34);
            writer.write(referer);
            writer.write("\" ");
        }
        final String agent = request.getHeader("User-Agent");
        if (agent == null) {
            writer.write("\"-\" ");
        }
        else {
            writer.write(34);
            writer.write(agent);
            writer.write(34);
        }
    }
    
    protected void doStart() throws Exception {
        if (this._logDateFormat != null) {
            (this._logDateCache = new DateCache(this._logDateFormat, this._logLocale)).setTimeZoneID(this._logTimeZone);
        }
        if (this._filename != null) {
            this._fileOut = new RolloverFileOutputStream(this._filename, this._append, this._retainDays, TimeZone.getTimeZone(this._logTimeZone), this._filenameDateFormat, null);
            this._closeOut = true;
            Log.info("Opened " + this.getDatedFilename());
        }
        else {
            this._fileOut = System.err;
        }
        this._out = this._fileOut;
        if (this._ignorePaths != null && this._ignorePaths.length > 0) {
            this._ignorePathMap = new PathMap();
            for (int i = 0; i < this._ignorePaths.length; ++i) {
                this._ignorePathMap.put(this._ignorePaths[i], this._ignorePaths[i]);
            }
        }
        else {
            this._ignorePathMap = null;
        }
        this._writer = new OutputStreamWriter(this._out);
        this._buffers = new ArrayList();
        this._copy = new char[1024];
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        try {
            if (this._writer != null) {
                this._writer.flush();
            }
        }
        catch (IOException e) {
            Log.ignore(e);
        }
        if (this._out != null && this._closeOut) {
            try {
                this._out.close();
            }
            catch (IOException e) {
                Log.ignore(e);
            }
        }
        this._out = null;
        this._fileOut = null;
        this._closeOut = false;
        this._logDateCache = null;
        this._writer = null;
        this._buffers = null;
        this._copy = null;
    }
    
    public String getFilenameDateFormat() {
        return this._filenameDateFormat;
    }
    
    public void setFilenameDateFormat(final String logFileDateFormat) {
        this._filenameDateFormat = logFileDateFormat;
    }
}
