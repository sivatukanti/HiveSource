// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.daemon;

import org.apache.derby.iapi.types.RowLocation;
import java.io.Writer;
import java.io.PrintWriter;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.catalog.Statistics;
import org.apache.derby.catalog.types.StatisticsImpl;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.dictionary.StatisticsDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.ArrayList;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.services.daemon.IndexStatisticsDaemon;

public class IndexStatisticsDaemonImpl implements IndexStatisticsDaemon, Runnable
{
    private static final boolean AS_BACKGROUND_TASK = true;
    private static final boolean AS_EXPLICIT_TASK = false;
    private static final int MAX_QUEUE_LENGTH;
    private final HeaderPrintWriter logStream;
    private final boolean doLog;
    private final boolean doTrace;
    private final boolean traceToDerbyLog;
    private final boolean traceToStdOut;
    private boolean daemonDisabled;
    private final ContextManager ctxMgr;
    public final boolean skipDisposableStats;
    private LanguageConnectionContext daemonLCC;
    private final Database db;
    private final String dbOwner;
    private final String databaseName;
    private final ArrayList queue;
    private Thread runningThread;
    private int errorsConsecutive;
    private long errorsUnknown;
    private long errorsKnown;
    private long wuProcessed;
    private long wuScheduled;
    private long wuRejectedDup;
    private long wuRejectedFQ;
    private long wuRejectedOther;
    private final long timeOfCreation;
    private long runTime;
    private final StringBuffer tsb;
    
    public IndexStatisticsDaemonImpl(final HeaderPrintWriter logStream, final boolean b, final String s, final Database db, final String dbOwner, final String s2) {
        this.queue = new ArrayList(IndexStatisticsDaemonImpl.MAX_QUEUE_LENGTH);
        this.tsb = new StringBuffer();
        if (logStream == null) {
            throw new IllegalArgumentException("log stream cannot be null");
        }
        this.logStream = logStream;
        this.doLog = b;
        this.traceToDerbyLog = (s.equalsIgnoreCase("both") || s.equalsIgnoreCase("log"));
        this.traceToStdOut = (s.equalsIgnoreCase("both") || s.equalsIgnoreCase("stdout"));
        this.doTrace = (this.traceToDerbyLog || this.traceToStdOut);
        final boolean systemBoolean = PropertyUtil.getSystemBoolean("derby.storage.indexStats.debug.keepDisposableStats");
        this.skipDisposableStats = (this.dbAtLeast10_9(db) && !systemBoolean);
        this.db = db;
        this.dbOwner = dbOwner;
        this.databaseName = s2;
        this.ctxMgr = ContextService.getFactory().newContextManager();
        this.timeOfCreation = System.currentTimeMillis();
        this.trace(0, "created{log=" + b + ", traceLog=" + this.traceToDerbyLog + ", traceOut=" + this.traceToStdOut + ", createThreshold=" + TableDescriptor.ISTATS_CREATE_THRESHOLD + ", absdiffThreshold=" + TableDescriptor.ISTATS_ABSDIFF_THRESHOLD + ", lndiffThreshold=" + TableDescriptor.ISTATS_LNDIFF_THRESHOLD + ", queueLength=" + IndexStatisticsDaemonImpl.MAX_QUEUE_LENGTH + "}) -> " + s2);
    }
    
    private boolean dbAtLeast10_9(final Database database) {
        try {
            return database.getDataDictionary().checkVersion(210, null);
        }
        catch (StandardException ex) {
            return false;
        }
    }
    
    public void schedule(final TableDescriptor e) {
        final String indexStatsUpdateReason = e.getIndexStatsUpdateReason();
        synchronized (this.queue) {
            if (this.acceptWork(e)) {
                this.queue.add(e);
                ++this.wuScheduled;
                this.log(true, e, "update scheduled" + ((indexStatsUpdateReason == null) ? "" : (", reason=[" + indexStatsUpdateReason + "]")) + " (queueSize=" + this.queue.size() + ")");
                if (this.runningThread == null) {
                    (this.runningThread = Monitor.getMonitor().getDaemonThread(this, "index-stat-thread", false)).start();
                }
            }
        }
    }
    
    private boolean acceptWork(final TableDescriptor tableDescriptor) {
        boolean b = !this.daemonDisabled && this.queue.size() < IndexStatisticsDaemonImpl.MAX_QUEUE_LENGTH;
        if (b && !this.queue.isEmpty()) {
            final String name = tableDescriptor.getName();
            final String schemaName = tableDescriptor.getSchemaName();
            for (int i = 0; i < this.queue.size(); ++i) {
                if (((TableDescriptor)this.queue.get(i)).tableNameEquals(name, schemaName)) {
                    b = false;
                    break;
                }
            }
        }
        if (!b) {
            final String string = tableDescriptor.getQualifiedName() + " rejected, ";
            String s;
            if (this.daemonDisabled) {
                ++this.wuRejectedOther;
                s = string + "daemon disabled";
            }
            else if (this.queue.size() >= IndexStatisticsDaemonImpl.MAX_QUEUE_LENGTH) {
                ++this.wuRejectedFQ;
                s = string + "queue full";
            }
            else {
                ++this.wuRejectedDup;
                s = string + "duplicate";
            }
            this.trace(1, s);
        }
        return b;
    }
    
    private void generateStatistics(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor) throws StandardException {
        this.trace(1, "processing " + tableDescriptor.getQualifiedName());
        int n = 0;
        while (true) {
            try {
                this.updateIndexStatsMinion(languageConnectionContext, tableDescriptor, null, true);
            }
            catch (StandardException ex) {
                if (ex.isLockTimeout() && n == 0) {
                    this.trace(1, "locks unavailable, retrying");
                    n = 1;
                    languageConnectionContext.internalRollback();
                    sleep(1000L);
                    continue;
                }
                throw ex;
            }
            break;
        }
    }
    
    private boolean isShuttingDown() {
        synchronized (this.queue) {
            return this.daemonDisabled || this.daemonLCC == null || !this.daemonLCC.getDatabase().isActive();
        }
    }
    
    private void updateIndexStatsMinion(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor, ConglomerateDescriptor[] conglomerateDescriptors, final boolean b) throws StandardException {
        final boolean b2 = conglomerateDescriptors == null;
        if (conglomerateDescriptors == null) {
            conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        }
        final long[] array = new long[conglomerateDescriptors.length];
        final ExecIndexRow[] array2 = new ExecIndexRow[conglomerateDescriptors.length];
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final ConglomerateController openConglomerate = transactionExecute.openConglomerate(tableDescriptor.getHeapConglomerateId(), false, 0, 6, b ? 1 : 4);
        final UUID[] array3 = new UUID[conglomerateDescriptors.length];
        try {
            for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                if (!conglomerateDescriptors[i].isIndex()) {
                    array[i] = -1L;
                }
                else {
                    final IndexRowGenerator indexDescriptor = conglomerateDescriptors[i].getIndexDescriptor();
                    if (this.skipDisposableStats && indexDescriptor.isUnique() && indexDescriptor.numberOfOrderedColumns() == 1) {
                        array[i] = -1L;
                    }
                    else {
                        array[i] = conglomerateDescriptors[i].getConglomerateNumber();
                        array3[i] = conglomerateDescriptors[i].getUUID();
                        array2[i] = indexDescriptor.getNullIndexRow(tableDescriptor.getColumnDescriptorList(), openConglomerate.newRowLocationTemplate());
                    }
                }
            }
        }
        finally {
            openConglomerate.close();
        }
        if (b2) {
            final List statistics = tableDescriptor.getStatistics();
            final StatisticsDescriptor[] array4 = statistics.toArray(new StatisticsDescriptor[statistics.size()]);
            for (int j = 0; j < array4.length; ++j) {
                final UUID referenceID = array4[j].getReferenceID();
                boolean b3 = false;
                for (int k = 0; k < array.length; ++k) {
                    if (referenceID.equals(array3[k])) {
                        b3 = true;
                        break;
                    }
                }
                if (!b3) {
                    final String string = "dropping disposable statistics entry " + array4[j].getUUID() + " for index " + array4[j].getReferenceID() + " (cols=" + array4[j].getColumnCount() + ")";
                    this.logAlways(tableDescriptor, null, string);
                    this.trace(1, string + " on table " + array4[j].getTableUUID());
                    final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
                    if (!languageConnectionContext.dataDictionaryInWriteMode()) {
                        dataDictionary.startWriting(languageConnectionContext);
                    }
                    dataDictionary.dropStatisticsDescriptors(tableDescriptor.getUUID(), array4[j].getReferenceID(), transactionExecute);
                    if (b) {
                        languageConnectionContext.internalCommit(true);
                    }
                }
            }
        }
        final long[][] array5 = new long[array.length][3];
        int n = 0;
        for (int l = 0; l < array.length; ++l) {
            if (array[l] != -1L) {
                if (b && this.isShuttingDown()) {
                    break;
                }
                array5[n][0] = array[l];
                array5[n][1] = System.currentTimeMillis();
                final int n2 = array2[l].nColumns() - 1;
                final long[] array6 = new long[n2];
                final KeyComparator keyComparator = new KeyComparator(array2[l]);
                final GroupFetchScanController openGroupFetchScan = transactionExecute.openGroupFetchScan(array[l], false, 0, 6, 1, null, null, 0, null, null, 0);
                try {
                    boolean b4 = false;
                    int fetchRows;
                    while ((fetchRows = keyComparator.fetchRows(openGroupFetchScan)) > 0) {
                        if (b && this.isShuttingDown()) {
                            b4 = true;
                            break;
                        }
                        for (int n3 = 0; n3 < fetchRows; ++n3) {
                            final int compareWithPrevKey = keyComparator.compareWithPrevKey(n3);
                            if (compareWithPrevKey >= 0) {
                                for (int n4 = compareWithPrevKey; n4 < n2; ++n4) {
                                    final long[] array7 = array6;
                                    final int n5 = n4;
                                    ++array7[n5];
                                }
                            }
                        }
                    }
                    if (b4) {
                        break;
                    }
                    openGroupFetchScan.setEstimatedRowCount(keyComparator.getRowCount());
                }
                finally {
                    openGroupFetchScan.close();
                }
                array5[n++][2] = System.currentTimeMillis();
                int n6 = 0;
                while (true) {
                    try {
                        this.writeUpdatedStats(languageConnectionContext, tableDescriptor, array3[l], keyComparator.getRowCount(), array6, b);
                    }
                    catch (StandardException ex) {
                        ++n6;
                        if (ex.isLockTimeout() && n6 < 3) {
                            this.trace(2, "lock timeout when writing stats, retrying");
                            sleep(100 * n6);
                            continue;
                        }
                        throw ex;
                    }
                    break;
                }
            }
        }
        this.log(b, tableDescriptor, fmtScanTimes(array5));
    }
    
    private void writeUpdatedStats(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor, final UUID obj, final long lng, final long[] array, final boolean b) throws StandardException {
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        this.trace(1, "writing new stats (xid=" + transactionExecute.getTransactionIdString() + ")");
        final UUID uuid = tableDescriptor.getUUID();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final UUIDFactory uuidFactory = dataDictionary.getUUIDFactory();
        this.setHeapRowEstimate(transactionExecute, tableDescriptor.getHeapConglomerateId(), lng);
        if (!languageConnectionContext.dataDictionaryInWriteMode()) {
            dataDictionary.startWriting(languageConnectionContext);
        }
        dataDictionary.dropStatisticsDescriptors(uuid, obj, transactionExecute);
        int n = 0;
        if (lng == 0L) {
            this.trace(2, "empty table, no stats written");
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                dataDictionary.addDescriptor(new StatisticsDescriptor(dataDictionary, uuidFactory.createUUID(), obj, uuid, "I", new StatisticsImpl(lng, array[i]), i + 1), null, 14, true, transactionExecute);
            }
            final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(obj);
            this.log(b, tableDescriptor, "wrote stats for index " + ((conglomerateDescriptor == null) ? "n/a" : conglomerateDescriptor.getDescriptorName()) + " (" + obj + "): rows=" + lng + ", card=" + cardToStr(array));
            if (b && conglomerateDescriptor == null) {
                this.log(b, tableDescriptor, "rolled back index stats because index has been dropped");
                languageConnectionContext.internalRollback();
            }
            n = ((conglomerateDescriptor == null) ? 1 : 0);
        }
        if (n == 0) {
            this.invalidateStatements(languageConnectionContext, tableDescriptor, b);
        }
        if (b) {
            languageConnectionContext.internalCommit(true);
        }
    }
    
    private void invalidateStatements(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        int n = 0;
        while (true) {
            try {
                if (!languageConnectionContext.dataDictionaryInWriteMode()) {
                    dataDictionary.startWriting(languageConnectionContext);
                }
                dependencyManager.invalidateFor(tableDescriptor, 40, languageConnectionContext);
                this.trace(1, "invalidation completed");
            }
            catch (StandardException ex) {
                if (ex.isLockTimeout() && b && n < 3) {
                    if (++n > 1) {
                        this.trace(2, "releasing locks");
                        languageConnectionContext.internalRollback();
                    }
                    this.trace(2, "lock timeout when invalidating");
                    sleep(100 * (1 + n));
                    continue;
                }
                this.trace(1, "invalidation failed");
                throw ex;
            }
            break;
        }
    }
    
    private void setHeapRowEstimate(final TransactionController transactionController, final long n, final long estimatedRowCount) throws StandardException {
        final ScanController openScan = transactionController.openScan(n, false, 0, 6, 1, null, null, 0, null, null, 0);
        try {
            openScan.setEstimatedRowCount(estimatedRowCount);
        }
        finally {
            openScan.close();
        }
    }
    
    public void run() {
        final long currentTimeMillis = System.currentTimeMillis();
        ContextService factory = null;
        try {
            factory = ContextService.getFactory();
            factory.setCurrentContextManager(this.ctxMgr);
            this.processingLoop();
        }
        catch (ShutdownException ex) {
            this.trace(1, "swallowed shutdown exception: " + extractIstatInfo(ex));
            this.stop();
            this.ctxMgr.cleanupOnError(ex, this.db.isActive());
        }
        catch (RuntimeException ex2) {
            if (!this.isShuttingDown()) {
                this.log(true, null, ex2, "runtime exception during normal operation");
                throw ex2;
            }
            this.trace(1, "swallowed runtime exception during shutdown: " + extractIstatInfo(ex2));
        }
        finally {
            if (factory != null) {
                factory.resetCurrentContextManager(this.ctxMgr);
            }
            this.runTime += System.currentTimeMillis() - currentTimeMillis;
            this.trace(0, "worker thread exit");
        }
    }
    
    private void processingLoop() {
        if (this.daemonLCC == null) {
            try {
                (this.daemonLCC = this.db.setupConnection(this.ctxMgr, this.dbOwner, null, this.databaseName)).setIsolationLevel(1);
                this.daemonLCC.getTransactionExecute().setNoLockWait(true);
            }
            catch (StandardException ex) {
                this.log(true, null, ex, "failed to initialize index statistics updater");
                return;
            }
        }
        try {
            TransactionController transactionExecute = this.daemonLCC.getTransactionExecute();
            this.trace(0, "worker thread started (xid=" + transactionExecute.getTransactionIdString() + ")");
            TableDescriptor tableDescriptor = null;
            while (true) {
                synchronized (this.queue) {
                    if (this.daemonDisabled) {
                        try {
                            transactionExecute.destroy();
                        }
                        catch (ShutdownException ex6) {}
                        transactionExecute = null;
                        this.daemonLCC = null;
                        this.queue.clear();
                        this.trace(1, "daemon disabled");
                        break;
                    }
                    if (this.queue.isEmpty()) {
                        this.trace(1, "queue empty");
                        break;
                    }
                    tableDescriptor = this.queue.get(0);
                }
                try {
                    final long currentTimeMillis = System.currentTimeMillis();
                    this.generateStatistics(this.daemonLCC, tableDescriptor);
                    ++this.wuProcessed;
                    this.errorsConsecutive = 0;
                    this.log(true, tableDescriptor, "generation complete (" + (System.currentTimeMillis() - currentTimeMillis) + " ms)");
                }
                catch (StandardException ex2) {
                    ++this.errorsConsecutive;
                    if (!this.handleFatalErrors(this.ctxMgr, ex2)) {
                        if (!this.handleExpectedErrors(tableDescriptor, ex2)) {
                            this.handleUnexpectedErrors(tableDescriptor, ex2);
                        }
                        this.daemonLCC.internalRollback();
                    }
                }
                finally {
                    synchronized (this.queue) {
                        if (!this.queue.isEmpty()) {
                            this.queue.remove(0);
                        }
                    }
                    if (this.errorsConsecutive >= 50) {
                        this.log(true, null, new IllegalStateException("degraded state"), "shutting down daemon, " + this.errorsConsecutive + " consecutive errors seen");
                        this.stop();
                    }
                }
            }
        }
        catch (StandardException ex3) {
            this.log(true, null, ex3, "thread died");
            synchronized (this.queue) {
                this.runningThread = null;
            }
            if (this.daemonLCC != null && !this.daemonLCC.isTransactionPristine()) {
                this.log(true, null, "transaction not pristine - forcing rollback");
                try {
                    this.daemonLCC.internalRollback();
                }
                catch (StandardException ex4) {
                    this.log(true, null, ex4, "forced rollback failed");
                }
            }
        }
        finally {
            synchronized (this.queue) {
                this.runningThread = null;
            }
            if (this.daemonLCC != null && !this.daemonLCC.isTransactionPristine()) {
                this.log(true, null, "transaction not pristine - forcing rollback");
                try {
                    this.daemonLCC.internalRollback();
                }
                catch (StandardException ex5) {
                    this.log(true, null, ex5, "forced rollback failed");
                }
            }
        }
    }
    
    public void runExplicitly(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor, final ConglomerateDescriptor[] array, final String str) throws StandardException {
        this.updateIndexStatsMinion(languageConnectionContext, tableDescriptor, array, false);
        this.trace(0, "explicit run completed" + ((str != null) ? (" (" + str + "): ") : ": ") + tableDescriptor.getQualifiedName());
    }
    
    public void stop() {
        Thread runningThread = null;
        boolean b = false;
        synchronized (this.queue) {
            if (!this.daemonDisabled) {
                b = true;
                final StringBuffer sb = new StringBuffer(100);
                sb.append("stopping daemon, active=").append(this.runningThread != null).append(", work/age=").append(this.runTime).append('/').append(System.currentTimeMillis() - this.timeOfCreation).append(' ');
                this.appendRunStats(sb);
                this.log(true, null, sb.toString());
                if (this.runningThread == null && this.daemonLCC != null && !this.isShuttingDown()) {
                    try {
                        this.daemonLCC.getTransactionExecute().destroy();
                    }
                    catch (ShutdownException ex) {}
                    this.daemonLCC = null;
                }
                this.daemonDisabled = true;
                runningThread = this.runningThread;
                this.runningThread = null;
                this.queue.clear();
            }
        }
        if (runningThread != null) {
            while (true) {
                try {
                    runningThread.join();
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                    continue;
                }
                break;
            }
        }
        if (b) {
            this.ctxMgr.cleanupOnError(StandardException.normalClose(), false);
        }
    }
    
    private boolean handleFatalErrors(final ContextManager contextManager, final StandardException ex) {
        boolean b = false;
        if ("40XD1".equals(ex.getMessageId())) {
            b = true;
        }
        else if (this.isShuttingDown() || ex.getSeverity() >= 45000) {
            this.trace(1, "swallowed exception during shutdown: " + extractIstatInfo(ex));
            b = true;
            contextManager.cleanupOnError(ex, this.db.isActive());
        }
        if (b) {
            this.daemonLCC.getDataDictionary().disableIndexStatsRefresher();
        }
        return b;
    }
    
    private boolean handleExpectedErrors(final TableDescriptor tableDescriptor, final StandardException ex) {
        final String messageId = ex.getMessageId();
        if ("XSAI2.S".equals(messageId) || "XSCH1.S".equals(messageId) || "XSDG9.D".equals(messageId) || ex.isLockTimeout()) {
            ++this.errorsKnown;
            this.log(true, tableDescriptor, "generation aborted (reason: " + messageId + ") {" + extractIstatInfo(ex) + "}");
            return true;
        }
        return false;
    }
    
    private boolean handleUnexpectedErrors(final TableDescriptor tableDescriptor, final StandardException ex) {
        ++this.errorsUnknown;
        this.log(true, tableDescriptor, ex, "generation failed");
        return true;
    }
    
    private static void sleep(final long n) {
        try {
            Thread.sleep(n);
        }
        catch (InterruptedException ex) {
            InterruptStatus.setInterrupted();
        }
    }
    
    private static String fmtScanTimes(final long[][] array) {
        final StringBuffer sb = new StringBuffer("scan durations (");
        for (int n = 0; n < array.length && array[n][0] > 0L; ++n) {
            sb.append('c').append(array[n][0]).append('=');
            if (array[n][2] == 0L) {
                sb.append("ABORTED,");
            }
            else {
                sb.append(array[n][2] - array[n][1]).append("ms,");
            }
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return sb.toString();
    }
    
    private void log(final boolean b, final TableDescriptor tableDescriptor, final String s) {
        this.log(b, tableDescriptor, null, s);
    }
    
    private void log(final boolean b, final TableDescriptor tableDescriptor, final Throwable t, final String s) {
        if (b && (this.doLog || t != null)) {
            this.logAlways(tableDescriptor, t, s);
        }
    }
    
    private void logAlways(final TableDescriptor tableDescriptor, final Throwable t, final String str) {
        final String string = "{istat} " + ((tableDescriptor == null) ? "" : (tableDescriptor.getQualifiedName() + ": ")) + str;
        if (t != null) {
            final PrintWriter s = new PrintWriter(this.logStream.getPrintWriter(), false);
            s.print(this.logStream.getHeader().getHeader());
            s.println(string);
            t.printStackTrace(s);
            s.flush();
        }
        else {
            this.logStream.printlnWithHeader(string);
        }
    }
    
    private synchronized void trace(final int n, final String str) {
        if (this.doTrace) {
            this.tsb.setLength(0);
            this.tsb.append("{istat,trace@").append(this.hashCode()).append("} ");
            for (int i = 0; i < n; ++i) {
                this.tsb.append("    ");
            }
            this.tsb.append(str).append(' ');
            if (n == 0) {
                this.appendRunStats(this.tsb);
            }
            if (this.traceToDerbyLog && this.logStream != null) {
                this.logStream.printlnWithHeader(this.tsb.toString());
            }
            if (this.traceToStdOut) {
                System.out.println(this.tsb.toString());
            }
        }
    }
    
    private void appendRunStats(final StringBuffer sb) {
        sb.append("[q/p/s=").append(this.queue.size()).append('/').append(this.wuProcessed).append('/').append(this.wuScheduled).append(",err:k/u/c=").append(this.errorsKnown).append('/').append(this.errorsUnknown).append('/').append(this.errorsConsecutive).append(",rej:f/d/o=").append(this.wuRejectedFQ).append('/').append(this.wuRejectedDup).append('/').append(this.wuRejectedOther).append(']');
    }
    
    private static String cardToStr(final long[] array) {
        if (array.length == 1) {
            return "[" + Long.toString(array[0]) + "]";
        }
        final StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < array.length; ++i) {
            sb.append(array[i]).append(',');
        }
        sb.deleteCharAt(sb.length() - 1).append(']');
        return sb.toString();
    }
    
    private static String extractIstatInfo(final Throwable t) {
        final String name = IndexStatisticsDaemonImpl.class.getName();
        final StackTraceElement[] stackTrace = t.getStackTrace();
        String s = "<no stacktrace>";
        String string = "";
        int i = 0;
        while (i < stackTrace.length) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getClassName().startsWith(name)) {
                s = stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber();
                if (i > 0) {
                    final StackTraceElement stackTraceElement2 = stackTrace[i - 1];
                    s = s + " -> " + stackTraceElement2.getClassName() + "." + stackTraceElement2.getMethodName() + "#" + stackTraceElement2.getLineNumber();
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        if (t instanceof StandardException) {
            string = ", SQLSTate=" + ((StandardException)t).getSQLState();
        }
        return "<" + t.getClass() + ", msg=" + t.getMessage() + string + "> " + s;
    }
    
    static {
        MAX_QUEUE_LENGTH = PropertyUtil.getSystemInt("derby.storage.indexStats.debug.queueSize", 20);
    }
    
    private static class KeyComparator
    {
        private static final int FETCH_SIZE = 16;
        private final DataValueDescriptor[][] rowBufferArray;
        private DataValueDescriptor[] lastUniqueKey;
        private DataValueDescriptor[] curr;
        private DataValueDescriptor[] prev;
        private int rowsReadLastRead;
        private long numRows;
        
        public KeyComparator(final ExecIndexRow execIndexRow) {
            this.rowsReadLastRead = -1;
            (this.rowBufferArray = new DataValueDescriptor[16][])[0] = execIndexRow.getRowArray();
            this.lastUniqueKey = execIndexRow.getRowArrayClone();
        }
        
        public int fetchRows(final GroupFetchScanController groupFetchScanController) throws StandardException {
            if (this.rowsReadLastRead == 16) {
                this.curr = this.rowBufferArray[15];
                this.rowBufferArray[15] = this.lastUniqueKey;
                this.lastUniqueKey = this.curr;
            }
            return this.rowsReadLastRead = groupFetchScanController.fetchNextGroup(this.rowBufferArray, null);
        }
        
        public int compareWithPrevKey(final int i) throws StandardException {
            if (i > this.rowsReadLastRead) {
                throw new IllegalStateException("invalid access, rowsReadLastRead=" + this.rowsReadLastRead + ", index=" + i + ", numRows=" + this.numRows);
            }
            ++this.numRows;
            if (this.numRows == 1L) {
                return 0;
            }
            this.prev = ((i == 0) ? this.lastUniqueKey : this.rowBufferArray[i - 1]);
            this.curr = this.rowBufferArray[i];
            for (int j = 0; j < this.prev.length - 1; ++j) {
                if (this.prev[j].isNull() || this.prev[j].compare(this.curr[j]) != 0) {
                    return j;
                }
            }
            return -1;
        }
        
        public long getRowCount() {
            return this.numRows;
        }
    }
}
