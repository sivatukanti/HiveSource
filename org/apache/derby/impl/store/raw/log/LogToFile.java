// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.io.StorageFactory;
import org.apache.derby.iapi.services.io.FileUtil;
import org.apache.derby.iapi.util.ReuseFactory;
import java.io.OutputStreamWriter;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.FileNotFoundException;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.Serializable;
import org.apache.derby.iapi.error.ErrorStringBuilder;
import java.io.SyncFailedException;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.property.PersistentSet;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.derby.iapi.store.raw.ScanHandle;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.raw.log.LogScan;
import org.apache.derby.iapi.store.raw.Loggable;
import java.io.Writer;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import org.apache.derby.iapi.store.raw.log.Logger;
import java.io.IOException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.io.File;
import org.apache.derby.io.StorageFile;
import java.util.zip.CRC32;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.replication.master.MasterFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.io.WritableStorageFactory;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.raw.log.LogFactory;

public final class LogToFile implements LogFactory, ModuleControl, ModuleSupportable, Serviceable, PrivilegedExceptionAction
{
    private static final long INT_LENGTH = 4L;
    private static int fid;
    public static final int LOG_FILE_HEADER_SIZE = 24;
    protected static final int LOG_FILE_HEADER_PREVIOUS_LOG_INSTANT_OFFSET = 16;
    public static final int LOG_RECORD_OVERHEAD = 16;
    public static final String DBG_FLAG;
    public static final String DUMP_LOG_ONLY;
    public static final String DUMP_LOG_FROM_LOG_FILE;
    protected static final String LOG_SYNC_STATISTICS = "LogSyncStatistics";
    private static final int OBSOLETE_LOG_VERSION_NUMBER = 9;
    private static final int DEFAULT_LOG_SWITCH_INTERVAL = 1048576;
    private static final int LOG_SWITCH_INTERVAL_MIN = 100000;
    private static final int LOG_SWITCH_INTERVAL_MAX = 134217728;
    private static final int CHECKPOINT_INTERVAL_MIN = 100000;
    private static final int CHECKPOINT_INTERVAL_MAX = 134217728;
    private static final int DEFAULT_CHECKPOINT_INTERVAL = 10485760;
    private static final int DEFAULT_LOG_BUFFER_SIZE = 32768;
    private static final int LOG_BUFFER_SIZE_MIN = 8192;
    private static final int LOG_BUFFER_SIZE_MAX = 134217728;
    private int logBufferSize;
    private static final byte IS_BETA_FLAG = 1;
    private static final byte IS_DURABILITY_TESTMODE_NO_SYNC_FLAG = 2;
    private static boolean wasDBInDurabilityTestModeNoSync;
    private static final String DEFAULT_LOG_ARCHIVE_DIRECTORY = "DEFAULT";
    private int logSwitchInterval;
    private int checkpointInterval;
    String dataDirectory;
    private WritableStorageFactory logStorageFactory;
    private boolean logBeingFlushed;
    protected LogAccessFile logOut;
    private StorageRandomAccessFile firstLog;
    protected long endPosition;
    long lastFlush;
    long logFileNumber;
    long bootTimeLogFileNumber;
    long firstLogFileNumber;
    private long maxLogFileNumber;
    private CheckpointOperation currentCheckpoint;
    long checkpointInstant;
    private DaemonService checkpointDaemon;
    private int myClientNumber;
    private volatile boolean checkpointDaemonCalled;
    private long logWrittenFromLastCheckPoint;
    private RawStoreFactory rawStoreFactory;
    protected DataFactory dataFactory;
    protected boolean ReadOnlyDB;
    private MasterFactory masterFactory;
    private boolean inReplicationMasterMode;
    private boolean inReplicationSlaveMode;
    private volatile StandardException replicationSlaveException;
    private boolean inReplicationSlavePreMode;
    private Object slaveRecoveryMonitor;
    private long allowedToReadFileNumber;
    private boolean keepAllLogs;
    private boolean databaseEncrypted;
    private boolean recoveryNeeded;
    private boolean inCheckpoint;
    private boolean inRedo;
    private boolean inLogSwitch;
    private boolean stopped;
    String logDevice;
    private boolean logNotSynced;
    private volatile boolean logArchived;
    private boolean logSwitchRequired;
    int test_logWritten;
    int test_numRecordToFillLog;
    private int mon_flushCalls;
    private int mon_syncCalls;
    private int mon_numLogFlushWaits;
    private boolean mon_LogSyncStatistics;
    private int mon_numBytesToLog;
    protected volatile StandardException corrupt;
    private boolean isFrozen;
    ProductVersionHolder jbmsVersion;
    private int onDiskMajorVersion;
    private int onDiskMinorVersion;
    private boolean onDiskBeta;
    private CRC32 checksum;
    private boolean isWriteSynced;
    private boolean jvmSyncErrorChecked;
    private volatile long logFileToBackup;
    private volatile boolean backupInProgress;
    public static final String TEST_LOG_SWITCH_LOG;
    public static final String TEST_LOG_INCOMPLETE_LOG_WRITE;
    public static final String TEST_LOG_PARTIAL_LOG_WRITE_NUM_BYTES;
    public static final String TEST_LOG_FULL;
    public static final String TEST_SWITCH_LOG_FAIL1;
    public static final String TEST_SWITCH_LOG_FAIL2;
    public static final String TEST_RECORD_TO_FILL_LOG;
    public static final String TEST_MAX_LOGFILE_NUMBER;
    private int action;
    private StorageFile activeFile;
    private File toFile;
    private String activePerms;
    
    public int getTypeFormatId() {
        return 128;
    }
    
    public LogToFile() {
        this.logBufferSize = 32768;
        this.logSwitchInterval = 1048576;
        this.checkpointInterval = 10485760;
        this.firstLog = null;
        this.endPosition = -1L;
        this.lastFlush = 0L;
        this.logFileNumber = -1L;
        this.bootTimeLogFileNumber = -1L;
        this.firstLogFileNumber = -1L;
        this.maxLogFileNumber = 2147483647L;
        this.logWrittenFromLastCheckPoint = 0L;
        this.inReplicationMasterMode = false;
        this.inReplicationSlaveMode = false;
        this.replicationSlaveException = null;
        this.inReplicationSlavePreMode = false;
        this.allowedToReadFileNumber = -1L;
        this.recoveryNeeded = true;
        this.inCheckpoint = false;
        this.inRedo = false;
        this.inLogSwitch = false;
        this.stopped = false;
        this.logNotSynced = false;
        this.logArchived = false;
        this.logSwitchRequired = false;
        this.test_logWritten = 0;
        this.test_numRecordToFillLog = -1;
        this.checksum = new CRC32();
        this.isWriteSynced = false;
        this.jvmSyncErrorChecked = false;
        this.backupInProgress = false;
        this.keepAllLogs = PropertyUtil.getSystemBoolean("derby.storage.keepTransactionLog");
    }
    
    public StandardException markCorrupt(final StandardException corrupt) {
        boolean b = false;
        synchronized (this) {
            if (this.corrupt == null && corrupt != null) {
                this.corrupt = corrupt;
                b = true;
            }
        }
        if (this.corrupt == corrupt) {
            this.logErrMsg(this.corrupt);
        }
        if (b) {
            synchronized (this) {
                this.stopped = true;
                if (this.logOut != null) {
                    try {
                        this.logOut.corrupt();
                    }
                    catch (IOException ex) {}
                }
                this.logOut = null;
            }
            if (this.dataFactory != null) {
                this.dataFactory.markCorrupt(null);
            }
        }
        return corrupt;
    }
    
    private void checkCorrupt() throws StandardException {
        synchronized (this) {
            if (this.corrupt != null) {
                throw StandardException.newException("XSLAA.D", this.corrupt);
            }
        }
    }
    
    public Logger getLogger() {
        if (this.ReadOnlyDB) {
            return null;
        }
        return new FileLogger(this);
    }
    
    public void setRawStoreFactory(final RawStoreFactory rawStoreFactory) {
        this.rawStoreFactory = rawStoreFactory;
    }
    
    public void recover(final DataFactory dataFactory, final TransactionFactory transactionFactory) throws StandardException {
        this.checkCorrupt();
        this.dataFactory = dataFactory;
        if (this.firstLog != null) {
            this.logOut = new LogAccessFile(this, this.firstLog, this.logBufferSize);
        }
        if (this.inReplicationSlaveMode) {
            synchronized (this.slaveRecoveryMonitor) {
                while (this.inReplicationSlaveMode && this.allowedToReadFileNumber < this.bootTimeLogFileNumber) {
                    if (this.replicationSlaveException != null) {
                        throw this.replicationSlaveException;
                    }
                    try {
                        this.slaveRecoveryMonitor.wait();
                    }
                    catch (InterruptedException ex7) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
        }
        Label_1228: {
            if (this.recoveryNeeded) {
                try {
                    final FileLogger fileLogger = (FileLogger)this.getLogger();
                    if (this.checkpointInstant != 0L) {
                        this.currentCheckpoint = this.findCheckpoint(this.checkpointInstant, fileLogger);
                    }
                    long redoLWM = 0L;
                    long undoLWM = 0L;
                    long checkpointInstant = 0L;
                    StreamLogScan streamLogScan;
                    if (this.currentCheckpoint != null) {
                        final Formatable formatable = null;
                        transactionFactory.useTransactionTable(formatable);
                        redoLWM = this.currentCheckpoint.redoLWM();
                        undoLWM = this.currentCheckpoint.undoLWM();
                        if (formatable != null) {
                            checkpointInstant = this.checkpointInstant;
                        }
                        this.firstLogFileNumber = LogCounter.getLogFileNumber(redoLWM);
                        if (LogCounter.getLogFileNumber(undoLWM) < this.firstLogFileNumber) {
                            this.firstLogFileNumber = LogCounter.getLogFileNumber(undoLWM);
                        }
                        streamLogScan = (StreamLogScan)this.openForwardsScan(undoLWM, null);
                    }
                    else {
                        transactionFactory.useTransactionTable(null);
                        final long logInstantAsLong = LogCounter.makeLogInstantAsLong(this.bootTimeLogFileNumber, 24L);
                        this.firstLogFileNumber = this.bootTimeLogFileNumber;
                        streamLogScan = (StreamLogScan)this.openForwardsScan(logInstantAsLong, null);
                    }
                    final RawTransaction startTransaction = transactionFactory.startTransaction(this.rawStoreFactory, ContextService.getFactory().getCurrentContextManager(), "UserTransaction");
                    startTransaction.recoveryTransaction();
                    this.inRedo = true;
                    final long redo = fileLogger.redo(startTransaction, transactionFactory, streamLogScan, redoLWM, checkpointInstant);
                    this.inRedo = false;
                    this.logFileNumber = this.bootTimeLogFileNumber;
                    StorageRandomAccessFile storageRandomAccessFile = null;
                    if (redo == 0L) {
                        Monitor.logTextMessage("L007");
                        StorageFile storageFile = this.getLogFileName(this.logFileNumber);
                        if (this.privExists(storageFile) && !this.privDelete(storageFile)) {
                            final long logFileNumber = this.logFileNumber + 1L;
                            this.logFileNumber = logFileNumber;
                            storageFile = this.getLogFileName(logFileNumber);
                        }
                        Throwable t = null;
                        try {
                            storageRandomAccessFile = this.privRandomAccessFile(storageFile, "rw");
                        }
                        catch (IOException ex) {
                            storageRandomAccessFile = null;
                            t = ex;
                        }
                        if (storageRandomAccessFile == null || !this.privCanWrite(storageFile)) {
                            if (storageRandomAccessFile != null) {
                                storageRandomAccessFile.close();
                            }
                            storageRandomAccessFile = null;
                            Monitor.logTextMessage("L022");
                            if (t != null) {
                                Monitor.logThrowable(t);
                            }
                            this.ReadOnlyDB = true;
                        }
                        else {
                            try {
                                if (!this.initLogFile(storageRandomAccessFile, this.logFileNumber, 0L)) {
                                    throw this.markCorrupt(StandardException.newException("XSLAQ.D", storageFile.getPath()));
                                }
                            }
                            catch (IOException ex2) {
                                throw this.markCorrupt(StandardException.newException("XSLA2.D", ex2));
                            }
                            this.setEndPosition(storageRandomAccessFile.getFilePointer());
                            this.lastFlush = this.endPosition;
                            if (this.isWriteSynced) {
                                this.preAllocateNewLogFile(storageRandomAccessFile);
                                storageRandomAccessFile.close();
                                storageRandomAccessFile = this.openLogFileInWriteMode(storageFile);
                                storageRandomAccessFile.seek(this.endPosition);
                            }
                            this.logSwitchRequired = false;
                        }
                    }
                    else {
                        this.logFileNumber = LogCounter.getLogFileNumber(redo);
                        this.ReadOnlyDB = dataFactory.isReadOnly();
                        final StorageFile logFileName = this.getLogFileName(this.logFileNumber);
                        if (!this.ReadOnlyDB) {
                            Throwable t2 = null;
                            try {
                                if (this.isWriteSynced) {
                                    storageRandomAccessFile = this.openLogFileInWriteMode(logFileName);
                                }
                                else {
                                    storageRandomAccessFile = this.privRandomAccessFile(logFileName, "rw");
                                }
                            }
                            catch (IOException ex3) {
                                storageRandomAccessFile = null;
                                t2 = ex3;
                            }
                            if (storageRandomAccessFile == null || !this.privCanWrite(logFileName)) {
                                if (storageRandomAccessFile != null) {
                                    storageRandomAccessFile.close();
                                }
                                storageRandomAccessFile = null;
                                Monitor.logTextMessage("L022");
                                if (t2 != null) {
                                    Monitor.logThrowable(t2);
                                }
                                this.ReadOnlyDB = true;
                            }
                        }
                        if (!this.ReadOnlyDB) {
                            this.setEndPosition(LogCounter.getLogFilePosition(redo));
                            if (streamLogScan.isLogEndFuzzy()) {
                                storageRandomAccessFile.seek(this.endPosition);
                                final long length = storageRandomAccessFile.length();
                                Monitor.logTextMessage("L010", logFileName, new Long(this.endPosition), new Long(length));
                                long n = (length - this.endPosition) / this.logBufferSize;
                                final int n2 = (int)((length - this.endPosition) % this.logBufferSize);
                                final byte[] array = new byte[this.logBufferSize];
                                while (n-- > 0L) {
                                    storageRandomAccessFile.write(array);
                                }
                                if (n2 != 0) {
                                    storageRandomAccessFile.write(array, 0, n2);
                                }
                                if (!this.isWriteSynced) {
                                    this.syncFile(storageRandomAccessFile);
                                }
                            }
                            this.lastFlush = this.endPosition;
                            storageRandomAccessFile.seek(this.endPosition);
                        }
                    }
                    if (storageRandomAccessFile != null) {
                        if (this.logOut != null) {
                            this.logOut.close();
                        }
                        this.logOut = new LogAccessFile(this, storageRandomAccessFile, this.logBufferSize);
                    }
                    if (this.logSwitchRequired) {
                        this.switchLogFile();
                    }
                    final boolean noActiveUpdateTransaction = transactionFactory.noActiveUpdateTransaction();
                    if (this.ReadOnlyDB && !noActiveUpdateTransaction) {
                        throw StandardException.newException("XSLAF.D");
                    }
                    if (!noActiveUpdateTransaction) {
                        transactionFactory.rollbackAllTransactions(startTransaction, this.rawStoreFactory);
                    }
                    transactionFactory.handlePreparedXacts(this.rawStoreFactory);
                    startTransaction.close();
                    this.dataFactory.postRecovery();
                    transactionFactory.resetTranId();
                    if (!this.ReadOnlyDB) {
                        boolean b = true;
                        if (this.currentCheckpoint != null && noActiveUpdateTransaction && redoLWM != 0L && undoLWM != 0L && this.logFileNumber == LogCounter.getLogFileNumber(redoLWM) && this.logFileNumber == LogCounter.getLogFileNumber(undoLWM) && this.endPosition < LogCounter.getLogFilePosition(redoLWM) + 1000L) {
                            b = false;
                        }
                        if (b && !this.checkpoint(this.rawStoreFactory, dataFactory, transactionFactory, false)) {
                            this.flush(this.logFileNumber, this.endPosition);
                        }
                    }
                    fileLogger.close();
                    this.recoveryNeeded = false;
                    break Label_1228;
                }
                catch (IOException ex4) {
                    throw this.markCorrupt(StandardException.newException("XSLA2.D", ex4));
                }
                catch (ClassNotFoundException ex5) {
                    throw this.markCorrupt(StandardException.newException("XSLA3.D", ex5));
                }
                catch (StandardException ex6) {
                    throw this.markCorrupt(ex6);
                }
                catch (Throwable t3) {
                    throw this.markCorrupt(StandardException.newException("XSLA6.D", t3));
                }
            }
            transactionFactory.useTransactionTable(null);
            transactionFactory.resetTranId();
        }
        this.checkpointDaemon = this.rawStoreFactory.getDaemon();
        if (this.checkpointDaemon != null) {
            this.myClientNumber = this.checkpointDaemon.subscribe(this, true);
            this.dataFactory.setupCacheCleaner(this.checkpointDaemon);
        }
    }
    
    public boolean checkpoint(final RawStoreFactory rawStoreFactory, final DataFactory dataFactory, final TransactionFactory transactionFactory, final boolean b) throws StandardException {
        return this.inReplicationSlavePreMode || this.checkpointWithTran(null, rawStoreFactory, dataFactory, transactionFactory, b);
    }
    
    private boolean checkpointWithTran(final RawTransaction startInternalTransaction, final RawStoreFactory rawStoreFactory, final DataFactory dataFactory, final TransactionFactory transactionFactory, final boolean b) throws StandardException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        org/apache/derby/impl/store/raw/log/LogToFile.logOut:Lorg/apache/derby/impl/store/raw/log/LogAccessFile;
        //     4: ifnonnull       9
        //     7: iconst_0       
        //     8: ireturn        
        //     9: iconst_1       
        //    10: istore          9
        //    12: aload_0        
        //    13: dup            
        //    14: astore          10
        //    16: monitorenter   
        //    17: aload_0        
        //    18: getfield        org/apache/derby/impl/store/raw/log/LogToFile.corrupt:Lorg/apache/derby/iapi/error/StandardException;
        //    21: ifnull          34
        //    24: ldc             "XSLAA.D"
        //    26: aload_0        
        //    27: getfield        org/apache/derby/impl/store/raw/log/LogToFile.corrupt:Lorg/apache/derby/iapi/error/StandardException;
        //    30: invokestatic    org/apache/derby/iapi/error/StandardException.newException:(Ljava/lang/String;Ljava/lang/Throwable;)Lorg/apache/derby/iapi/error/StandardException;
        //    33: athrow         
        //    34: aload_0        
        //    35: getfield        org/apache/derby/impl/store/raw/log/LogToFile.endPosition:J
        //    38: lstore          7
        //    40: aload_0        
        //    41: getfield        org/apache/derby/impl/store/raw/log/LogToFile.inCheckpoint:Z
        //    44: ifne            58
        //    47: aload_0        
        //    48: iconst_1       
        //    49: putfield        org/apache/derby/impl/store/raw/log/LogToFile.inCheckpoint:Z
        //    52: aload           10
        //    54: monitorexit    
        //    55: goto            107
        //    58: iload           5
        //    60: ifeq            85
        //    63: aload_0        
        //    64: getfield        org/apache/derby/impl/store/raw/log/LogToFile.inCheckpoint:Z
        //    67: ifeq            88
        //    70: aload_0        
        //    71: invokevirtual   java/lang/Object.wait:()V
        //    74: goto            63
        //    77: astore          11
        //    79: invokestatic    org/apache/derby/iapi/util/InterruptStatus.setInterrupted:()V
        //    82: goto            63
        //    85: iconst_0       
        //    86: istore          9
        //    88: aload           10
        //    90: monitorexit    
        //    91: goto            102
        //    94: astore          12
        //    96: aload           10
        //    98: monitorexit    
        //    99: aload           12
        //   101: athrow         
        //   102: iload           9
        //   104: ifne            12
        //   107: iload           9
        //   109: ifne            114
        //   112: iconst_0       
        //   113: ireturn        
        //   114: aload_1        
        //   115: ifnonnull       122
        //   118: iconst_1       
        //   119: goto            123
        //   122: iconst_0       
        //   123: istore          10
        //   125: lload           7
        //   127: aload_0        
        //   128: getfield        org/apache/derby/impl/store/raw/log/LogToFile.logSwitchInterval:I
        //   131: i2l            
        //   132: lcmp           
        //   133: ifle            148
        //   136: aload_0        
        //   137: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.switchLogFile:()V
        //   140: aload_0        
        //   141: lconst_0       
        //   142: putfield        org/apache/derby/impl/store/raw/log/LogToFile.logWrittenFromLastCheckPoint:J
        //   145: goto            157
        //   148: aload_0        
        //   149: aload_0        
        //   150: getfield        org/apache/derby/impl/store/raw/log/LogToFile.endPosition:J
        //   153: lneg           
        //   154: putfield        org/apache/derby/impl/store/raw/log/LogToFile.logWrittenFromLastCheckPoint:J
        //   157: iload           10
        //   159: ifeq            177
        //   162: aload           4
        //   164: aload_2        
        //   165: invokestatic    org/apache/derby/iapi/services/context/ContextService.getFactory:()Lorg/apache/derby/iapi/services/context/ContextService;
        //   168: invokevirtual   org/apache/derby/iapi/services/context/ContextService.getCurrentContextManager:()Lorg/apache/derby/iapi/services/context/ContextManager;
        //   171: invokeinterface org/apache/derby/iapi/store/raw/xact/TransactionFactory.startInternalTransaction:(Lorg/apache/derby/iapi/store/raw/RawStoreFactory;Lorg/apache/derby/iapi/services/context/ContextManager;)Lorg/apache/derby/iapi/store/raw/xact/RawTransaction;
        //   176: astore_1       
        //   177: aload_0        
        //   178: dup            
        //   179: astore          15
        //   181: monitorenter   
        //   182: aload_0        
        //   183: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.currentInstant:()J
        //   186: lstore          13
        //   188: new             Lorg/apache/derby/impl/store/raw/log/LogCounter;
        //   191: dup            
        //   192: lload           13
        //   194: invokespecial   org/apache/derby/impl/store/raw/log/LogCounter.<init>:(J)V
        //   197: astore          6
        //   199: aload           4
        //   201: invokeinterface org/apache/derby/iapi/store/raw/xact/TransactionFactory.firstUpdateInstant:()Lorg/apache/derby/iapi/store/raw/log/LogInstant;
        //   206: checkcast       Lorg/apache/derby/impl/store/raw/log/LogCounter;
        //   209: checkcast       Lorg/apache/derby/impl/store/raw/log/LogCounter;
        //   212: astore          16
        //   214: aload           16
        //   216: ifnonnull       226
        //   219: lload           13
        //   221: lstore          11
        //   223: goto            233
        //   226: aload           16
        //   228: invokevirtual   org/apache/derby/impl/store/raw/log/LogCounter.getValueAsLong:()J
        //   231: lstore          11
        //   233: aload           15
        //   235: monitorexit    
        //   236: goto            247
        //   239: astore          17
        //   241: aload           15
        //   243: monitorexit    
        //   244: aload           17
        //   246: athrow         
        //   247: aload_3        
        //   248: invokeinterface org/apache/derby/iapi/store/raw/data/DataFactory.checkpoint:()V
        //   253: aload           4
        //   255: invokeinterface org/apache/derby/iapi/store/raw/xact/TransactionFactory.getTransactionTable:()Lorg/apache/derby/iapi/services/io/Formatable;
        //   260: astore          15
        //   262: new             Lorg/apache/derby/impl/store/raw/log/CheckpointOperation;
        //   265: dup            
        //   266: lload           13
        //   268: lload           11
        //   270: aload           15
        //   272: invokespecial   org/apache/derby/impl/store/raw/log/CheckpointOperation.<init>:(JJLorg/apache/derby/iapi/services/io/Formatable;)V
        //   275: astore          16
        //   277: aload_1        
        //   278: aload           16
        //   280: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.logAndDo:(Lorg/apache/derby/iapi/store/raw/Loggable;)V
        //   283: aload_1        
        //   284: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.getLastLogInstant:()Lorg/apache/derby/iapi/store/raw/log/LogInstant;
        //   287: checkcast       Lorg/apache/derby/impl/store/raw/log/LogCounter;
        //   290: checkcast       Lorg/apache/derby/impl/store/raw/log/LogCounter;
        //   293: astore          17
        //   295: aload           17
        //   297: ifnull          309
        //   300: aload_0        
        //   301: aload           17
        //   303: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.flush:(Lorg/apache/derby/iapi/store/raw/log/LogInstant;)V
        //   306: goto            315
        //   309: ldc             "XSLAI.D"
        //   311: invokestatic    org/apache/derby/iapi/error/StandardException.newException:(Ljava/lang/String;)Lorg/apache/derby/iapi/error/StandardException;
        //   314: athrow         
        //   315: aload_1        
        //   316: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.commit:()Lorg/apache/derby/iapi/store/raw/log/LogInstant;
        //   319: pop            
        //   320: iload           10
        //   322: ifeq            331
        //   325: aload_1        
        //   326: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.close:()V
        //   329: aconst_null    
        //   330: astore_1       
        //   331: aload_0        
        //   332: aload_0        
        //   333: invokespecial   org/apache/derby/impl/store/raw/log/LogToFile.getControlFileName:()Lorg/apache/derby/io/StorageFile;
        //   336: aload           17
        //   338: invokevirtual   org/apache/derby/impl/store/raw/log/LogCounter.getValueAsLong:()J
        //   341: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.writeControlFile:(Lorg/apache/derby/io/StorageFile;J)Z
        //   344: ifne            357
        //   347: ldc             "XSLAE.D"
        //   349: aload_0        
        //   350: invokespecial   org/apache/derby/impl/store/raw/log/LogToFile.getControlFileName:()Lorg/apache/derby/io/StorageFile;
        //   353: invokestatic    org/apache/derby/iapi/error/StandardException.newException:(Ljava/lang/String;Ljava/lang/Object;)Lorg/apache/derby/iapi/error/StandardException;
        //   356: athrow         
        //   357: aload_0        
        //   358: aload           16
        //   360: putfield        org/apache/derby/impl/store/raw/log/LogToFile.currentCheckpoint:Lorg/apache/derby/impl/store/raw/log/CheckpointOperation;
        //   363: aload_0        
        //   364: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.logArchived:()Z
        //   367: ifne            378
        //   370: aload_0        
        //   371: aload_0        
        //   372: getfield        org/apache/derby/impl/store/raw/log/LogToFile.currentCheckpoint:Lorg/apache/derby/impl/store/raw/log/CheckpointOperation;
        //   375: invokespecial   org/apache/derby/impl/store/raw/log/LogToFile.truncateLog:(Lorg/apache/derby/impl/store/raw/log/CheckpointOperation;)V
        //   378: aload_0        
        //   379: getfield        org/apache/derby/impl/store/raw/log/LogToFile.backupInProgress:Z
        //   382: ifne            393
        //   385: aload_3        
        //   386: aload           6
        //   388: invokeinterface org/apache/derby/iapi/store/raw/data/DataFactory.removeDroppedContainerFileStubs:(Lorg/apache/derby/iapi/store/raw/log/LogInstant;)V
        //   393: jsr             421
        //   396: goto            488
        //   399: astore          11
        //   401: aload_0        
        //   402: ldc             "XSLA2.D"
        //   404: aload           11
        //   406: invokestatic    org/apache/derby/iapi/error/StandardException.newException:(Ljava/lang/String;Ljava/lang/Throwable;)Lorg/apache/derby/iapi/error/StandardException;
        //   409: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.markCorrupt:(Lorg/apache/derby/iapi/error/StandardException;)Lorg/apache/derby/iapi/error/StandardException;
        //   412: athrow         
        //   413: astore          18
        //   415: jsr             421
        //   418: aload           18
        //   420: athrow         
        //   421: astore          19
        //   423: aload_0        
        //   424: dup            
        //   425: astore          20
        //   427: monitorenter   
        //   428: aload_0        
        //   429: iconst_0       
        //   430: putfield        org/apache/derby/impl/store/raw/log/LogToFile.inCheckpoint:Z
        //   433: aload_0        
        //   434: invokevirtual   java/lang/Object.notifyAll:()V
        //   437: aload           20
        //   439: monitorexit    
        //   440: goto            451
        //   443: astore          21
        //   445: aload           20
        //   447: monitorexit    
        //   448: aload           21
        //   450: athrow         
        //   451: aload_1        
        //   452: ifnull          486
        //   455: iload           10
        //   457: ifeq            486
        //   460: aload_1        
        //   461: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.commit:()Lorg/apache/derby/iapi/store/raw/log/LogInstant;
        //   464: pop            
        //   465: aload_1        
        //   466: invokevirtual   org/apache/derby/iapi/store/raw/xact/RawTransaction.close:()V
        //   469: goto            486
        //   472: astore          20
        //   474: aload_0        
        //   475: ldc             "XSLA3.D"
        //   477: aload           20
        //   479: invokestatic    org/apache/derby/iapi/error/StandardException.newException:(Ljava/lang/String;Ljava/lang/Throwable;)Lorg/apache/derby/iapi/error/StandardException;
        //   482: invokevirtual   org/apache/derby/impl/store/raw/log/LogToFile.markCorrupt:(Lorg/apache/derby/iapi/error/StandardException;)Lorg/apache/derby/iapi/error/StandardException;
        //   485: athrow         
        //   486: ret             19
        //   488: iconst_1       
        //   489: ireturn        
        //    Exceptions:
        //  throws org.apache.derby.iapi.error.StandardException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                           
        //  -----  -----  -----  -----  -----------------------------------------------
        //  70     74     77     85     Ljava/lang/InterruptedException;
        //  17     55     94     102    Any
        //  58     91     94     102    Any
        //  94     99     94     102    Any
        //  182    236    239    247    Any
        //  239    244    239    247    Any
        //  125    393    399    413    Ljava/io/IOException;
        //  125    396    413    421    Any
        //  399    418    413    421    Any
        //  428    440    443    451    Any
        //  443    448    443    451    Any
        //  460    469    472    486    Lorg/apache/derby/iapi/error/StandardException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 3 out of bounds for length 3
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
        //     at com.strobel.assembler.Collection.get(Collection.java:43)
        //     at java.base/java.util.Collections$UnmodifiableList.get(Collections.java:1308)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCallCore(AstMethodBodyBuilder.java:1313)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCall(AstMethodBodyBuilder.java:1286)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1178)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:1009)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:494)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void flush(final LogInstant logInstant) throws StandardException {
        long logFileNumber;
        long logFilePosition;
        if (logInstant == null) {
            logFileNumber = 0L;
            logFilePosition = 0L;
        }
        else {
            final LogCounter logCounter = (LogCounter)logInstant;
            logFileNumber = logCounter.getLogFileNumber();
            logFilePosition = logCounter.getLogFilePosition();
        }
        this.flush(logFileNumber, logFilePosition);
    }
    
    public void flushAll() throws StandardException {
        final long logFileNumber;
        final long endPosition;
        synchronized (this) {
            logFileNumber = this.logFileNumber;
            endPosition = this.endPosition;
        }
        this.flush(logFileNumber, endPosition);
    }
    
    private boolean verifyLogFormat(final StorageFile storageFile, final long n) throws StandardException {
        boolean verifyLogFormat = false;
        try {
            final StorageRandomAccessFile privRandomAccessFile = this.privRandomAccessFile(storageFile, "r");
            verifyLogFormat = this.verifyLogFormat(privRandomAccessFile, n);
            privRandomAccessFile.close();
        }
        catch (IOException ex) {}
        return verifyLogFormat;
    }
    
    private boolean verifyLogFormat(final StorageRandomAccessFile storageRandomAccessFile, final long n) throws StandardException {
        try {
            storageRandomAccessFile.seek(0L);
            final int int1 = storageRandomAccessFile.readInt();
            storageRandomAccessFile.readInt();
            final long long1 = storageRandomAccessFile.readLong();
            if (int1 != LogToFile.fid || long1 != n) {
                throw StandardException.newException("XSLAC.D", this.dataDirectory);
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSLAM.D", ex, this.dataDirectory);
        }
        return true;
    }
    
    private boolean initLogFile(final StorageRandomAccessFile storageRandomAccessFile, final long n, final long n2) throws IOException, StandardException {
        if (storageRandomAccessFile.length() != 0L) {
            return false;
        }
        storageRandomAccessFile.seek(0L);
        storageRandomAccessFile.writeInt(LogToFile.fid);
        storageRandomAccessFile.writeInt(9);
        storageRandomAccessFile.writeLong(n);
        storageRandomAccessFile.writeLong(n2);
        this.syncFile(storageRandomAccessFile);
        return true;
    }
    
    public void switchLogFile() throws StandardException {
        boolean b = false;
        synchronized (this) {
            while (this.logBeingFlushed | this.isFrozen) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                }
            }
            if (this.endPosition == 24L) {
                return;
            }
            StorageFile logFileName = this.getLogFileName(this.logFileNumber + 1L);
            if (this.logFileNumber + 1L >= this.maxLogFileNumber) {
                throw StandardException.newException("XSLAK.D", new Long(this.maxLogFileNumber));
            }
            StorageRandomAccessFile storageRandomAccessFile = null;
            try {
                if (this.privExists(logFileName) && !this.privDelete(logFileName)) {
                    this.logErrMsg(MessageService.getTextMessage("L015", logFileName.getPath()));
                    return;
                }
                try {
                    storageRandomAccessFile = this.privRandomAccessFile(logFileName, "rw");
                }
                catch (IOException ex3) {
                    storageRandomAccessFile = null;
                }
                if (storageRandomAccessFile == null || !this.privCanWrite(logFileName)) {
                    if (storageRandomAccessFile != null) {
                        storageRandomAccessFile.close();
                    }
                    storageRandomAccessFile = null;
                    return;
                }
                if (this.initLogFile(storageRandomAccessFile, this.logFileNumber + 1L, LogCounter.makeLogInstantAsLong(this.logFileNumber, this.endPosition))) {
                    b = true;
                    this.logOut.writeEndMarker(0);
                    this.setEndPosition(this.endPosition + 4L);
                    this.inLogSwitch = true;
                    this.flush(this.logFileNumber, this.endPosition);
                    this.logOut.close();
                    this.logWrittenFromLastCheckPoint += this.endPosition;
                    this.setEndPosition(storageRandomAccessFile.getFilePointer());
                    this.lastFlush = this.endPosition;
                    if (this.isWriteSynced) {
                        this.preAllocateNewLogFile(storageRandomAccessFile);
                        storageRandomAccessFile.close();
                        storageRandomAccessFile = this.openLogFileInWriteMode(logFileName);
                        storageRandomAccessFile.seek(this.endPosition);
                    }
                    this.logOut = new LogAccessFile(this, storageRandomAccessFile, this.logBufferSize);
                    storageRandomAccessFile = null;
                    ++this.logFileNumber;
                }
                else {
                    storageRandomAccessFile.close();
                    storageRandomAccessFile = null;
                    if (this.privExists(logFileName)) {
                        this.privDelete(logFileName);
                    }
                    this.logErrMsg(MessageService.getTextMessage("L016", logFileName.getPath()));
                    logFileName = null;
                }
            }
            catch (IOException ex) {
                this.inLogSwitch = false;
                this.logErrMsg(MessageService.getTextMessage("L017", logFileName.getPath(), ex.toString()));
                try {
                    if (storageRandomAccessFile != null) {
                        storageRandomAccessFile.close();
                    }
                }
                catch (IOException ex4) {}
                if (logFileName != null && this.privExists(logFileName)) {
                    this.privDelete(logFileName);
                }
                if (b) {
                    this.logOut = null;
                    throw this.markCorrupt(StandardException.newException("XSLA2.D", ex));
                }
            }
            if (this.inReplicationSlaveMode) {
                this.allowedToReadFileNumber = this.logFileNumber - 1L;
                synchronized (this.slaveRecoveryMonitor) {
                    this.slaveRecoveryMonitor.notify();
                }
            }
            this.inLogSwitch = false;
        }
    }
    
    private void flushBuffer(final long n, final long n2) throws IOException, StandardException {
        synchronized (this) {
            if (n < this.logFileNumber) {
                return;
            }
            if (n2 < this.lastFlush) {
                return;
            }
            this.logOut.flushLogAccessFile();
        }
    }
    
    private void truncateLog(final CheckpointOperation checkpointOperation) {
        final long firstLogNeeded;
        if ((firstLogNeeded = this.getFirstLogNeeded(checkpointOperation)) == -1L) {
            return;
        }
        this.truncateLog(firstLogNeeded);
    }
    
    private void truncateLog(long firstLogFileNumber) {
        if (this.keepAllLogs) {
            return;
        }
        if (this.backupInProgress) {
            final long logFileToBackup = this.logFileToBackup;
            if (logFileToBackup < firstLogFileNumber) {
                firstLogFileNumber = logFileToBackup;
            }
        }
        long firstLogFileNumber2 = this.firstLogFileNumber;
        this.firstLogFileNumber = firstLogFileNumber;
        while (firstLogFileNumber2 < firstLogFileNumber) {
            try {
                if (this.privDelete(this.getLogFileName(firstLogFileNumber2))) {}
            }
            catch (StandardException ex) {}
            ++firstLogFileNumber2;
        }
    }
    
    private long getFirstLogNeeded(final CheckpointOperation checkpointOperation) {
        final long n;
        synchronized (this) {
            n = ((checkpointOperation != null) ? LogCounter.getLogFileNumber(checkpointOperation.undoLWM()) : -1L);
        }
        return n;
    }
    
    boolean writeControlFile(final StorageFile storageFile, final long v) throws IOException, StandardException {
        StorageRandomAccessFile storageRandomAccessFile = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeInt(LogToFile.fid);
        dataOutputStream.writeInt(9);
        dataOutputStream.writeLong(v);
        if (this.onDiskMajorVersion == 0) {
            this.onDiskMajorVersion = this.jbmsVersion.getMajorVersion();
            this.onDiskMinorVersion = this.jbmsVersion.getMinorVersion();
            this.onDiskBeta = this.jbmsVersion.isBeta();
        }
        dataOutputStream.writeInt(this.onDiskMajorVersion);
        dataOutputStream.writeInt(this.onDiskMinorVersion);
        dataOutputStream.writeInt(this.jbmsVersion.getBuildNumberAsInt());
        int v2 = 0;
        if (this.onDiskBeta) {
            v2 = (byte)(v2 | 0x1);
        }
        if (this.logNotSynced || LogToFile.wasDBInDurabilityTestModeNoSync) {
            v2 = (byte)(v2 | 0x2);
        }
        dataOutputStream.writeByte(v2);
        final long v3 = 0L;
        dataOutputStream.writeByte(0);
        dataOutputStream.writeByte(0);
        dataOutputStream.writeByte(0);
        dataOutputStream.writeLong(v3);
        dataOutputStream.flush();
        this.checksum.reset();
        this.checksum.update(out.toByteArray(), 0, out.size());
        dataOutputStream.writeLong(this.checksum.getValue());
        dataOutputStream.flush();
        try {
            this.checkCorrupt();
            try {
                storageRandomAccessFile = this.privRandomAccessFile(storageFile, "rw");
            }
            catch (IOException ex) {
                storageRandomAccessFile = null;
                return false;
            }
            if (!this.privCanWrite(storageFile)) {
                return false;
            }
            storageRandomAccessFile.seek(0L);
            storageRandomAccessFile.write(out.toByteArray());
            this.syncFile(storageRandomAccessFile);
            storageRandomAccessFile.close();
            try {
                storageRandomAccessFile = this.privRandomAccessFile(this.getMirrorControlFileName(), "rw");
            }
            catch (IOException ex2) {
                storageRandomAccessFile = null;
                return false;
            }
            storageRandomAccessFile.seek(0L);
            storageRandomAccessFile.write(out.toByteArray());
            this.syncFile(storageRandomAccessFile);
        }
        finally {
            if (storageRandomAccessFile != null) {
                storageRandomAccessFile.close();
            }
        }
        return true;
    }
    
    private long readControlFile(final StorageFile storageFile, final Properties properties) throws IOException, StandardException {
        ByteArrayInputStream in = null;
        DataInputStream dataInputStream = null;
        final StorageRandomAccessFile privRandomAccessFile = this.privRandomAccessFile(storageFile, "r");
        boolean b = false;
        long long1 = 0L;
        long long2 = 0L;
        final long length = privRandomAccessFile.length();
        byte[] array = null;
        try {
            if (length < 16L) {
                long2 = -1L;
            }
            else if (length == 16L) {
                array = new byte[16];
                privRandomAccessFile.readFully(array);
            }
            else if (length > 16L) {
                array = new byte[(int)privRandomAccessFile.length() - 8];
                privRandomAccessFile.readFully(array);
                long2 = privRandomAccessFile.readLong();
                if (long2 != 0L) {
                    this.checksum.reset();
                    this.checksum.update(array, 0, array.length);
                }
            }
            if (long2 == this.checksum.getValue() || long2 == 0L) {
                in = new ByteArrayInputStream(array);
                dataInputStream = new DataInputStream(in);
                if (dataInputStream.readInt() != LogToFile.fid) {
                    throw StandardException.newException("XSLAC.D", this.dataDirectory);
                }
                dataInputStream.readInt();
                long1 = dataInputStream.readLong();
                this.onDiskMajorVersion = dataInputStream.readInt();
                this.onDiskMinorVersion = dataInputStream.readInt();
                dataInputStream.readInt();
                final byte byte1 = dataInputStream.readByte();
                LogToFile.wasDBInDurabilityTestModeNoSync = ((byte1 & 0x2) != 0x0);
                this.onDiskBeta = ((byte1 & 0x1) != 0x0);
                if (this.onDiskBeta && (!this.jbmsVersion.isBeta() || this.onDiskMajorVersion != this.jbmsVersion.getMajorVersion() || this.onDiskMinorVersion != this.jbmsVersion.getMinorVersion()) && !false) {
                    throw StandardException.newException("XSLAP.D", this.dataDirectory, ProductVersionHolder.simpleVersionString(this.onDiskMajorVersion, this.onDiskMinorVersion, this.onDiskBeta));
                }
                if (this.onDiskMajorVersion > this.jbmsVersion.getMajorVersion() || (this.onDiskMajorVersion == this.jbmsVersion.getMajorVersion() && this.onDiskMinorVersion > this.jbmsVersion.getMinorVersion())) {
                    throw StandardException.newException("XSLAN.D", this.dataDirectory, ProductVersionHolder.simpleVersionString(this.onDiskMajorVersion, this.onDiskMinorVersion, this.onDiskBeta));
                }
                if (this.onDiskMajorVersion != this.jbmsVersion.getMajorVersion() || this.onDiskMinorVersion != this.jbmsVersion.getMinorVersion()) {
                    b = true;
                }
                if (long2 == 0L && (this.onDiskMajorVersion > 3 || this.onDiskMinorVersion > 5 || this.onDiskMajorVersion == 0)) {
                    long1 = 0L;
                }
            }
        }
        finally {
            if (privRandomAccessFile != null) {
                privRandomAccessFile.close();
            }
            if (in != null) {
                in.close();
            }
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
        if (b && Monitor.isFullUpgrade(properties, ProductVersionHolder.simpleVersionString(this.onDiskMajorVersion, this.onDiskMinorVersion, this.onDiskBeta))) {
            this.onDiskMajorVersion = this.jbmsVersion.getMajorVersion();
            this.onDiskMinorVersion = this.jbmsVersion.getMinorVersion();
            this.onDiskBeta = this.jbmsVersion.isBeta();
            if (!this.writeControlFile(storageFile, long1)) {
                throw StandardException.newException("XSLAE.D", storageFile);
            }
        }
        return long1;
    }
    
    private void createLogDirectory() throws StandardException {
        final StorageFile storageFile = this.logStorageFactory.newStorageFile("log");
        if (this.privExists(storageFile)) {
            final String[] privList = this.privList(storageFile);
            if (privList != null && privList.length != 0) {
                throw StandardException.newException("XSLAT.D", storageFile.getPath());
            }
        }
        else {
            if (!this.privMkdirs(storageFile)) {
                throw StandardException.newException("XSLAQ.D", storageFile.getPath());
            }
            this.createDataWarningFile();
        }
    }
    
    public void createDataWarningFile() throws StandardException {
        final StorageFile storageFile = this.logStorageFactory.newStorageFile("log", "README_DO_NOT_TOUCH_FILES.txt");
        if (!this.privExists(storageFile)) {
            Writer privGetOutputStreamWriter = null;
            try {
                privGetOutputStreamWriter = this.privGetOutputStreamWriter(storageFile);
                privGetOutputStreamWriter.write(MessageService.getTextMessage("M006"));
            }
            catch (IOException ex) {}
            finally {
                if (privGetOutputStreamWriter != null) {
                    try {
                        ((OutputStreamWriter)privGetOutputStreamWriter).close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
    }
    
    public StorageFile getLogDirectory() throws StandardException {
        final StorageFile storageFile = this.logStorageFactory.newStorageFile("log");
        if (!this.privExists(storageFile)) {
            throw StandardException.newException("XSLAQ.D", storageFile.getPath());
        }
        return storageFile;
    }
    
    public String getCanonicalLogPath() {
        if (this.logDevice == null) {
            return null;
        }
        try {
            return this.logStorageFactory.getCanonicalName();
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    private StorageFile getControlFileName() throws StandardException {
        return this.logStorageFactory.newStorageFile(this.getLogDirectory(), "log.ctrl");
    }
    
    private StorageFile getMirrorControlFileName() throws StandardException {
        return this.logStorageFactory.newStorageFile(this.getLogDirectory(), "logmirror.ctrl");
    }
    
    private StorageFile getLogFileName(final long lng) throws StandardException {
        return this.logStorageFactory.newStorageFile(this.getLogDirectory(), "log" + lng + ".dat");
    }
    
    private CheckpointOperation findCheckpoint(final long n, final FileLogger fileLogger) throws IOException, StandardException, ClassNotFoundException {
        final StreamLogScan streamLogScan = (StreamLogScan)this.openForwardsScan(n, null);
        final Loggable logRecord = fileLogger.readLogRecord(streamLogScan, 100);
        streamLogScan.close();
        if (logRecord instanceof CheckpointOperation) {
            return (CheckpointOperation)logRecord;
        }
        return null;
    }
    
    protected LogScan openBackwardsScan(final long n, final LogInstant logInstant) throws IOException, StandardException {
        this.checkCorrupt();
        if (n == 0L) {
            return this.openBackwardsScan(logInstant);
        }
        this.flushBuffer(LogCounter.getLogFileNumber(n), LogCounter.getLogFilePosition(n));
        return new Scan(this, n, logInstant, (byte)2);
    }
    
    protected LogScan openBackwardsScan(final LogInstant logInstant) throws IOException, StandardException {
        this.checkCorrupt();
        final long currentInstant;
        synchronized (this) {
            this.logOut.flushLogAccessFile();
            currentInstant = this.currentInstant();
        }
        return new Scan(this, currentInstant, logInstant, (byte)4);
    }
    
    public ScanHandle openFlushedScan(final DatabaseInstant databaseInstant, final int n) throws StandardException {
        return new FlushedScanHandle(this, databaseInstant, n);
    }
    
    protected LogScan openForwardsScan(long firstLogInstant, final LogInstant logInstant) throws IOException, StandardException {
        this.checkCorrupt();
        if (firstLogInstant == 0L) {
            firstLogInstant = this.firstLogInstant();
        }
        if (logInstant != null) {
            final LogCounter logCounter = (LogCounter)logInstant;
            this.flushBuffer(logCounter.getLogFileNumber(), logCounter.getLogFilePosition());
        }
        else {
            synchronized (this) {
                if (this.logOut != null) {
                    this.logOut.flushLogAccessFile();
                }
            }
        }
        return new Scan(this, firstLogInstant, logInstant, (byte)1);
    }
    
    protected StorageRandomAccessFile getLogFileAtBeginning(final long n) throws IOException, StandardException {
        if (this.inReplicationSlaveMode && this.allowedToReadFileNumber != -1L) {
            synchronized (this.slaveRecoveryMonitor) {
                while (this.inReplicationSlaveMode && n > this.allowedToReadFileNumber) {
                    if (this.replicationSlaveException != null) {
                        throw this.replicationSlaveException;
                    }
                    try {
                        this.slaveRecoveryMonitor.wait();
                    }
                    catch (InterruptedException ex) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
        }
        return this.getLogFileAtPosition(LogCounter.makeLogInstantAsLong(n, 24L));
    }
    
    protected StorageRandomAccessFile getLogFileAtPosition(final long n) throws IOException, StandardException {
        this.checkCorrupt();
        final long logFileNumber = LogCounter.getLogFileNumber(n);
        final long logFilePosition = LogCounter.getLogFilePosition(n);
        final StorageFile logFileName = this.getLogFileName(logFileNumber);
        if (!this.privExists(logFileName)) {
            return null;
        }
        StorageRandomAccessFile privRandomAccessFile = null;
        try {
            privRandomAccessFile = this.privRandomAccessFile(logFileName, "r");
            if (!this.verifyLogFormat(privRandomAccessFile, logFileNumber)) {
                privRandomAccessFile.close();
                privRandomAccessFile = null;
            }
            else {
                privRandomAccessFile.seek(logFilePosition);
            }
        }
        catch (IOException ex) {
            try {
                if (privRandomAccessFile != null) {
                    privRandomAccessFile.close();
                }
            }
            catch (IOException ex2) {}
            throw ex;
        }
        return privRandomAccessFile;
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.__rt.storage.log");
        return property == null || !property.equals("readonly");
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final String property = properties.getProperty("replication.slave.mode");
        if (property != null && property.equals("slavemode")) {
            this.inReplicationSlaveMode = true;
            this.slaveRecoveryMonitor = new Object();
        }
        else if (property != null && property.equals("slavepremode")) {
            this.inReplicationSlavePreMode = true;
        }
        this.dataDirectory = properties.getProperty("derby.__rt.serviceDirectory");
        this.logDevice = properties.getProperty("logDevice");
        if (this.logDevice != null) {
            String file = null;
            try {
                file = new URL(this.logDevice).getFile();
            }
            catch (MalformedURLException ex2) {}
            if (file != null) {
                this.logDevice = file;
            }
        }
        if (b) {
            this.getLogStorageFactory();
            this.createLogDirectory();
        }
        else if (!this.restoreLogs(properties)) {
            this.getLogStorageFactory();
            if (this.logDevice != null) {
                final StorageFile storageFile = this.logStorageFactory.newStorageFile("log");
                if (!this.privExists(storageFile)) {
                    throw StandardException.newException("XSLAB.D", storageFile.getPath());
                }
            }
        }
        this.logBufferSize = PropertyUtil.getSystemInt("derby.storage.logBufferSize", 8192, 134217728, 32768);
        this.jbmsVersion = Monitor.getMonitor().getEngineVersion();
        this.logArchived = Boolean.valueOf(properties.getProperty("derby.storage.logArchiveMode"));
        this.getLogFactoryProperties(null);
        if (this.logStorageFactory.supportsWriteSync()) {
            this.isWriteSynced = !PropertyUtil.getSystemBoolean("derby.storage.fileSyncTransactionLog");
        }
        else {
            this.isWriteSynced = false;
        }
        if ("test".equalsIgnoreCase(PropertyUtil.getSystemProperty("derby.system.durability"))) {
            this.logNotSynced = true;
            this.isWriteSynced = false;
        }
        int n = b ? 1 : 0;
        this.checkpointInstant = 0L;
        try {
            final StorageFile controlFileName = this.getControlFileName();
            if (n == 0) {
                if (this.privExists(controlFileName)) {
                    this.checkpointInstant = this.readControlFile(controlFileName, properties);
                    if (LogToFile.wasDBInDurabilityTestModeNoSync) {
                        Monitor.logMessage(MessageService.getTextMessage("L020", "derby.system.durability", "test"));
                    }
                    if (this.checkpointInstant == 0L && this.privExists(this.getMirrorControlFileName())) {
                        this.checkpointInstant = this.readControlFile(this.getMirrorControlFileName(), properties);
                    }
                }
                else if (this.logDevice != null) {
                    throw StandardException.newException("XSLAB.D", controlFileName.getPath());
                }
                if (this.checkpointInstant != 0L) {
                    this.logFileNumber = LogCounter.getLogFileNumber(this.checkpointInstant);
                }
                else {
                    this.logFileNumber = 1L;
                }
                final StorageFile logFileName = this.getLogFileName(this.logFileNumber);
                if (!this.privExists(logFileName)) {
                    if (this.logDevice != null) {
                        throw StandardException.newException("XSLAB.D", controlFileName.getPath());
                    }
                    this.logErrMsg(MessageService.getTextMessage("L018", logFileName.getPath()));
                    n = 1;
                }
                else if (!this.verifyLogFormat(logFileName, this.logFileNumber)) {
                    Monitor.logTextMessage("L008", logFileName);
                    if (!this.privDelete(logFileName) && this.logFileNumber == 1L) {
                        this.logErrMsgForDurabilityTestModeNoSync();
                        throw StandardException.newException("XSLAC.D", this.dataDirectory);
                    }
                    n = 1;
                }
            }
            if (n != 0) {
                if (this.writeControlFile(controlFileName, 0L)) {
                    this.firstLogFileNumber = 1L;
                    this.logFileNumber = 1L;
                    final StorageFile logFileName2 = this.getLogFileName(this.logFileNumber);
                    if (this.privExists(logFileName2)) {
                        Monitor.logTextMessage("L009", logFileName2);
                        if (!this.privDelete(logFileName2)) {
                            this.logErrMsgForDurabilityTestModeNoSync();
                            throw StandardException.newException("XSLAC.D", this.dataDirectory);
                        }
                    }
                    this.firstLog = this.privRandomAccessFile(logFileName2, "rw");
                    if (!this.initLogFile(this.firstLog, this.logFileNumber, 0L)) {
                        throw StandardException.newException("XSLAQ.D", logFileName2.getPath());
                    }
                    this.setEndPosition(this.firstLog.getFilePointer());
                    this.lastFlush = this.firstLog.getFilePointer();
                    if (this.isWriteSynced) {
                        this.preAllocateNewLogFile(this.firstLog);
                        this.firstLog.close();
                        (this.firstLog = this.openLogFileInWriteMode(logFileName2)).seek(this.endPosition);
                    }
                }
                else {
                    Monitor.logTextMessage("L022");
                    Monitor.logThrowable(new Exception("Error writing control file"));
                    this.ReadOnlyDB = true;
                    this.logOut = null;
                    this.firstLog = null;
                }
                this.recoveryNeeded = false;
            }
            else {
                this.recoveryNeeded = true;
            }
        }
        catch (IOException ex) {
            throw Monitor.exceptionStartingModule(ex);
        }
        if (!this.checkVersion(10, 1)) {
            this.maxLogFileNumber = 4194303L;
        }
        this.bootTimeLogFileNumber = this.logFileNumber;
    }
    
    private void getLogStorageFactory() throws StandardException {
        if (this.logDevice == null) {
            this.logStorageFactory = (WritableStorageFactory)((DataFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.store.raw.data.DataFactory")).getStorageFactory();
        }
        else {
            try {
                this.logStorageFactory = (WritableStorageFactory)Monitor.getMonitor().getServiceType(this).getStorageFactoryInstance(false, this.logDevice, null, null);
            }
            catch (IOException ex) {
                throw StandardException.newException("XSLAB.D", ex, this.logDevice);
            }
        }
    }
    
    public void stop() {
        if (this.checkpointDaemon != null) {
            this.checkpointDaemon.unsubscribe(this.myClientNumber);
            this.checkpointDaemon.stop();
        }
        synchronized (this) {
            this.stopped = true;
            if (this.logOut != null) {
                try {
                    this.logOut.flushLogAccessFile();
                    this.logOut.close();
                }
                catch (IOException ex) {}
                catch (StandardException ex2) {}
                this.logOut = null;
            }
        }
        if (this.corrupt == null && !this.logArchived() && !this.keepAllLogs && !this.ReadOnlyDB) {
            this.deleteObsoleteLogfiles();
        }
        if (this.logDevice != null) {
            this.logStorageFactory.shutdown();
        }
        this.logStorageFactory = null;
    }
    
    private void deleteObsoleteLogfiles() {
        long firstLogNeeded = this.getFirstLogNeeded(this.currentCheckpoint);
        if (firstLogNeeded == -1L) {
            return;
        }
        if (this.backupInProgress) {
            final long logFileToBackup = this.logFileToBackup;
            if (logFileToBackup < firstLogNeeded) {
                firstLogNeeded = logFileToBackup;
            }
        }
        StorageFile logDirectory;
        try {
            logDirectory = this.getLogDirectory();
        }
        catch (StandardException ex) {
            return;
        }
        final String[] privList = this.privList(logDirectory);
        if (privList != null) {
            for (int i = 0; i < privList.length; ++i) {
                if (!privList[i].startsWith("log") || !privList[i].endsWith(".dat") || Long.parseLong(privList[i].substring(3, privList[i].length() - 4)) >= firstLogNeeded || this.privDelete(this.logStorageFactory.newStorageFile(logDirectory, privList[i]))) {}
            }
        }
    }
    
    public boolean serviceASAP() {
        return false;
    }
    
    public boolean serviceImmediately() {
        return false;
    }
    
    public void getLogFactoryProperties(final PersistentSet set) throws StandardException {
        String s;
        String s2;
        if (set == null) {
            s = PropertyUtil.getSystemProperty("derby.storage.logSwitchInterval");
            s2 = PropertyUtil.getSystemProperty("derby.storage.checkpointInterval");
        }
        else {
            s = PropertyUtil.getServiceProperty(set, "derby.storage.logSwitchInterval");
            s2 = PropertyUtil.getServiceProperty(set, "derby.storage.checkpointInterval");
        }
        if (s != null) {
            this.logSwitchInterval = Integer.parseInt(s);
            if (this.logSwitchInterval < 100000) {
                this.logSwitchInterval = 100000;
            }
            else if (this.logSwitchInterval > 134217728) {
                this.logSwitchInterval = 134217728;
            }
        }
        if (s2 != null) {
            this.checkpointInterval = Integer.parseInt(s2);
            if (this.checkpointInterval < 100000) {
                this.checkpointInterval = 100000;
            }
            else if (this.checkpointInterval > 134217728) {
                this.checkpointInterval = 134217728;
            }
        }
    }
    
    public int performWork(final ContextManager contextManager) {
        synchronized (this) {
            if (this.corrupt != null) {
                return 1;
            }
        }
        final AccessFactory accessFactory = (AccessFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory");
        try {
            if (accessFactory != null) {
                PersistentSet andNameTransaction = null;
                try {
                    andNameTransaction = accessFactory.getAndNameTransaction(contextManager, "SystemTransaction");
                    this.getLogFactoryProperties(andNameTransaction);
                }
                finally {
                    if (andNameTransaction != null) {
                        ((TransactionController)andNameTransaction).commit();
                    }
                }
            }
            this.rawStoreFactory.checkpoint();
        }
        catch (StandardException ex) {
            Monitor.logTextMessage("L011");
            this.logErrMsg(ex);
        }
        catch (ShutdownException ex2) {}
        this.checkpointDaemonCalled = false;
        return 1;
    }
    
    public long appendLogRecord(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) throws StandardException {
        if (this.inReplicationSlavePreMode) {
            return LogCounter.makeLogInstantAsLong(this.logFileNumber, this.endPosition);
        }
        if (this.ReadOnlyDB) {
            throw StandardException.newException("XSLAH.D");
        }
        if (n2 <= 0) {
            throw StandardException.newException("XSLB6.S");
        }
        long logInstantAsLong;
        try {
            synchronized (this) {
                if (this.corrupt != null) {
                    throw StandardException.newException("XSLAA.D", this.corrupt);
                }
                if (this.logOut == null) {
                    throw StandardException.newException("XSLAJ.D");
                }
                final int checksumLogRecordSize = this.logOut.getChecksumLogRecordSize();
                if (this.endPosition + 16L + n2 + 4L + checksumLogRecordSize >= 268435455L) {
                    this.switchLogFile();
                    if (this.endPosition + 16L + n2 + 4L + checksumLogRecordSize >= 268435455L) {
                        throw StandardException.newException("XSLAL.D", new Long(this.logFileNumber), new Long(this.endPosition), new Long(n2), new Long(268435455L));
                    }
                }
                this.setEndPosition(this.endPosition + this.logOut.reserveSpaceForChecksum(n2, this.logFileNumber, this.endPosition));
                logInstantAsLong = LogCounter.makeLogInstantAsLong(this.logFileNumber, this.endPosition);
                this.logOut.writeLogRecord(n2, logInstantAsLong, array, n, array2, n3, n4);
                if (n4 != 0) {}
                this.setEndPosition(this.endPosition + (n2 + 16));
            }
        }
        catch (IOException ex) {
            throw this.markCorrupt(StandardException.newException("XSLA4.D", ex));
        }
        return logInstantAsLong;
    }
    
    protected synchronized long currentInstant() {
        return LogCounter.makeLogInstantAsLong(this.logFileNumber, this.endPosition);
    }
    
    protected synchronized long endPosition() {
        return this.endPosition;
    }
    
    private synchronized long getLogFileNumber() {
        return this.logFileNumber;
    }
    
    private synchronized long firstLogInstant() {
        return LogCounter.makeLogInstantAsLong(this.firstLogFileNumber, 24L);
    }
    
    protected void flush(final long n, final long n2) throws StandardException {
        long endPosition = 0L;
        synchronized (this) {
            Label_0220: {
                try {
                    while (this.corrupt == null) {
                        while (this.isFrozen) {
                            try {
                                this.wait();
                            }
                            catch (InterruptedException ex4) {
                                InterruptStatus.setInterrupted();
                            }
                        }
                        if (n2 == 0L || n < this.logFileNumber || n2 < this.lastFlush) {
                            return;
                        }
                        if (this.recoveryNeeded && this.inRedo && !this.inReplicationSlaveMode) {
                            return;
                        }
                        boolean b;
                        if (this.logBeingFlushed) {
                            b = true;
                            try {
                                this.wait();
                            }
                            catch (InterruptedException ex5) {
                                InterruptStatus.setInterrupted();
                            }
                        }
                        else {
                            b = false;
                            if (!this.isWriteSynced) {
                                this.logOut.flushLogAccessFile();
                            }
                            else {
                                this.logOut.switchLogBuffer();
                            }
                            endPosition = this.endPosition;
                            this.logBeingFlushed = true;
                            if (this.inReplicationMasterMode) {
                                this.masterFactory.flushedTo(LogCounter.makeLogInstantAsLong(n, n2));
                            }
                        }
                        if (!b) {
                            break Label_0220;
                        }
                    }
                    throw StandardException.newException("XSLAA.D", this.corrupt);
                }
                catch (IOException ex) {
                    throw this.markCorrupt(StandardException.newException("XSLA0.D", ex, this.getLogFileName(this.logFileNumber).getPath()));
                }
            }
        }
        boolean b2 = false;
        try {
            if (this.isWriteSynced) {
                this.logOut.flushDirtyBuffers();
            }
            else if (!this.logNotSynced) {
                this.logOut.syncLogAccessFile();
            }
            b2 = true;
        }
        catch (SyncFailedException ex2) {
            throw this.markCorrupt(StandardException.newException("XSLA0.D", ex2, this.getLogFileName(this.logFileNumber).getPath()));
        }
        catch (IOException ex3) {
            throw this.markCorrupt(StandardException.newException("XSLA0.D", ex3, this.getLogFileName(this.logFileNumber).getPath()));
        }
        finally {
            synchronized (this) {
                this.logBeingFlushed = false;
                if (b2) {
                    this.lastFlush = endPosition;
                }
                this.notifyAll();
            }
        }
        if (this.logWrittenFromLastCheckPoint + endPosition > this.checkpointInterval && this.checkpointDaemon != null && !this.checkpointDaemonCalled && !this.inLogSwitch) {
            synchronized (this) {
                if (this.logWrittenFromLastCheckPoint + endPosition > this.checkpointInterval && this.checkpointDaemon != null && !this.checkpointDaemonCalled && !this.inLogSwitch) {
                    this.checkpointDaemonCalled = true;
                    this.checkpointDaemon.serviceNow(this.myClientNumber);
                }
            }
        }
        else if (endPosition > this.logSwitchInterval && !this.checkpointDaemonCalled && !this.inLogSwitch) {
            synchronized (this) {
                if (endPosition > this.logSwitchInterval && !this.checkpointDaemonCalled && !this.inLogSwitch) {
                    this.inLogSwitch = true;
                    this.switchLogFile();
                }
            }
        }
    }
    
    private void syncFile(final StorageRandomAccessFile storageRandomAccessFile) throws StandardException {
        int n = 0;
        while (true) {
            try {
                storageRandomAccessFile.sync();
            }
            catch (IOException ex) {
                ++n;
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                }
                if (n > 20) {
                    throw StandardException.newException("XSLA4.D", ex);
                }
                continue;
            }
            break;
        }
    }
    
    public LogScan openForwardsFlushedScan(final LogInstant logInstant) throws StandardException {
        this.checkCorrupt();
        return new FlushedScan(this, ((LogCounter)logInstant).getValueAsLong());
    }
    
    public LogScan openForwardsScan(final LogInstant logInstant, final LogInstant logInstant2) throws StandardException {
        try {
            long valueAsLong;
            if (logInstant == null) {
                valueAsLong = 0L;
            }
            else {
                valueAsLong = ((LogCounter)logInstant).getValueAsLong();
            }
            return this.openForwardsScan(valueAsLong, logInstant2);
        }
        catch (IOException ex) {
            throw this.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
    }
    
    public final boolean databaseEncrypted() {
        return this.databaseEncrypted;
    }
    
    public void setDatabaseEncrypted(final boolean databaseEncrypted, final boolean b) throws StandardException {
        if (b) {
            this.flushAll();
        }
        this.databaseEncrypted = databaseEncrypted;
    }
    
    public void startNewLogFile() throws StandardException {
        this.switchLogFile();
    }
    
    public boolean isCheckpointInLastLogFile() throws StandardException {
        return !this.privExists(this.getLogFileName(LogCounter.getLogFileNumber(this.checkpointInstant) + 1L));
    }
    
    public void deleteLogFileAfterCheckpointLogFile() throws StandardException {
        final StorageFile logFileName = this.getLogFileName(LogCounter.getLogFileNumber(this.checkpointInstant) + 1L);
        if (this.privExists(logFileName) && !this.privDelete(logFileName)) {
            throw StandardException.newException("XBM0R.D", logFileName);
        }
    }
    
    public int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws StandardException {
        return this.rawStoreFactory.encrypt(array, n, n2, array2, n3, false);
    }
    
    public int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws StandardException {
        return this.rawStoreFactory.decrypt(array, n, n2, array2, n3);
    }
    
    public int getEncryptionBlockSize() {
        return this.rawStoreFactory.getEncryptionBlockSize();
    }
    
    public int getEncryptedDataLength(final int n) {
        if (n % this.getEncryptionBlockSize() != 0) {
            return n + this.getEncryptionBlockSize() - n % this.getEncryptionBlockSize();
        }
        return n;
    }
    
    public synchronized LogInstant getFirstUnflushedInstant() {
        return new LogCounter(this.logFileNumber, this.lastFlush);
    }
    
    public synchronized long getFirstUnflushedInstantAsLong() {
        return LogCounter.makeLogInstantAsLong(this.logFileNumber, this.lastFlush);
    }
    
    public void freezePersistentStore() throws StandardException {
        synchronized (this) {
            this.isFrozen = true;
        }
    }
    
    public void unfreezePersistentStore() throws StandardException {
        synchronized (this) {
            this.isFrozen = false;
            this.notifyAll();
        }
    }
    
    public boolean logArchived() {
        return this.logArchived;
    }
    
    boolean checkVersion(final int n, final int n2) {
        return this.onDiskMajorVersion > n || (this.onDiskMajorVersion == n && this.onDiskMinorVersion >= n2);
    }
    
    public boolean checkVersion(final int n, final int n2, final String s) throws StandardException {
        final boolean checkVersion = this.checkVersion(n, n2);
        if (!checkVersion && s != null) {
            throw StandardException.newException("XCL47.S", s, ProductVersionHolder.simpleVersionString(this.onDiskMajorVersion, this.onDiskMinorVersion, this.onDiskBeta), ProductVersionHolder.simpleVersionString(n, n2, false));
        }
        return checkVersion;
    }
    
    protected void logErrMsg(final String s) {
        this.logErrMsgForDurabilityTestModeNoSync();
        Monitor.logTextMessage("L001");
        Monitor.logMessage(s);
        Monitor.logTextMessage("L002");
    }
    
    protected void logErrMsg(final Throwable t) {
        this.logErrMsgForDurabilityTestModeNoSync();
        if (this.corrupt != null) {
            Monitor.logTextMessage("L003");
            this.printErrorStack(this.corrupt);
            Monitor.logTextMessage("L004");
        }
        if (t != this.corrupt) {
            Monitor.logTextMessage("L005");
            this.printErrorStack(t);
            Monitor.logTextMessage("L006");
        }
    }
    
    private void logErrMsgForDurabilityTestModeNoSync() {
        if (this.logNotSynced || LogToFile.wasDBInDurabilityTestModeNoSync) {
            Monitor.logTextMessage("L021", "derby.system.durability", "test");
        }
    }
    
    private void printErrorStack(final Throwable t) {
        final ErrorStringBuilder errorStringBuilder = new ErrorStringBuilder(Monitor.getStream().getHeader());
        errorStringBuilder.stackTrace(t);
        Monitor.logMessage(errorStringBuilder.get().toString());
        errorStringBuilder.reset();
    }
    
    private long logtest_appendPartialLogRecord(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) throws StandardException {
        return 0L;
    }
    
    protected void testLogFull() throws IOException {
    }
    
    public StorageRandomAccessFile getLogFileToSimulateCorruption(final long n) throws IOException, StandardException {
        return null;
    }
    
    public boolean inReplicationMasterMode() {
        return this.inReplicationMasterMode;
    }
    
    public void enableLogArchiveMode() throws StandardException {
        if (!this.logArchived) {
            this.logArchived = true;
            final AccessFactory accessFactory = (AccessFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory");
            if (accessFactory != null) {
                accessFactory.getTransaction(ContextService.getFactory().getCurrentContextManager()).setProperty("derby.storage.logArchiveMode", "true", true);
            }
        }
    }
    
    public void disableLogArchiveMode() throws StandardException {
        final AccessFactory accessFactory = (AccessFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory");
        if (accessFactory != null) {
            accessFactory.getTransaction(ContextService.getFactory().getCurrentContextManager()).setProperty("derby.storage.logArchiveMode", "false", true);
        }
        this.logArchived = false;
    }
    
    public void deleteOnlineArchivedLogFiles() {
        this.deleteObsoleteLogfiles();
    }
    
    public void startLogBackup(final File file) throws StandardException {
        synchronized (this) {
            while (this.inCheckpoint) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
            this.backupInProgress = true;
            final StorageFile controlFileName = this.getControlFileName();
            final File file2 = new File(file, controlFileName.getName());
            if (!this.privCopyFile(controlFileName, file2)) {
                throw StandardException.newException("XSRS5.S", controlFileName, file2);
            }
            final StorageFile mirrorControlFileName = this.getMirrorControlFileName();
            final File file3 = new File(file, mirrorControlFileName.getName());
            if (!this.privCopyFile(mirrorControlFileName, file3)) {
                throw StandardException.newException("XSRS5.S", mirrorControlFileName, file3);
            }
            this.logFileToBackup = this.getFirstLogNeeded(this.currentCheckpoint);
        }
        this.backupLogFiles(file, this.getLogFileNumber() - 1L);
    }
    
    private void backupLogFiles(final File parent, final long n) throws StandardException {
        while (this.logFileToBackup <= n) {
            final StorageFile logFileName = this.getLogFileName(this.logFileToBackup);
            final File file = new File(parent, logFileName.getName());
            if (!this.privCopyFile(logFileName, file)) {
                throw StandardException.newException("XSRS5.S", logFileName, file);
            }
            ++this.logFileToBackup;
        }
    }
    
    public void endLogBackup(final File file) throws StandardException {
        this.flush(this.logFileNumber, this.endPosition);
        long logFileNumber;
        if (this.logArchived) {
            this.switchLogFile();
            logFileNumber = this.getLogFileNumber() - 1L;
        }
        else {
            logFileNumber = this.getLogFileNumber();
        }
        this.backupLogFiles(file, logFileNumber);
        this.backupInProgress = false;
    }
    
    public void abortLogBackup() {
        this.backupInProgress = false;
    }
    
    public boolean inRFR() {
        if (this.recoveryNeeded) {
            int n = 0;
            try {
                n = (this.privCanWrite(this.getControlFileName()) ? 0 : 1);
            }
            catch (StandardException ex) {}
            Label_0056: {
                if (n == 0) {
                    if (this.dataFactory != null) {
                        if (this.dataFactory.isReadOnly()) {
                            break Label_0056;
                        }
                    }
                    final boolean b = false;
                    return !b;
                }
            }
            final boolean b = true;
            return !b;
        }
        return false;
    }
    
    public void checkpointInRFR(final LogInstant logInstant, final long n, final long n2, final DataFactory dataFactory) throws StandardException {
        dataFactory.checkpoint();
        try {
            if (!this.writeControlFile(this.getControlFileName(), ((LogCounter)logInstant).getValueAsLong())) {
                throw StandardException.newException("XSLAE.D", this.getControlFileName());
            }
        }
        catch (IOException ex) {
            throw this.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
        dataFactory.removeDroppedContainerFileStubs(new LogCounter(n));
        if (this.inReplicationSlaveMode) {
            this.truncateLog(LogCounter.getLogFileNumber(n2));
        }
    }
    
    public void startReplicationMasterRole(final MasterFactory masterFactory) throws StandardException {
        this.masterFactory = masterFactory;
        synchronized (this) {
            this.inReplicationMasterMode = true;
            this.logOut.setReplicationMasterRole(masterFactory);
        }
    }
    
    public void stopReplicationMasterRole() {
        this.inReplicationMasterMode = false;
        this.masterFactory = null;
        if (this.logOut != null) {
            this.logOut.stopReplicationMasterRole();
        }
    }
    
    public void stopReplicationSlaveRole() throws StandardException {
        if (!this.stopped) {
            this.flushAll();
        }
        this.replicationSlaveException = StandardException.newException("08006.D");
        synchronized (this.slaveRecoveryMonitor) {
            this.slaveRecoveryMonitor.notify();
        }
    }
    
    protected void checkForReplication(final LogAccessFile logAccessFile) {
        if (this.inReplicationMasterMode) {
            logAccessFile.setReplicationMasterRole(this.masterFactory);
        }
        else if (this.inReplicationSlaveMode) {
            logAccessFile.setReplicationSlaveRole();
        }
    }
    
    public void initializeReplicationSlaveRole() throws StandardException {
        try {
            while (this.getLogFileAtBeginning(this.logFileNumber + 1L) != null) {
                ++this.logFileNumber;
            }
            final long logInstantAsLong = LogCounter.makeLogInstantAsLong(this.logFileNumber, 24L);
            long logRecordEnd = 24L;
            final StreamLogScan streamLogScan = (StreamLogScan)this.openForwardsScan(logInstantAsLong, null);
            while (streamLogScan.getNextRecord(new ArrayInputStream(), null, 0) != null) {
                logRecordEnd = streamLogScan.getLogRecordEnd();
            }
            this.setEndPosition(LogCounter.getLogFilePosition(logRecordEnd));
            StorageRandomAccessFile storageRandomAccessFile;
            if (this.isWriteSynced) {
                storageRandomAccessFile = this.openLogFileInWriteMode(this.getLogFileName(this.logFileNumber));
            }
            else {
                storageRandomAccessFile = this.privRandomAccessFile(this.getLogFileName(this.logFileNumber), "rw");
            }
            this.logOut = new LogAccessFile(this, storageRandomAccessFile, this.logBufferSize);
            this.lastFlush = this.endPosition;
            storageRandomAccessFile.seek(this.endPosition);
        }
        catch (IOException ex) {
            throw StandardException.newException("XRE03", ex);
        }
    }
    
    public void failoverSlave() {
        if (!this.stopped) {
            try {
                this.flushAll();
            }
            catch (StandardException ex) {}
        }
        this.inReplicationSlaveMode = false;
        synchronized (this.slaveRecoveryMonitor) {
            this.slaveRecoveryMonitor.notify();
        }
    }
    
    private boolean restoreLogs(final Properties properties) throws StandardException {
        boolean b = false;
        boolean b2 = false;
        String parent = properties.getProperty("createFrom");
        if (parent != null) {
            b = true;
        }
        else {
            parent = properties.getProperty("restoreFrom");
            if (parent != null) {
                b2 = true;
            }
            else {
                parent = properties.getProperty("rollForwardRecoveryFrom");
            }
        }
        if (parent == null) {
            return false;
        }
        if (!b && this.logDevice == null) {
            this.logDevice = properties.getProperty("derby.storage.logDeviceWhenBackedUp");
        }
        this.getLogStorageFactory();
        final StorageFile storageFile = this.logStorageFactory.newStorageFile("log");
        if (b2 && this.logDevice != null && !this.privRemoveDirectory(storageFile) && !this.privDelete(storageFile)) {
            throw StandardException.newException("XSDG7.D", this.getLogDirPath(storageFile));
        }
        if (b || b2) {
            this.createLogDirectory();
        }
        final File parent2 = new File(parent, "log");
        final String[] privList = this.privList(parent2);
        if (privList != null) {
            for (int i = 0; i < privList.length; ++i) {
                final File file = new File(parent2, privList[i]);
                final StorageFile storageFile2 = this.logStorageFactory.newStorageFile(storageFile, privList[i]);
                if (!this.privCopyFile(file, storageFile2)) {
                    throw StandardException.newException("XSLAR.D", file, storageFile2);
                }
            }
            return this.logSwitchRequired = true;
        }
        throw StandardException.newException("XSLAS.D", parent2);
    }
    
    private void preAllocateNewLogFile(final StorageRandomAccessFile storageRandomAccessFile) throws IOException, StandardException {
        final int n = this.logSwitchInterval - 24;
        final int n2 = this.logBufferSize * 2;
        final byte[] array = new byte[n2];
        int n3 = n / n2;
        final int n4 = n % n2;
        try {
            while (n3-- > 0) {
                storageRandomAccessFile.write(array);
            }
            if (n4 != 0) {
                storageRandomAccessFile.write(array, 0, n4);
            }
            this.syncFile(storageRandomAccessFile);
        }
        catch (IOException ex) {}
    }
    
    private StorageRandomAccessFile openLogFileInWriteMode(final StorageFile storageFile) throws IOException {
        if (!this.jvmSyncErrorChecked && this.checkJvmSyncError(storageFile)) {
            this.isWriteSynced = false;
            return this.privRandomAccessFile(storageFile, "rw");
        }
        return this.privRandomAccessFile(storageFile, "rwd");
    }
    
    private String getLogDirPath(final StorageFile storageFile) {
        if (this.logDevice == null) {
            return storageFile.toString();
        }
        return this.logDevice + this.logStorageFactory.getSeparator() + storageFile.toString();
    }
    
    private boolean checkJvmSyncError(final StorageFile storageFile) throws IOException {
        boolean b = false;
        this.privRandomAccessFile(storageFile, "rw").close();
        try {
            this.privRandomAccessFile(storageFile, "rws").close();
        }
        catch (FileNotFoundException ex) {
            this.logErrMsg("LogToFile.checkJvmSyncError: Your JVM seems to have a problem with implicit syncing of log files. Will use explicit syncing instead.");
            b = true;
        }
        this.jvmSyncErrorChecked = true;
        return b;
    }
    
    protected boolean privExists(final StorageFile storageFile) {
        return this.runBooleanAction(0, storageFile);
    }
    
    protected boolean privDelete(final StorageFile storageFile) {
        return this.runBooleanAction(1, storageFile);
    }
    
    private synchronized StorageRandomAccessFile privRandomAccessFile(final StorageFile activeFile, final String activePerms) throws IOException {
        this.action = 2;
        this.activeFile = activeFile;
        this.activePerms = activePerms;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<StorageRandomAccessFile>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private synchronized OutputStreamWriter privGetOutputStreamWriter(final StorageFile activeFile) throws IOException {
        this.action = 10;
        this.activeFile = activeFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<OutputStreamWriter>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    protected boolean privCanWrite(final StorageFile storageFile) {
        return this.runBooleanAction(3, storageFile);
    }
    
    protected boolean privMkdirs(final StorageFile storageFile) {
        return this.runBooleanAction(4, storageFile);
    }
    
    private synchronized String[] privList(final File toFile) {
        this.action = 8;
        this.toFile = toFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
    }
    
    private synchronized String[] privList(final StorageFile activeFile) {
        this.action = 5;
        this.activeFile = activeFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
    }
    
    private synchronized boolean privCopyFile(final StorageFile activeFile, final File toFile) throws StandardException {
        this.action = 6;
        this.activeFile = activeFile;
        this.toFile = toFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            if (ex.getCause() instanceof StandardException) {
                throw (StandardException)ex.getCause();
            }
            return false;
        }
    }
    
    private synchronized boolean privCopyFile(final File toFile, final StorageFile activeFile) {
        this.action = 9;
        this.activeFile = activeFile;
        this.toFile = toFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
    }
    
    private boolean privRemoveDirectory(final StorageFile storageFile) {
        return this.runBooleanAction(7, storageFile);
    }
    
    private synchronized boolean runBooleanAction(final int action, final StorageFile activeFile) {
        this.action = action;
        this.activeFile = activeFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
    }
    
    private void setEndPosition(final long endPosition) {
        this.endPosition = endPosition;
    }
    
    public final Object run() throws IOException, StandardException {
        switch (this.action) {
            case 0: {
                return ReuseFactory.getBoolean(this.activeFile.exists());
            }
            case 1: {
                return ReuseFactory.getBoolean(this.activeFile.delete());
            }
            case 2: {
                final boolean exists = this.activeFile.exists();
                final StorageRandomAccessFile randomAccessFile = this.activeFile.getRandomAccessFile(this.activePerms);
                if (!exists) {
                    this.activeFile.limitAccessToOwner();
                }
                return randomAccessFile;
            }
            case 3: {
                return ReuseFactory.getBoolean(this.activeFile.canWrite());
            }
            case 4: {
                final boolean mkdirs = this.activeFile.mkdirs();
                if (mkdirs) {
                    this.activeFile.limitAccessToOwner();
                }
                return ReuseFactory.getBoolean(mkdirs);
            }
            case 5: {
                return this.activeFile.list();
            }
            case 6: {
                return ReuseFactory.getBoolean(FileUtil.copyFile(this.logStorageFactory, this.activeFile, this.toFile));
            }
            case 7: {
                if (!this.activeFile.exists()) {
                    return ReuseFactory.getBoolean(true);
                }
                return ReuseFactory.getBoolean(this.activeFile.deleteAll());
            }
            case 8: {
                return this.toFile.list();
            }
            case 9: {
                return ReuseFactory.getBoolean(FileUtil.copyFile(this.logStorageFactory, this.toFile, this.activeFile));
            }
            case 10: {
                return new OutputStreamWriter(this.activeFile.getOutputStream(), "UTF8");
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        LogToFile.fid = 128;
        DBG_FLAG = null;
        DUMP_LOG_ONLY = null;
        DUMP_LOG_FROM_LOG_FILE = null;
        LogToFile.wasDBInDurabilityTestModeNoSync = false;
        TEST_LOG_SWITCH_LOG = null;
        TEST_LOG_INCOMPLETE_LOG_WRITE = null;
        TEST_LOG_PARTIAL_LOG_WRITE_NUM_BYTES = null;
        TEST_LOG_FULL = null;
        TEST_SWITCH_LOG_FAIL1 = null;
        TEST_SWITCH_LOG_FAIL2 = null;
        TEST_RECORD_TO_FILL_LOG = null;
        TEST_MAX_LOGFILE_NUMBER = null;
    }
}
