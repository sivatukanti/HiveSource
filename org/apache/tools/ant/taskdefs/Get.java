// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.apache.tools.ant.util.Base64Converter;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.URLResource;
import java.util.Date;
import org.apache.tools.ant.util.FileNameMapper;
import java.net.URL;
import java.util.Iterator;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.Mapper;
import java.io.File;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Get extends Task
{
    private static final int NUMBER_RETRIES = 3;
    private static final int DOTS_PER_LINE = 50;
    private static final int BIG_BUFFER_SIZE = 102400;
    private static final FileUtils FILE_UTILS;
    private static final int REDIRECT_LIMIT = 25;
    private static final int HTTP_MOVED_TEMP = 307;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private Resources sources;
    private File destination;
    private boolean verbose;
    private boolean useTimestamp;
    private boolean ignoreErrors;
    private String uname;
    private String pword;
    private long maxTime;
    private int numberRetries;
    private boolean skipExisting;
    private boolean httpUseCaches;
    private Mapper mapperElement;
    
    public Get() {
        this.sources = new Resources();
        this.verbose = false;
        this.useTimestamp = false;
        this.ignoreErrors = false;
        this.uname = null;
        this.pword = null;
        this.maxTime = 0L;
        this.numberRetries = 3;
        this.skipExisting = false;
        this.httpUseCaches = true;
        this.mapperElement = null;
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkAttributes();
        for (final Resource r : this.sources) {
            final URLProvider up = r.as(URLProvider.class);
            final URL source = up.getURL();
            File dest = this.destination;
            if (this.destination.isDirectory()) {
                if (this.mapperElement == null) {
                    String path = source.getPath();
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    final int slash = path.lastIndexOf("/");
                    if (slash > -1) {
                        path = path.substring(slash + 1);
                    }
                    dest = new File(this.destination, path);
                }
                else {
                    final FileNameMapper mapper = this.mapperElement.getImplementation();
                    final String[] d = mapper.mapFileName(source.toString());
                    if (d == null) {
                        this.log("skipping " + r + " - mapper can't handle it", 1);
                        continue;
                    }
                    if (d.length == 0) {
                        this.log("skipping " + r + " - mapper returns no file name", 1);
                        continue;
                    }
                    if (d.length > 1) {
                        this.log("skipping " + r + " - mapper returns multiple file" + " names", 1);
                        continue;
                    }
                    dest = new File(this.destination, d[0]);
                }
            }
            final int logLevel = 2;
            DownloadProgress progress = null;
            if (this.verbose) {
                progress = new VerboseProgress(System.out);
            }
            try {
                this.doGet(source, dest, logLevel, progress);
            }
            catch (IOException ioe) {
                this.log("Error getting " + source + " to " + dest);
                if (!this.ignoreErrors) {
                    throw new BuildException(ioe, this.getLocation());
                }
                continue;
            }
        }
    }
    
    @Deprecated
    public boolean doGet(final int logLevel, final DownloadProgress progress) throws IOException {
        this.checkAttributes();
        final Iterator i$ = this.sources.iterator();
        if (i$.hasNext()) {
            final Resource r = i$.next();
            final URLProvider up = r.as(URLProvider.class);
            final URL source = up.getURL();
            return this.doGet(source, this.destination, logLevel, progress);
        }
        return false;
    }
    
    public boolean doGet(final URL source, final File dest, final int logLevel, DownloadProgress progress) throws IOException {
        if (dest.exists() && this.skipExisting) {
            this.log("Destination already exists (skipping): " + dest.getAbsolutePath(), logLevel);
            return true;
        }
        if (progress == null) {
            progress = new NullProgress();
        }
        this.log("Getting: " + source, logLevel);
        this.log("To: " + dest.getAbsolutePath(), logLevel);
        long timestamp = 0L;
        boolean hasTimestamp = false;
        if (this.useTimestamp && dest.exists()) {
            timestamp = dest.lastModified();
            if (this.verbose) {
                final Date t = new Date(timestamp);
                this.log("local file date : " + t.toString(), logLevel);
            }
            hasTimestamp = true;
        }
        final GetThread getThread = new GetThread(source, dest, hasTimestamp, timestamp, progress, logLevel);
        getThread.setDaemon(true);
        this.getProject().registerThreadTask(getThread, this);
        getThread.start();
        try {
            getThread.join(this.maxTime * 1000L);
        }
        catch (InterruptedException ie) {
            this.log("interrupted waiting for GET to finish", 3);
        }
        if (!getThread.isAlive()) {
            return getThread.wasSuccessful();
        }
        final String msg = "The GET operation took longer than " + this.maxTime + " seconds, stopping it.";
        if (this.ignoreErrors) {
            this.log(msg);
        }
        getThread.closeStreams();
        if (!this.ignoreErrors) {
            throw new BuildException(msg);
        }
        return false;
    }
    
    private void checkAttributes() {
        if (this.sources.size() == 0) {
            throw new BuildException("at least one source is required", this.getLocation());
        }
        for (final Resource r : this.sources) {
            final URLProvider up = r.as(URLProvider.class);
            if (up == null) {
                throw new BuildException("Only URLProvider resources are supported", this.getLocation());
            }
        }
        if (this.destination == null) {
            throw new BuildException("dest attribute is required", this.getLocation());
        }
        if (this.destination.exists() && this.sources.size() > 1 && !this.destination.isDirectory()) {
            throw new BuildException("The specified destination is not a directory", this.getLocation());
        }
        if (this.destination.exists() && !this.destination.canWrite()) {
            throw new BuildException("Can't write to " + this.destination.getAbsolutePath(), this.getLocation());
        }
        if (this.sources.size() > 1 && !this.destination.exists()) {
            this.destination.mkdirs();
        }
    }
    
    public void setSrc(final URL u) {
        this.add(new URLResource(u));
    }
    
    public void add(final ResourceCollection rc) {
        this.sources.add(rc);
    }
    
    public void setDest(final File dest) {
        this.destination = dest;
    }
    
    public void setVerbose(final boolean v) {
        this.verbose = v;
    }
    
    public void setIgnoreErrors(final boolean v) {
        this.ignoreErrors = v;
    }
    
    public void setUseTimestamp(final boolean v) {
        this.useTimestamp = v;
    }
    
    public void setUsername(final String u) {
        this.uname = u;
    }
    
    public void setPassword(final String p) {
        this.pword = p;
    }
    
    public void setMaxTime(final long maxTime) {
        this.maxTime = maxTime;
    }
    
    public void setRetries(final int r) {
        this.numberRetries = r;
    }
    
    public void setSkipExisting(final boolean s) {
        this.skipExisting = s;
    }
    
    public void setHttpUseCaches(final boolean httpUseCache) {
        this.httpUseCaches = httpUseCache;
    }
    
    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        return this.mapperElement = new Mapper(this.getProject());
    }
    
    public void add(final FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    protected static class Base64Converter extends org.apache.tools.ant.util.Base64Converter
    {
    }
    
    public static class NullProgress implements DownloadProgress
    {
        public void beginDownload() {
        }
        
        public void onTick() {
        }
        
        public void endDownload() {
        }
    }
    
    public static class VerboseProgress implements DownloadProgress
    {
        private int dots;
        PrintStream out;
        
        public VerboseProgress(final PrintStream out) {
            this.dots = 0;
            this.out = out;
        }
        
        public void beginDownload() {
            this.dots = 0;
        }
        
        public void onTick() {
            this.out.print(".");
            if (this.dots++ > 50) {
                this.out.flush();
                this.dots = 0;
            }
        }
        
        public void endDownload() {
            this.out.println();
            this.out.flush();
        }
    }
    
    private class GetThread extends Thread
    {
        private final URL source;
        private final File dest;
        private final boolean hasTimestamp;
        private final long timestamp;
        private final DownloadProgress progress;
        private final int logLevel;
        private boolean success;
        private IOException ioexception;
        private BuildException exception;
        private InputStream is;
        private OutputStream os;
        private URLConnection connection;
        private int redirections;
        
        GetThread(final URL source, final File dest, final boolean h, final long t, final DownloadProgress p, final int l) {
            this.success = false;
            this.ioexception = null;
            this.exception = null;
            this.is = null;
            this.os = null;
            this.redirections = 0;
            this.source = source;
            this.dest = dest;
            this.hasTimestamp = h;
            this.timestamp = t;
            this.progress = p;
            this.logLevel = l;
        }
        
        @Override
        public void run() {
            try {
                this.success = this.get();
            }
            catch (IOException ioex) {
                this.ioexception = ioex;
            }
            catch (BuildException bex) {
                this.exception = bex;
            }
        }
        
        private boolean get() throws IOException, BuildException {
            this.connection = this.openConnection(this.source);
            if (this.connection == null) {
                return false;
            }
            final boolean downloadSucceeded = this.downloadFile();
            if (downloadSucceeded && Get.this.useTimestamp) {
                this.updateTimeStamp();
            }
            return downloadSucceeded;
        }
        
        private boolean redirectionAllowed(final URL aSource, final URL aDest) {
            if (!aSource.getProtocol().equals(aDest.getProtocol()) && (!"http".equals(aSource.getProtocol()) || !"https".equals(aDest.getProtocol()))) {
                final String message = "Redirection detected from " + aSource.getProtocol() + " to " + aDest.getProtocol() + ". Protocol switch unsafe, not allowed.";
                if (Get.this.ignoreErrors) {
                    Get.this.log(message, this.logLevel);
                    return false;
                }
                throw new BuildException(message);
            }
            else {
                ++this.redirections;
                if (this.redirections <= 25) {
                    return true;
                }
                final String message = "More than 25 times redirected, giving up";
                if (Get.this.ignoreErrors) {
                    Get.this.log(message, this.logLevel);
                    return false;
                }
                throw new BuildException(message);
            }
        }
        
        private URLConnection openConnection(final URL aSource) throws IOException {
            final URLConnection connection = aSource.openConnection();
            if (this.hasTimestamp) {
                connection.setIfModifiedSince(this.timestamp);
            }
            if (Get.this.uname != null || Get.this.pword != null) {
                final String up = Get.this.uname + ":" + Get.this.pword;
                final Base64Converter encoder = new Base64Converter();
                final String encoding = encoder.encode(up.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + encoding);
            }
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).setInstanceFollowRedirects(false);
                ((HttpURLConnection)connection).setUseCaches(Get.this.httpUseCaches);
            }
            try {
                connection.connect();
            }
            catch (NullPointerException e) {
                throw new BuildException("Failed to parse " + this.source.toString(), e);
            }
            if (connection instanceof HttpURLConnection) {
                final HttpURLConnection httpConnection = (HttpURLConnection)connection;
                final int responseCode = httpConnection.getResponseCode();
                if (responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307) {
                    final String newLocation = httpConnection.getHeaderField("Location");
                    final String message = aSource + ((responseCode == 301) ? " permanently" : "") + " moved to " + newLocation;
                    Get.this.log(message, this.logLevel);
                    final URL newURL = new URL(aSource, newLocation);
                    if (!this.redirectionAllowed(aSource, newURL)) {
                        return null;
                    }
                    return this.openConnection(newURL);
                }
                else {
                    final long lastModified = httpConnection.getLastModified();
                    if (responseCode == 304 || (lastModified != 0L && this.hasTimestamp && this.timestamp >= lastModified)) {
                        Get.this.log("Not modified - so not downloaded", this.logLevel);
                        return null;
                    }
                    if (responseCode == 401) {
                        final String message2 = "HTTP Authorization failure";
                        if (Get.this.ignoreErrors) {
                            Get.this.log(message2, this.logLevel);
                            return null;
                        }
                        throw new BuildException(message2);
                    }
                }
            }
            return connection;
        }
        
        private boolean downloadFile() throws FileNotFoundException, IOException {
            int i = 0;
            while (i < Get.this.numberRetries) {
                try {
                    this.is = this.connection.getInputStream();
                }
                catch (IOException ex) {
                    Get.this.log("Error opening connection " + ex, this.logLevel);
                    ++i;
                    continue;
                }
                break;
            }
            if (this.is != null) {
                this.os = new FileOutputStream(this.dest);
                this.progress.beginDownload();
                boolean finished = false;
                try {
                    final byte[] buffer = new byte[102400];
                    int length;
                    while (!this.isInterrupted() && (length = this.is.read(buffer)) >= 0) {
                        this.os.write(buffer, 0, length);
                        this.progress.onTick();
                    }
                    finished = !this.isInterrupted();
                }
                finally {
                    FileUtils.close(this.os);
                    FileUtils.close(this.is);
                    if (!finished) {
                        this.dest.delete();
                    }
                }
                this.progress.endDownload();
                return true;
            }
            Get.this.log("Can't get " + this.source + " to " + this.dest, this.logLevel);
            if (Get.this.ignoreErrors) {
                return false;
            }
            throw new BuildException("Can't get " + this.source + " to " + this.dest, Get.this.getLocation());
        }
        
        private void updateTimeStamp() {
            final long remoteTimestamp = this.connection.getLastModified();
            if (Get.this.verbose) {
                final Date t = new Date(remoteTimestamp);
                Get.this.log("last modified = " + t.toString() + ((remoteTimestamp == 0L) ? " - using current time instead" : ""), this.logLevel);
            }
            if (remoteTimestamp != 0L) {
                Get.FILE_UTILS.setFileLastModified(this.dest, remoteTimestamp);
            }
        }
        
        boolean wasSuccessful() throws IOException, BuildException {
            if (this.ioexception != null) {
                throw this.ioexception;
            }
            if (this.exception != null) {
                throw this.exception;
            }
            return this.success;
        }
        
        void closeStreams() {
            this.interrupt();
            FileUtils.close(this.os);
            FileUtils.close(this.is);
            if (!this.success && this.dest.exists()) {
                this.dest.delete();
            }
        }
    }
    
    public interface DownloadProgress
    {
        void beginDownload();
        
        void onTick();
        
        void endDownload();
    }
}
