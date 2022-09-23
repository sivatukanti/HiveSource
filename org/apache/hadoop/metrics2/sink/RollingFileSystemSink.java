// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink;

import java.util.TimeZone;
import java.util.Iterator;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.LocatedFileStatus;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.TimerTask;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.Date;
import java.io.IOException;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Calendar;
import java.util.Timer;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.metrics2.MetricsSink;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class RollingFileSystemSink implements MetricsSink, Closeable
{
    private static final String BASEPATH_KEY = "basepath";
    private static final String SOURCE_KEY = "source";
    private static final String IGNORE_ERROR_KEY = "ignore-error";
    private static final boolean DEFAULT_IGNORE_ERROR = false;
    private static final String ALLOW_APPEND_KEY = "allow-append";
    private static final boolean DEFAULT_ALLOW_APPEND = false;
    private static final String KEYTAB_PROPERTY_KEY = "keytab-key";
    private static final String USERNAME_PROPERTY_KEY = "principal-key";
    private static final String ROLL_INTERVAL_KEY = "roll-interval";
    private static final String DEFAULT_ROLL_INTERVAL = "1h";
    private static final String ROLL_OFFSET_INTERVAL_MILLIS_KEY = "roll-offset-interval-millis";
    private static final int DEFAULT_ROLL_OFFSET_INTERVAL_MILLIS = 30000;
    private static final String SOURCE_DEFAULT = "unknown";
    private static final String BASEPATH_DEFAULT = "/tmp";
    private static final FastDateFormat DATE_FORMAT;
    private final Object lock;
    private boolean initialized;
    private SubsetConfiguration properties;
    private Configuration conf;
    @VisibleForTesting
    protected String source;
    @VisibleForTesting
    protected boolean ignoreError;
    @VisibleForTesting
    protected boolean allowAppend;
    @VisibleForTesting
    protected Path basePath;
    private FileSystem fileSystem;
    private Path currentDirPath;
    private Path currentFilePath;
    private PrintStream currentOutStream;
    private FSDataOutputStream currentFSOutStream;
    private Timer flushTimer;
    @VisibleForTesting
    protected long rollIntervalMillis;
    @VisibleForTesting
    protected long rollOffsetIntervalMillis;
    @VisibleForTesting
    protected Calendar nextFlush;
    @VisibleForTesting
    protected static boolean forceFlush;
    @VisibleForTesting
    protected static volatile boolean hasFlushed;
    @VisibleForTesting
    protected static Configuration suppliedConf;
    @VisibleForTesting
    protected static FileSystem suppliedFilesystem;
    
    public RollingFileSystemSink() {
        this.lock = new Object();
        this.initialized = false;
        this.nextFlush = null;
    }
    
    @VisibleForTesting
    protected RollingFileSystemSink(final long flushIntervalMillis, final long flushOffsetIntervalMillis) {
        this.lock = new Object();
        this.initialized = false;
        this.nextFlush = null;
        this.rollIntervalMillis = flushIntervalMillis;
        this.rollOffsetIntervalMillis = flushOffsetIntervalMillis;
    }
    
    @Override
    public void init(final SubsetConfiguration metrics2Properties) {
        this.properties = metrics2Properties;
        this.basePath = new Path(this.properties.getString("basepath", "/tmp"));
        this.source = this.properties.getString("source", "unknown");
        this.ignoreError = this.properties.getBoolean("ignore-error", false);
        this.allowAppend = this.properties.getBoolean("allow-append", false);
        this.rollOffsetIntervalMillis = this.getNonNegative("roll-offset-interval-millis", 30000);
        this.rollIntervalMillis = this.getRollInterval();
        UserGroupInformation.setConfiguration(this.conf = this.loadConf());
        if (UserGroupInformation.isSecurityEnabled()) {
            this.checkIfPropertyExists("keytab-key");
            this.checkIfPropertyExists("principal-key");
            try {
                SecurityUtil.login(this.conf, this.properties.getString("keytab-key"), this.properties.getString("principal-key"));
            }
            catch (IOException ex) {
                throw new MetricsException("Error logging in securely: [" + ex.toString() + "]", ex);
            }
        }
    }
    
    private boolean initFs() {
        boolean success = false;
        this.fileSystem = this.getFileSystem();
        try {
            this.fileSystem.mkdirs(this.basePath);
            success = true;
        }
        catch (Exception ex) {
            if (!this.ignoreError) {
                throw new MetricsException("Failed to create " + this.basePath + "[" + "source" + "=" + this.source + ", " + "allow-append" + "=" + this.allowAppend + ", " + this.stringifySecurityProperty("keytab-key") + ", " + this.stringifySecurityProperty("principal-key") + "] -- " + ex.toString(), ex);
            }
        }
        if (success) {
            if (this.allowAppend) {
                this.allowAppend = this.checkAppend(this.fileSystem);
            }
            this.flushTimer = new Timer("RollingFileSystemSink Flusher", true);
            this.setInitialFlushTime(new Date());
        }
        return success;
    }
    
    private String stringifySecurityProperty(final String property) {
        String securityProperty;
        if (this.properties.containsKey(property)) {
            final String propertyValue = this.properties.getString(property);
            final String confValue = this.conf.get(this.properties.getString(property));
            if (confValue != null) {
                securityProperty = property + "=" + propertyValue + ", " + this.properties.getString(property) + "=" + confValue;
            }
            else {
                securityProperty = property + "=" + propertyValue + ", " + this.properties.getString(property) + "=<NOT SET>";
            }
        }
        else {
            securityProperty = property + "=<NOT SET>";
        }
        return securityProperty;
    }
    
    @VisibleForTesting
    protected long getRollInterval() {
        final String rollInterval = this.properties.getString("roll-interval", "1h");
        final Pattern pattern = Pattern.compile("^\\s*(\\d+)\\s*([A-Za-z]*)\\s*$");
        final Matcher match = pattern.matcher(rollInterval);
        if (!match.matches()) {
            throw new MetricsException("Unrecognized flush interval: " + rollInterval + ". Must be a number followed by an optional unit. The unit must be one of: minute, hour, day");
        }
        final String flushUnit = match.group(2);
        int rollIntervalInt;
        try {
            rollIntervalInt = Integer.parseInt(match.group(1));
        }
        catch (NumberFormatException ex) {
            throw new MetricsException("Unrecognized flush interval: " + rollInterval + ". Must be a number followed by an optional unit. The unit must be one of: minute, hour, day", ex);
        }
        long millis = 0L;
        if ("".equals(flushUnit)) {
            millis = TimeUnit.HOURS.toMillis(rollIntervalInt);
        }
        else {
            final String lowerCase = flushUnit.toLowerCase();
            switch (lowerCase) {
                case "m":
                case "min":
                case "minute":
                case "minutes": {
                    millis = TimeUnit.MINUTES.toMillis(rollIntervalInt);
                    break;
                }
                case "h":
                case "hr":
                case "hour":
                case "hours": {
                    millis = TimeUnit.HOURS.toMillis(rollIntervalInt);
                    break;
                }
                case "d":
                case "day":
                case "days": {
                    millis = TimeUnit.DAYS.toMillis(rollIntervalInt);
                    break;
                }
                default: {
                    throw new MetricsException("Unrecognized unit for flush interval: " + flushUnit + ". Must be one of: minute, hour, day");
                }
            }
        }
        if (millis < 60000L) {
            throw new MetricsException("The flush interval property must be at least 1 minute. Value was " + rollInterval);
        }
        return millis;
    }
    
    private long getNonNegative(final String key, final int defaultValue) {
        final int flushOffsetIntervalMillis = this.properties.getInt(key, defaultValue);
        if (flushOffsetIntervalMillis < 0) {
            throw new MetricsException("The " + key + " property must be non-negative. Value was " + flushOffsetIntervalMillis);
        }
        return flushOffsetIntervalMillis;
    }
    
    private void checkIfPropertyExists(final String key) {
        if (!this.properties.containsKey(key)) {
            throw new MetricsException("Metrics2 configuration is missing " + key + " property");
        }
    }
    
    private Configuration loadConf() {
        Configuration c;
        if (RollingFileSystemSink.suppliedConf != null) {
            c = RollingFileSystemSink.suppliedConf;
        }
        else {
            c = new Configuration();
        }
        return c;
    }
    
    private FileSystem getFileSystem() throws MetricsException {
        FileSystem fs = null;
        if (RollingFileSystemSink.suppliedFilesystem != null) {
            fs = RollingFileSystemSink.suppliedFilesystem;
        }
        else {
            try {
                fs = FileSystem.get(new URI(this.basePath.toString()), this.conf);
            }
            catch (URISyntaxException ex) {
                throw new MetricsException("The supplied filesystem base path URI is not a valid URI: " + this.basePath.toString(), ex);
            }
            catch (IOException ex2) {
                throw new MetricsException("Error connecting to file system: " + this.basePath + " [" + ex2.toString() + "]", ex2);
            }
        }
        return fs;
    }
    
    private boolean checkAppend(final FileSystem fs) {
        boolean canAppend = true;
        try {
            fs.append(this.basePath);
        }
        catch (UnsupportedOperationException ex) {
            canAppend = false;
        }
        catch (IOException ex2) {}
        return canAppend;
    }
    
    private void rollLogDirIfNeeded() throws MetricsException {
        final Date now = new Date();
        if (this.currentOutStream == null || now.after(this.nextFlush.getTime())) {
            if (!this.initialized) {
                this.initialized = this.initFs();
            }
            if (this.initialized) {
                if (this.currentOutStream != null) {
                    this.currentOutStream.close();
                }
                this.currentDirPath = this.findCurrentDirectory(now);
                try {
                    this.rollLogDir();
                }
                catch (IOException ex) {
                    this.throwMetricsException("Failed to create new log file", ex);
                }
                this.updateFlushTime(now);
                this.scheduleFlush(this.nextFlush.getTime());
            }
        }
        else if (RollingFileSystemSink.forceFlush) {
            this.scheduleFlush(new Date());
        }
    }
    
    private Path findCurrentDirectory(final Date now) {
        final long offset = (now.getTime() - this.nextFlush.getTimeInMillis()) / this.rollIntervalMillis * this.rollIntervalMillis;
        final String currentDir = RollingFileSystemSink.DATE_FORMAT.format(new Date(this.nextFlush.getTimeInMillis() + offset));
        return new Path(this.basePath, currentDir);
    }
    
    private void scheduleFlush(final Date when) {
        final PrintStream toClose = this.currentOutStream;
        this.flushTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (RollingFileSystemSink.this.lock) {
                    toClose.close();
                }
                RollingFileSystemSink.hasFlushed = true;
            }
        }, when);
    }
    
    @VisibleForTesting
    protected void updateFlushTime(final Date now) {
        final int millis = (int)(((now.getTime() - this.nextFlush.getTimeInMillis()) / this.rollIntervalMillis + 1L) * this.rollIntervalMillis);
        this.nextFlush.add(14, millis);
    }
    
    @VisibleForTesting
    protected void setInitialFlushTime(final Date now) {
        (this.nextFlush = Calendar.getInstance()).setTime(now);
        this.nextFlush.set(14, 0);
        this.nextFlush.set(13, 0);
        this.nextFlush.set(12, 0);
        int millis = (int)((now.getTime() - this.nextFlush.getTimeInMillis()) / this.rollIntervalMillis * this.rollIntervalMillis);
        if (this.rollOffsetIntervalMillis > 0L) {
            for (millis += (int)ThreadLocalRandom.current().nextLong(this.rollOffsetIntervalMillis); this.nextFlush.getTimeInMillis() + millis > now.getTime(); millis -= (int)this.rollIntervalMillis) {}
        }
        this.nextFlush.add(14, millis);
    }
    
    private void rollLogDir() throws IOException {
        final String fileName = this.source + "-" + InetAddress.getLocalHost().getHostName() + ".log";
        final Path targetFile = new Path(this.currentDirPath, fileName);
        this.fileSystem.mkdirs(this.currentDirPath);
        if (this.allowAppend) {
            this.createOrAppendLogFile(targetFile);
        }
        else {
            this.createLogFile(targetFile);
        }
    }
    
    private void createLogFile(final Path initial) throws IOException {
        Path currentAttempt = initial;
        int id = 0;
        while (true) {
            try {
                this.currentFSOutStream = this.fileSystem.create(currentAttempt, false);
                this.currentOutStream = new PrintStream(this.currentFSOutStream, true, StandardCharsets.UTF_8.name());
                this.currentFilePath = currentAttempt;
            }
            catch (IOException ex) {
                if (this.fileSystem.exists(currentAttempt)) {
                    id = this.getNextIdToTry(initial, id);
                    currentAttempt = new Path(initial.toString() + "." + id);
                    continue;
                }
                throw ex;
            }
            break;
        }
    }
    
    private int getNextIdToTry(final Path initial, final int lastId) throws IOException {
        final RemoteIterator<LocatedFileStatus> files = this.fileSystem.listFiles(this.currentDirPath, true);
        final String base = initial.toString();
        int id = lastId;
        while (files.hasNext()) {
            final String file = files.next().getPath().getName();
            if (file.startsWith(base)) {
                final int fileId = this.extractId(file);
                if (fileId <= id) {
                    continue;
                }
                id = fileId;
            }
        }
        return id + 1;
    }
    
    private int extractId(final String file) {
        final int index = file.lastIndexOf(".");
        int id = -1;
        if (index > 0) {
            try {
                id = Integer.parseInt(file.substring(index + 1));
            }
            catch (NumberFormatException ex) {}
        }
        return id;
    }
    
    private void createOrAppendLogFile(final Path targetFile) throws IOException {
        try {
            this.currentFSOutStream = this.fileSystem.create(targetFile, false);
            this.currentOutStream = new PrintStream(this.currentFSOutStream, true, StandardCharsets.UTF_8.name());
        }
        catch (IOException ex3) {
            try {
                this.currentFSOutStream = this.fileSystem.append(targetFile);
                this.currentOutStream = new PrintStream(this.currentFSOutStream, true, StandardCharsets.UTF_8.name());
            }
            catch (IOException ex2) {
                ex2.initCause(ex3);
                throw ex2;
            }
        }
        this.currentFilePath = targetFile;
    }
    
    @Override
    public void putMetrics(final MetricsRecord record) {
        synchronized (this.lock) {
            this.rollLogDirIfNeeded();
            if (this.currentOutStream != null) {
                this.currentOutStream.printf("%d %s.%s", record.timestamp(), record.context(), record.name());
                String separator = ": ";
                for (final MetricsTag tag : record.tags()) {
                    this.currentOutStream.printf("%s%s=%s", separator, tag.name(), tag.value());
                    separator = ", ";
                }
                for (final AbstractMetric metric : record.metrics()) {
                    this.currentOutStream.printf("%s%s=%s", separator, metric.name(), metric.value());
                }
                this.currentOutStream.println();
                try {
                    this.currentFSOutStream.hflush();
                }
                catch (IOException ex) {
                    this.throwMetricsException("Failed flushing the stream", ex);
                }
                this.checkForErrors("Unable to write to log file");
            }
            else if (!this.ignoreError) {
                this.throwMetricsException("Unable to write to log file");
            }
        }
    }
    
    @Override
    public void flush() {
        synchronized (this.lock) {
            if (this.currentFSOutStream != null) {
                try {
                    this.currentFSOutStream.hflush();
                }
                catch (IOException ex) {
                    this.throwMetricsException("Unable to flush log file", ex);
                }
            }
        }
    }
    
    @Override
    public void close() {
        synchronized (this.lock) {
            if (this.currentOutStream != null) {
                this.currentOutStream.close();
                try {
                    this.checkForErrors("Unable to close log file");
                }
                finally {
                    this.currentOutStream = null;
                    this.currentFSOutStream = null;
                }
            }
        }
    }
    
    private void checkForErrors(final String message) throws MetricsException {
        if (!this.ignoreError && this.currentOutStream.checkError()) {
            throw new MetricsException(message + ": " + this.currentFilePath);
        }
    }
    
    private void throwMetricsException(final String message, final Throwable t) {
        if (!this.ignoreError) {
            throw new MetricsException(message + ": " + this.currentFilePath + " [" + t.toString() + "]", t);
        }
    }
    
    private void throwMetricsException(final String message) {
        if (!this.ignoreError) {
            throw new MetricsException(message + ": " + this.currentFilePath);
        }
    }
    
    static {
        DATE_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmm", TimeZone.getTimeZone("GMT"));
        RollingFileSystemSink.forceFlush = false;
        RollingFileSystemSink.hasFlushed = false;
        RollingFileSystemSink.suppliedConf = null;
        RollingFileSystemSink.suppliedFilesystem = null;
    }
}
