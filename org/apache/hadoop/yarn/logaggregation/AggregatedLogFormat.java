// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.logaggregation;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.hadoop.yarn.util.Times;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.commons.io.output.WriterOutputStream;
import java.io.Writer;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.io.EOFException;
import java.io.DataInputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.fs.Options;
import java.util.EnumSet;
import org.apache.hadoop.fs.CreateFlag;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.io.file.tfile.TFile;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import java.util.regex.Pattern;
import com.google.common.collect.Sets;
import com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import java.util.Arrays;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.io.SecureIOUtils;
import java.io.FileInputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.ConverterUtils;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.File;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import java.util.List;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.io.Writable;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AggregatedLogFormat
{
    private static final Log LOG;
    private static final LogKey APPLICATION_ACL_KEY;
    private static final LogKey APPLICATION_OWNER_KEY;
    private static final LogKey VERSION_KEY;
    private static final Map<String, LogKey> RESERVED_KEYS;
    private static final int VERSION = 1;
    private static final FsPermission APP_LOG_FILE_UMASK;
    
    static {
        LOG = LogFactory.getLog(AggregatedLogFormat.class);
        APPLICATION_ACL_KEY = new LogKey("APPLICATION_ACL");
        APPLICATION_OWNER_KEY = new LogKey("APPLICATION_OWNER");
        VERSION_KEY = new LogKey("VERSION");
        APP_LOG_FILE_UMASK = FsPermission.createImmutable((short)95);
        (RESERVED_KEYS = new HashMap<String, LogKey>()).put(AggregatedLogFormat.APPLICATION_ACL_KEY.toString(), AggregatedLogFormat.APPLICATION_ACL_KEY);
        AggregatedLogFormat.RESERVED_KEYS.put(AggregatedLogFormat.APPLICATION_OWNER_KEY.toString(), AggregatedLogFormat.APPLICATION_OWNER_KEY);
        AggregatedLogFormat.RESERVED_KEYS.put(AggregatedLogFormat.VERSION_KEY.toString(), AggregatedLogFormat.VERSION_KEY);
    }
    
    @InterfaceAudience.Public
    public static class LogKey implements Writable
    {
        private String keyString;
        
        public LogKey() {
        }
        
        public LogKey(final ContainerId containerId) {
            this.keyString = containerId.toString();
        }
        
        public LogKey(final String keyString) {
            this.keyString = keyString;
        }
        
        @Override
        public int hashCode() {
            return (this.keyString == null) ? 0 : this.keyString.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof LogKey)) {
                return false;
            }
            final LogKey other = (LogKey)obj;
            if (this.keyString == null) {
                return other.keyString == null;
            }
            return this.keyString.equals(other.keyString);
        }
        
        @InterfaceAudience.Private
        @Override
        public void write(final DataOutput out) throws IOException {
            out.writeUTF(this.keyString);
        }
        
        @InterfaceAudience.Private
        @Override
        public void readFields(final DataInput in) throws IOException {
            this.keyString = in.readUTF();
        }
        
        @Override
        public String toString() {
            return this.keyString;
        }
    }
    
    @InterfaceAudience.Private
    public static class LogValue
    {
        private final List<String> rootLogDirs;
        private final ContainerId containerId;
        private final String user;
        private final LogAggregationContext logAggregationContext;
        private Set<File> uploadedFiles;
        private final Set<String> alreadyUploadedLogFiles;
        private Set<String> allExistingFileMeta;
        
        public LogValue(final List<String> rootLogDirs, final ContainerId containerId, final String user) {
            this(rootLogDirs, containerId, user, null, new HashSet<String>());
        }
        
        public LogValue(final List<String> rootLogDirs, final ContainerId containerId, final String user, final LogAggregationContext logAggregationContext, final Set<String> alreadyUploadedLogFiles) {
            this.uploadedFiles = new HashSet<File>();
            this.allExistingFileMeta = new HashSet<String>();
            this.rootLogDirs = new ArrayList<String>(rootLogDirs);
            this.containerId = containerId;
            this.user = user;
            Collections.sort(this.rootLogDirs);
            this.logAggregationContext = logAggregationContext;
            this.alreadyUploadedLogFiles = alreadyUploadedLogFiles;
        }
        
        private Set<File> getPendingLogFilesToUploadForThisContainer() {
            final Set<File> pendingUploadFiles = new HashSet<File>();
            for (final String rootLogDir : this.rootLogDirs) {
                final File appLogDir = new File(rootLogDir, ConverterUtils.toString(this.containerId.getApplicationAttemptId().getApplicationId()));
                final File containerLogDir = new File(appLogDir, ConverterUtils.toString(this.containerId));
                if (!containerLogDir.isDirectory()) {
                    continue;
                }
                pendingUploadFiles.addAll(this.getPendingLogFilesToUpload(containerLogDir));
            }
            return pendingUploadFiles;
        }
        
        public void write(final DataOutputStream out, final Set<File> pendingUploadFiles) throws IOException {
            final List<File> fileList = new ArrayList<File>(pendingUploadFiles);
            Collections.sort(fileList);
            for (final File logFile : fileList) {
                if (logFile.isDirectory()) {
                    AggregatedLogFormat.LOG.warn(logFile.getAbsolutePath() + " is a directory. Ignore it.");
                }
                else {
                    FileInputStream in = null;
                    try {
                        in = this.secureOpenFile(logFile);
                    }
                    catch (IOException e) {
                        logErrorMessage(logFile, e);
                        IOUtils.cleanup(AggregatedLogFormat.LOG, in);
                        continue;
                    }
                    final long fileLength = logFile.length();
                    out.writeUTF(logFile.getName());
                    out.writeUTF(String.valueOf(fileLength));
                    try {
                        final byte[] buf = new byte[65535];
                        int len = 0;
                        long bytesLeft = fileLength;
                        while ((len = in.read(buf)) != -1) {
                            if (len >= bytesLeft) {
                                out.write(buf, 0, (int)bytesLeft);
                                break;
                            }
                            out.write(buf, 0, len);
                            bytesLeft -= len;
                        }
                        final long newLength = logFile.length();
                        if (fileLength < newLength) {
                            AggregatedLogFormat.LOG.warn("Aggregated logs truncated by approximately " + (newLength - fileLength) + " bytes.");
                        }
                        this.uploadedFiles.add(logFile);
                    }
                    catch (IOException e2) {
                        final String message = logErrorMessage(logFile, e2);
                        out.write(message.getBytes());
                    }
                    finally {
                        IOUtils.cleanup(AggregatedLogFormat.LOG, in);
                    }
                }
            }
        }
        
        @VisibleForTesting
        public FileInputStream secureOpenFile(final File logFile) throws IOException {
            return SecureIOUtils.openForRead(logFile, this.getUser(), null);
        }
        
        private static String logErrorMessage(final File logFile, final Exception e) {
            final String message = "Error aggregating log file. Log file : " + logFile.getAbsolutePath() + ". " + e.getMessage();
            AggregatedLogFormat.LOG.error(message, e);
            return message;
        }
        
        public String getUser() {
            return this.user;
        }
        
        private Set<File> getPendingLogFilesToUpload(final File containerLogDir) {
            Set<File> candidates = new HashSet<File>(Arrays.asList(containerLogDir.listFiles()));
            for (final File logFile : candidates) {
                this.allExistingFileMeta.add(this.getLogFileMetaData(logFile));
            }
            if (this.logAggregationContext != null && candidates.size() > 0) {
                if (this.logAggregationContext.getIncludePattern() != null && !this.logAggregationContext.getIncludePattern().isEmpty()) {
                    this.filterFiles(this.logAggregationContext.getIncludePattern(), candidates, false);
                }
                if (this.logAggregationContext.getExcludePattern() != null && !this.logAggregationContext.getExcludePattern().isEmpty()) {
                    this.filterFiles(this.logAggregationContext.getExcludePattern(), candidates, true);
                }
                final Iterable<File> mask = Iterables.filter(candidates, new Predicate<File>() {
                    @Override
                    public boolean apply(final File next) {
                        return !LogValue.this.alreadyUploadedLogFiles.contains(LogValue.this.getLogFileMetaData(next));
                    }
                });
                candidates = (Set<File>)Sets.newHashSet((Iterable<?>)mask);
            }
            return candidates;
        }
        
        private void filterFiles(final String pattern, final Set<File> candidates, final boolean exclusion) {
            final Pattern filterPattern = Pattern.compile(pattern);
            final Iterator<File> candidatesItr = candidates.iterator();
            while (candidatesItr.hasNext()) {
                final File candidate = candidatesItr.next();
                final boolean match = filterPattern.matcher(candidate.getName()).find();
                if ((!match && !exclusion) || (match && exclusion)) {
                    candidatesItr.remove();
                }
            }
        }
        
        public Set<Path> getCurrentUpLoadedFilesPath() {
            final Set<Path> path = new HashSet<Path>();
            for (final File file : this.uploadedFiles) {
                path.add(new Path(file.getAbsolutePath()));
            }
            return path;
        }
        
        public Set<String> getCurrentUpLoadedFileMeta() {
            final Set<String> info = new HashSet<String>();
            for (final File file : this.uploadedFiles) {
                info.add(this.getLogFileMetaData(file));
            }
            return info;
        }
        
        public Set<String> getAllExistingFilesMeta() {
            return this.allExistingFileMeta;
        }
        
        private String getLogFileMetaData(final File file) {
            return this.containerId.toString() + "_" + file.getName() + "_" + file.lastModified();
        }
    }
    
    @InterfaceAudience.Private
    public static class LogWriter
    {
        private final FSDataOutputStream fsDataOStream;
        private final TFile.Writer writer;
        private FileContext fc;
        
        public LogWriter(final Configuration conf, final Path remoteAppLogFile, final UserGroupInformation userUgi) throws IOException {
            try {
                this.fsDataOStream = userUgi.doAs((PrivilegedExceptionAction<FSDataOutputStream>)new PrivilegedExceptionAction<FSDataOutputStream>() {
                    @Override
                    public FSDataOutputStream run() throws Exception {
                        LogWriter.this.fc = FileContext.getFileContext(conf);
                        LogWriter.this.fc.setUMask(AggregatedLogFormat.APP_LOG_FILE_UMASK);
                        return LogWriter.this.fc.create(remoteAppLogFile, EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE), new Options.CreateOpts[0]);
                    }
                });
            }
            catch (InterruptedException e) {
                throw new IOException(e);
            }
            this.writer = new TFile.Writer(this.fsDataOStream, 262144, conf.get("yarn.nodemanager.log-aggregation.compression-type", "none"), null, conf);
            this.writeVersion();
        }
        
        @VisibleForTesting
        public TFile.Writer getWriter() {
            return this.writer;
        }
        
        private void writeVersion() throws IOException {
            DataOutputStream out = this.writer.prepareAppendKey(-1);
            AggregatedLogFormat.VERSION_KEY.write(out);
            out.close();
            out = this.writer.prepareAppendValue(-1);
            out.writeInt(1);
            out.close();
        }
        
        public void writeApplicationOwner(final String user) throws IOException {
            DataOutputStream out = this.writer.prepareAppendKey(-1);
            AggregatedLogFormat.APPLICATION_OWNER_KEY.write(out);
            out.close();
            out = this.writer.prepareAppendValue(-1);
            out.writeUTF(user);
            out.close();
        }
        
        public void writeApplicationACLs(final Map<ApplicationAccessType, String> appAcls) throws IOException {
            DataOutputStream out = this.writer.prepareAppendKey(-1);
            AggregatedLogFormat.APPLICATION_ACL_KEY.write(out);
            out.close();
            out = this.writer.prepareAppendValue(-1);
            for (final Map.Entry<ApplicationAccessType, String> entry : appAcls.entrySet()) {
                out.writeUTF(entry.getKey().toString());
                out.writeUTF(entry.getValue());
            }
            out.close();
        }
        
        public void append(final LogKey logKey, final LogValue logValue) throws IOException {
            final Set<File> pendingUploadFiles = logValue.getPendingLogFilesToUploadForThisContainer();
            if (pendingUploadFiles.size() == 0) {
                return;
            }
            DataOutputStream out = this.writer.prepareAppendKey(-1);
            logKey.write(out);
            out.close();
            out = this.writer.prepareAppendValue(-1);
            logValue.write(out, pendingUploadFiles);
            out.close();
        }
        
        public void close() {
            try {
                this.writer.close();
            }
            catch (IOException e) {
                AggregatedLogFormat.LOG.warn("Exception closing writer", e);
            }
            IOUtils.closeStream(this.fsDataOStream);
        }
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static class LogReader
    {
        private final FSDataInputStream fsDataIStream;
        private final TFile.Reader.Scanner scanner;
        private final TFile.Reader reader;
        private boolean atBeginning;
        
        public LogReader(final Configuration conf, final Path remoteAppLogFile) throws IOException {
            this.atBeginning = true;
            final FileContext fileContext = FileContext.getFileContext(conf);
            this.fsDataIStream = fileContext.open(remoteAppLogFile);
            this.reader = new TFile.Reader(this.fsDataIStream, fileContext.getFileStatus(remoteAppLogFile).getLen(), conf);
            this.scanner = this.reader.createScanner();
        }
        
        public String getApplicationOwner() throws IOException {
            final TFile.Reader.Scanner ownerScanner = this.reader.createScanner();
            final LogKey key = new LogKey();
            while (!ownerScanner.atEnd()) {
                final TFile.Reader.Scanner.Entry entry = ownerScanner.entry();
                key.readFields(entry.getKeyStream());
                if (key.toString().equals(AggregatedLogFormat.APPLICATION_OWNER_KEY.toString())) {
                    final DataInputStream valueStream = entry.getValueStream();
                    return valueStream.readUTF();
                }
                ownerScanner.advance();
            }
            return null;
        }
        
        public Map<ApplicationAccessType, String> getApplicationAcls() throws IOException {
            final TFile.Reader.Scanner aclScanner = this.reader.createScanner();
            final LogKey key = new LogKey();
            final Map<ApplicationAccessType, String> acls = new HashMap<ApplicationAccessType, String>();
            while (!aclScanner.atEnd()) {
                final TFile.Reader.Scanner.Entry entry = aclScanner.entry();
                key.readFields(entry.getKeyStream());
                if (key.toString().equals(AggregatedLogFormat.APPLICATION_ACL_KEY.toString())) {
                    final DataInputStream valueStream = entry.getValueStream();
                    while (true) {
                        String appAccessOp = null;
                        String aclString = null;
                        try {
                            appAccessOp = valueStream.readUTF();
                        }
                        catch (EOFException e) {
                            break;
                        }
                        try {
                            aclString = valueStream.readUTF();
                        }
                        catch (EOFException e) {
                            throw new YarnRuntimeException("Error reading ACLs", e);
                        }
                        acls.put(ApplicationAccessType.valueOf(appAccessOp), aclString);
                    }
                }
                aclScanner.advance();
            }
            return acls;
        }
        
        public DataInputStream next(final LogKey key) throws IOException {
            if (!this.atBeginning) {
                this.scanner.advance();
            }
            else {
                this.atBeginning = false;
            }
            if (this.scanner.atEnd()) {
                return null;
            }
            final TFile.Reader.Scanner.Entry entry = this.scanner.entry();
            key.readFields(entry.getKeyStream());
            if (AggregatedLogFormat.RESERVED_KEYS.containsKey(key.toString())) {
                return this.next(key);
            }
            final DataInputStream valueStream = entry.getValueStream();
            return valueStream;
        }
        
        @InterfaceAudience.Private
        public ContainerLogsReader getContainerLogsReader(final ContainerId containerId) throws IOException {
            ContainerLogsReader logReader = null;
            LogKey containerKey;
            LogKey key;
            DataInputStream valueStream;
            for (containerKey = new LogKey(containerId), key = new LogKey(), valueStream = this.next(key); valueStream != null && !key.equals(containerKey); valueStream = this.next(key)) {}
            if (valueStream != null) {
                logReader = new ContainerLogsReader(valueStream);
            }
            return logReader;
        }
        
        public static void readAcontainerLogs(final DataInputStream valueStream, final Writer writer, final long logUploadedTime) throws IOException {
            OutputStream os = null;
            PrintStream ps = null;
            try {
                os = new WriterOutputStream(writer);
                ps = new PrintStream(os);
                try {
                    while (true) {
                        readContainerLogs(valueStream, ps, logUploadedTime);
                    }
                }
                catch (EOFException e) {}
            }
            finally {
                IOUtils.cleanup(AggregatedLogFormat.LOG, ps);
                IOUtils.cleanup(AggregatedLogFormat.LOG, os);
            }
        }
        
        public static void readAcontainerLogs(final DataInputStream valueStream, final Writer writer) throws IOException {
            readAcontainerLogs(valueStream, writer, -1L);
        }
        
        private static void readContainerLogs(final DataInputStream valueStream, final PrintStream out, final long logUploadedTime) throws IOException {
            final byte[] buf = new byte[65535];
            final String fileType = valueStream.readUTF();
            final String fileLengthStr = valueStream.readUTF();
            final long fileLength = Long.parseLong(fileLengthStr);
            out.print("LogType:");
            out.println(fileType);
            if (logUploadedTime != -1L) {
                out.print("Log Upload Time:");
                out.println(Times.format(logUploadedTime));
            }
            out.print("LogLength:");
            out.println(fileLengthStr);
            out.println("Log Contents:");
            long curRead = 0L;
            long pendingRead = fileLength - curRead;
            for (int toRead = (pendingRead > buf.length) ? buf.length : ((int)pendingRead), len = valueStream.read(buf, 0, toRead); len != -1 && curRead < fileLength; curRead += len, pendingRead = fileLength - curRead, toRead = ((pendingRead > buf.length) ? buf.length : ((int)pendingRead)), len = valueStream.read(buf, 0, toRead)) {
                out.write(buf, 0, len);
            }
            out.println("");
        }
        
        public static void readAContainerLogsForALogType(final DataInputStream valueStream, final PrintStream out, final long logUploadedTime) throws IOException {
            readContainerLogs(valueStream, out, logUploadedTime);
        }
        
        public static void readAContainerLogsForALogType(final DataInputStream valueStream, final PrintStream out) throws IOException {
            readAContainerLogsForALogType(valueStream, out, -1L);
        }
        
        public void close() {
            IOUtils.cleanup(AggregatedLogFormat.LOG, this.scanner, this.reader, this.fsDataIStream);
        }
    }
    
    @InterfaceAudience.Private
    public static class ContainerLogsReader
    {
        private DataInputStream valueStream;
        private String currentLogType;
        private long currentLogLength;
        private BoundedInputStream currentLogData;
        private InputStreamReader currentLogISR;
        
        public ContainerLogsReader(final DataInputStream stream) {
            this.currentLogType = null;
            this.currentLogLength = 0L;
            this.currentLogData = null;
            this.valueStream = stream;
        }
        
        public String nextLog() throws IOException {
            if (this.currentLogData != null && this.currentLogLength > 0L) {
                while (this.currentLogData.skip(this.currentLogLength) >= 0L) {
                    if (this.currentLogData.read() == -1) {
                        break;
                    }
                }
            }
            this.currentLogType = null;
            this.currentLogLength = 0L;
            this.currentLogData = null;
            this.currentLogISR = null;
            try {
                final String logType = this.valueStream.readUTF();
                final String logLengthStr = this.valueStream.readUTF();
                this.currentLogLength = Long.parseLong(logLengthStr);
                (this.currentLogData = new BoundedInputStream(this.valueStream, this.currentLogLength)).setPropagateClose(false);
                this.currentLogISR = new InputStreamReader(this.currentLogData);
                this.currentLogType = logType;
            }
            catch (EOFException ex) {}
            return this.currentLogType;
        }
        
        public String getCurrentLogType() {
            return this.currentLogType;
        }
        
        public long getCurrentLogLength() {
            return this.currentLogLength;
        }
        
        public long skip(final long n) throws IOException {
            return this.currentLogData.skip(n);
        }
        
        public int read(final byte[] buf, final int off, final int len) throws IOException {
            return this.currentLogData.read(buf, off, len);
        }
        
        public int read(final char[] buf, final int off, final int len) throws IOException {
            return this.currentLogISR.read(buf, off, len);
        }
    }
}
